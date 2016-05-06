package astar.path;

import java.awt.Point;
import java.util.Iterator;

import astar.TileCell;

/**
 * Classic * A * algorithm .
 * <br/> 1, first pathfinding Find (findPath) before , first find the minimum
 * cost of the whole map as the base to calculate the distance of Manhattan ;
 * <br/> 2, each step before traversing around the map section, choose the best
 * path to open a list of preferred inside as the minimum Manhattan distance ;
 * <br/> 3, the node has been set aside wayfinding open list and placed close
 * list ;
 * <br/> 4, adjacent to each traverse through the plate , and set it to start
 * distance value (cost), and the node is not added to the list open close list
 * ;
 * <br/> 5, repeat step 2 until you find the path or when the finished plate
 * after traversing all maps (open list is empty ) found that there is no path .
 * *
 */
public class ClassicAStar extends OldAStar {

    private double minCost;

    @Override
    public Path findPath() {
        initMinCost();
        return super.findPath();
    }

    private void initMinCost() {
        minCost = Double.MAX_VALUE;

        for (TileCell[] columns : map.getCells()) {
            for (TileCell row : columns) {
                minCost = Math.min(row.getCost(), minCost);
            }
        }
    }

    @Override
    protected boolean stepFind() {
        TileCell minFCell = findMinFCellFromOpen();
        TileCell[] adjacents = map.getAdjacents(minFCell);
        for (TileCell adjacent : adjacents) {
            if (adjacent == null) {
                continue;
            }

            if (adjacent.equals(end)) {
                return true;
            } else if (!closed.contains(adjacent) && !open.contains(adjacent)) {
                adjacent.addG(minFCell);
                adjacent.show();
                open.add(adjacent);
            }
        }

        closed.add(minFCell);
        open.remove(minFCell);

        slowShowFindPath();
        return false;
    }

    /**
     * Optimal preferred path selection list open inside the Manhattan distance
     * as the smallest
     */
    private TileCell findMinFCellFromOpen() {
        double minF = Double.MAX_VALUE;
        TileCell minFCell = open.get(open.size() - 1);
        Iterator<TileCell> openIt = open.iterator();
        while (openIt.hasNext()) {
            TileCell cell = openIt.next();
            cell.setH(manhattanDistance(cell, end, minCost));
            if (cell.getF() < minF) {
                minF = cell.getF();
                minFCell = cell;
            }
        }
        return minFCell;
    }

    /**
     * From from to to calculate weighted Manhattan distance
     * @param from
     * @param to
     * @param low
     * @return 
     */
    protected double manhattanDistance(TileCell from, TileCell to, double low) {
        Point a = from.getPosition();
        Point b = to.getPosition();
        return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
    }

}
