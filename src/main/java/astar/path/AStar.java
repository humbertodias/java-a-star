package astar.path;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import astar.Player;
import astar.TileCell;
import astar.TileMap;

public abstract class AStar extends Thread implements PathFinder {

    private static final int MAX_STEP = 1000;

    private volatile boolean isShutDown = true;

    protected TileMap map;
    protected Player player;

    protected TileCell start;
    protected TileCell end;

    private int maxCellNum;
    protected List<TileCell> open;
    protected List<TileCell> closed;

    public void init(TileMap map, Player player, TileCell targetCell) {
        this.map = map;
        this.player = player;

        this.start = map.getStart(player);
        this.start.markStart();
        this.end = map.getEnd(targetCell);
        this.end.markEnd();

        //Hell home computer , do not add the following statement this sysout , when slowShowMove Interrupted ... for the next time it will not automatically find its way
        this.maxCellNum = map.getWidth() * map.getHeight();
        this.open = new ArrayList<>(maxCellNum);
        this.closed = new ArrayList<>(maxCellNum);
    }

    @Override
    public void run() {
        isShutDown = false;
        Path path = findPath();
        if (path == null) {
            return;
        }
        move(path);
        isShutDown = true;
    }

    @Override
    public Path findPath() {
        if (start.equals(end)) {
            return null;
        }

        start.show();
        open.add(start);

        boolean found = false;
        int step = 0;
        while (!isShutDown && !found && step < MAX_STEP) {
            found = stepFind();
            step++;
        }

        if (found) {
            return shortestPath(); //  Back shortest path
        } else {
            return null; // Back in Manhattan reachable node node shortest distance
        }
    }

    /**
     * Every step of searching pathfinding
     */
    protected abstract boolean stepFind();

    private void move(Path path) {
        for (TileCell cell : path.getNodes()) {
            while (!player.arrivedAt(cell)) {
                //System.out.println(player);
                if (isShutDown) {
                    return;
                }
                player.closeTo(cell);
                slowShowMove();
            }
            cell.notPath();
            cell.notEnd();
        }
        player.idle();
    }

    private void slowShowMove() {
        try {
            TimeUnit.MILLISECONDS.sleep(player.getFrameDelay());
        } catch (InterruptedException e) {
            System.out.println("slowShowMove Interrupted...");
            shutdown();
        }
    }

    /**
     * Slow display the search path , including the G value
     */
    protected void slowShowFindPath() {
        try {
            TimeUnit.MILLISECONDS.sleep(player.getFrameDelay() * 2);
        } catch (InterruptedException e) {
            System.out.println("slowShowFindPath Interrupted...");
            shutdown();
        }
    }

    private Path shortestPath() {
        System.out.println("Path Found");

        List<TileCell> nodes = new ArrayList<>(maxCellNum);
        nodes.add(end);
        if (start.equals(end)) {
            return new Path(nodes);
        }

        boolean finished = false;
        TileCell next;
        TileCell now = end;
        TileCell stop = start;
        while (!finished) {
            next = map.getLowestAdjacent(now);
            now = next;
            if (now.equals(stop)) {
                finished = true;
            } else {
                now.markPath();
                nodes.add(0, now);
            }
        }

        System.out.println("Done");
        return new Path(nodes);
    }

    public void shutdown() {
        isShutDown = true;
        interrupt();
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    public boolean isHasMap() {
        return map != null;
    }

    public TileMap getMap() {
        return map;
    }

    public boolean isEnd(TileCell targetCell) {
        return end.equals(map.getEnd(targetCell));
    }

}
