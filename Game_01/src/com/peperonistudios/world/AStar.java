package com.peperonistudios.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AStar {

    public static double LastTime = System.currentTimeMillis();

    private static Comparator<Node> nodeSorter = new Comparator<Node>() {
        @Override
        public int compare(Node n0, Node n1) {
            if (n1.fCost < n0.fCost) return +1;
            if (n1.fCost > n0.fCost) return -1;
            return 0;
        }
    };

    public static boolean clear() {
        return (System.currentTimeMillis() - LastTime >= 1000);
    }

    private static double calculateDistance(Vector2i tile, Vector2i goal) {
        double dx = tile.x - goal.x;
        double dy = tile.y - goal.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static Node getNodeInList(List<Node> list, Vector2i vector) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).tile.equals(vector)) return list.get(i);
        }
        return null;
    }

    private static boolean isWall(int x, int y) {
        // Checa limites do mapa para não estourar exceção nem ler memória errada
        if (x < 0 || y < 0 || x >= World.WIDTH || y >= World.HEIGHT) return true;
        
        Tile tile = World.tiles[x + (y * World.WIDTH)];
        return (tile == null || tile instanceof WallTile);
    }

    public static List<Node> findPath(World world, Vector2i start, Vector2i end) {
        LastTime = System.currentTimeMillis();
        List<Node> openList = new ArrayList<Node>();
        List<Node> closedList = new ArrayList<Node>();

        Node current = new Node(start, null, 0, calculateDistance(start, end));
        openList.add(current);

        while (!openList.isEmpty()) {
            Collections.sort(openList, nodeSorter);
            current = openList.get(0);

            if (current.tile.equals(end)) {
                List<Node> path = new ArrayList<Node>();
                while (current.parent != null) {
                    path.add(current);
                    current = current.parent;
                }
                openList.clear();
                closedList.clear();
                return path;
            }

            openList.remove(current);
            closedList.add(current);

            for (int i = 0; i < 9; i++) {
                if (i == 4) continue; // Pula o próprio nó central

                int x = current.tile.x;
                int y = current.tile.y;
                int xi = (i % 3) - 1;
                int yi = (i / 3) - 1;

                int targetX = x + xi;
                int targetY = y + yi;

                // 1. Se o próprio tile alvo for parece, ignora
                if (isWall(targetX, targetY)) continue;

                // 2. Bloqueio de corte de quina na diagonal
                if (xi != 0 && yi != 0) {
                    // Se qualquer uma das paredes adjacentes for WallTile, impede o movimento diagonal
                    if (isWall(x + xi, y) || isWall(x, y + yi)) {
                        continue;
                    }
                }

                Vector2i a = new Vector2i(targetX, targetY);
                double gCost = current.gCost + calculateDistance(current.tile, a);
                double hCost = calculateDistance(a, end);

                Node existingClosed = getNodeInList(closedList, a);
                if (existingClosed != null && gCost >= existingClosed.gCost) continue;

                Node existingOpen = getNodeInList(openList, a);
                if (existingOpen == null) {
                    Node node = new Node(a, current, gCost, hCost);
                    openList.add(node);
                } else if (gCost < existingOpen.gCost) {
                    // Atualiza o nó existente com o novo caminho mais curto
                    existingOpen.parent = current;
                    existingOpen.gCost = gCost;
                    existingOpen.fCost = gCost + hCost;
                }
            }
        }
        
        closedList.clear();
        return null;
    }
}
