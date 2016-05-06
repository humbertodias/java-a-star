package astar.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import astar.TileCell;

/**
 * Older and inefficient A * algorithm to find every pathfinding steps are to remove all open traverse the list while also adding more nodes to open the list until you find the target end node
 *
 */
public class OldAStar extends AStar {

    @Override
    protected boolean stepFind() {
        List<TileCell> cloneOpen = new ArrayList<>(open);
        Iterator<TileCell> openIt = cloneOpen.iterator();
        while (openIt.hasNext()) {
            TileCell cell = openIt.next();
            TileCell[] adjacents = map.getAdjacents(cell);
            for (TileCell adjacent : adjacents) {
                if (adjacent == null) {
                    continue;
                }

                if (adjacent.equals(end)) {
                    return true;
                } else if (!closed.contains(adjacent) && !open.contains(adjacent)) {
                    adjacent.addG(cell);
                    adjacent.show();
                    open.add(adjacent);
                }
            }

            closed.add(cell);
            openIt.remove();
        }

        slowShowFindPath();
        return false;
    }

}
