package astar.path;

import java.util.List;

import astar.TileCell;

public class Path {

    private final List<TileCell> nodes;

    public Path(List<TileCell> nodes) {
        this.nodes = nodes;
    }

    public List<TileCell> getNodes() {
        return nodes;
    }

}
