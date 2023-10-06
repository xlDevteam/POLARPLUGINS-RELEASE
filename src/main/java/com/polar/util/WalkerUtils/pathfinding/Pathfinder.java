package com.polar.util.WalkerUtils.pathfinding;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.coords.WorldPoint;

import java.util.*;

@Slf4j
public class Pathfinder {
    private final CollisionMap map;
    private final Node start;
    private final WorldPoint target;
    private final List<Node> boundary = new LinkedList<>();
    private final Set<WorldPoint> visited = new HashSet<>();
    private final HashMap<WorldPoint, List<Transport>> transports;
    private Node nearest;

    public Pathfinder(CollisionMap map, HashMap<WorldPoint, List<Transport>> transports, WorldPoint start, WorldPoint target) {
        this.map = map;
        this.transports = transports;
        this.target = target;
        this.start = new Node(start, null);
        nearest = null;
    }

    public List<WorldPoint> find() {
        long startTime = System.currentTimeMillis(); // Record the start time

        boundary.add(start);

        int bestDistance = Integer.MAX_VALUE;

        while (!boundary.isEmpty()) {
            Node node = boundary.remove(0);

            if (node.position.equals(target)) {
                long endTime = System.currentTimeMillis(); // Record the end time
                long elapsedTime = endTime - startTime; // Calculate elapsed time
                log.info("Path found in {}", elapsedTime + " milliseconds");
                return node.path();
            }

            int distance = Math.max(Math.abs(node.position.getX() - target.getX()), Math.abs(node.position.getY() - target.getY()));
            if (nearest == null || distance < bestDistance) {
                nearest = node;
                bestDistance = distance;
            }

            addNeighbors(node);
        }

        if (nearest != null) {
            long endTime = System.currentTimeMillis(); // Record the end time
            long elapsedTime = endTime - startTime; // Calculate elapsed time
            log.info("No path found, nearest point found in {}", elapsedTime + " milliseconds");
            return nearest.path();
        }

        long endTime = System.currentTimeMillis(); // Record the end time
        long elapsedTime = endTime - startTime; // Calculate elapsed time
        log.info("No path found. Search time: {}", + elapsedTime + " milliseconds");

        return null;
    }

    private void addNeighbors(Node node) {
        if (map.w(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() - 1, node.position.getY(), node.position.getPlane()));
        }

        if (map.e(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() + 1, node.position.getY(), node.position.getPlane()));
        }

        if (map.s(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX(), node.position.getY() - 1, node.position.getPlane()));
        }

        if (map.n(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX(), node.position.getY() + 1, node.position.getPlane()));
        }

        if (map.sw(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() - 1, node.position.getY() - 1, node.position.getPlane()));
        }

        if (map.se(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() + 1, node.position.getY() - 1, node.position.getPlane()));
        }

        if (map.nw(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() - 1, node.position.getY() + 1, node.position.getPlane()));
        }

        if (map.ne(node.position.getX(), node.position.getY(), node.position.getPlane())) {
            addNeighbor(node, new WorldPoint(node.position.getX() + 1, node.position.getY() + 1, node.position.getPlane()));
        }

        for (Transport transport : transports.getOrDefault(node.position, new ArrayList<>())) {
            addNeighbor(node, transport.getDestination());
        }
    }

    public List<WorldPoint> currentBest() {
        return nearest==null ? null : nearest.path();
    }

    private void addNeighbor(Node node, WorldPoint neighbor) {

        if (!visited.add(neighbor)) {
            return;
        }

        boundary.add(new Node(neighbor, node));
    }

    private static class Node {
        public final WorldPoint position;
        public final Node previous;

        public Node(WorldPoint position, Node previous) {
            this.position = position;
            this.previous = previous;
        }

        public List<WorldPoint> path() {
            List<WorldPoint> path = new LinkedList<>();
            Node node = this;

            while (node != null) {
                path.add(0, node.position);
                node = node.previous;
            }

            return new ArrayList<>(path);
        }
    }
}