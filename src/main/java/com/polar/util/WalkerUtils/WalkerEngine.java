package com.polar.util.WalkerUtils;

import com.ethan.Packets.MovementPackets;
import com.polar.util.PolarUtilsPlugin;
import com.polar.util.WalkerUtils.Locations.BankLocations;
import com.polar.util.WalkerUtils.pathfinding.CollisionMap;
import com.polar.util.WalkerUtils.pathfinding.Pathfinder;
import com.polar.util.WalkerUtils.pathfinding.SplitFlagMap;
import com.polar.util.WalkerUtils.pathfinding.Transport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.RuneLite;

import javax.swing.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class WalkerEngine {

    private final Client client = RuneLite.getInjector().getInstance(Client.class);

    @Getter
    private final CollisionMap map;

    @Getter
    private Pathfinder pathfinder;

    @Getter
    private List<WorldPoint> path;

    @Getter
    private HashMap<WorldPoint, List<Transport>> transports;

    @Getter
    private Transport transport;

    public WalkerEngine() {

        long initializationStartTime = System.currentTimeMillis();

        map = loadCollisionMap();
        transports = Transport.loadAllFromResources();

        long initializationEndTime = System.currentTimeMillis();
        long initializationTime = initializationEndTime - initializationStartTime;
        log.info("WalkerEngine initialization took {}ms.", initializationTime);

    }

    public void walk(WorldPoint destination) {

        Random random = new Random();

        int spacing = 10;

        path = generatePathFinderPath(destination);

        if (path.isEmpty() && playerIsAt(destination)) {
            log.info("Arrived at destination: {}", destination);
            return;
        }

        log.info("Walking to destination: {}", destination);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < path.size(); i += (spacing + random.nextInt(10 + 1)) + 1) {
                    WorldPoint currentWaypoint = path.get(i);
                    if (!playerIsAt(currentWaypoint)) {
                        PolarUtilsPlugin.enableRun();
                        MovementPackets.queueMovement(currentWaypoint);
                        while (!playerIsAt(currentWaypoint)) {
                            Util.sleep(2000);
                        }
                    }
                }

                // Ensure the final waypoint is reached
                WorldPoint finalWaypoint = path.get(path.size() - 1);
                if (!playerIsAt(finalWaypoint)) {
                    PolarUtilsPlugin.enableRun();
                    MovementPackets.queueMovement(finalWaypoint);
                    while (!playerIsAt(finalWaypoint)) {
                        Util.sleep(2000);
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                log.info("Arrived at destination: {}", destination);
                path = null;
            }
        };

        worker.execute();
    }
    public void walkPath(WorldPoint[] worldPoints) {

        if (worldPoints == null || worldPoints.length == 0) {
            log.warn("No destination points provided.");
            return;
        }

        List<WorldPoint> path = new ArrayList<>(List.of(worldPoints));

        if (path.isEmpty()) {
            log.info("Unable to create path for the given destinations.");
            return;
        }

        WorldPoint destination = worldPoints[worldPoints.length - 1]; // Use the last destination as the final destination

        log.info("Walking to destination: {}", destination);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (WorldPoint waypoint : path) {
                    if (!playerIsAt(waypoint)) {
                        MovementPackets.queueMovement(waypoint);
                        while (!playerIsAt(waypoint)) {
                                Util.sleep(2000);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                log.info("Arrived at destination: {}", destination);
            }
        };

        worker.execute();
    }
    public void walkNearestBank() {
        WorldPoint destination = findClosestBank();

        walk(destination);

    }

    private List<WorldPoint> generatePathFinderPath(WorldPoint destination) {
        // Create an instance of Pathfinder with your collision map and other parameters
        pathfinder = new Pathfinder(map, transports, client.getLocalPlayer().getWorldLocation(), destination);
        return pathfinder.find();
    }
    private CollisionMap loadCollisionMap() {
        Map<SplitFlagMap.Position, byte[]> compressedRegions = new HashMap<>();

        // Provide the correct resource path with a leading slash ("/")
        try (ZipInputStream in = new ZipInputStream(Objects.requireNonNull(WalkerEngine.class.getResourceAsStream("collision-map.zip")))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                String[] n = entry.getName().split("_");
                compressedRegions.put(
                        new SplitFlagMap.Position(Integer.parseInt(n[0]), Integer.parseInt(n[1])),
                        Util.readAllBytes(in)
                );
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        log.info("Loaded Collision Map!");
        return new CollisionMap(64, compressedRegions);
    }

    private double calculateDistance(WorldPoint point1, WorldPoint point2) {
        return point1.distanceTo(point2);
    }
    public WorldPoint findClosestBank() {

        List<WorldPoint> banks = BankLocations.reachableBanksNoDoors;

        WorldPoint closestBank = null;
        double shortestDistance = Double.MAX_VALUE;

        for (WorldPoint bankLocation : banks) {
            double distance = calculateDistance(client.getLocalPlayer().getWorldLocation(), bankLocation);

            if (distance < shortestDistance) {
                shortestDistance = distance;

                closestBank = bankLocation;
            }
        }

        return closestBank;
    }
    private boolean playerIsAt(WorldPoint point) {
        return client.getLocalPlayer().getWorldLocation().distanceTo(point) <= 6;  // Adjust the threshold as needed
    }

}