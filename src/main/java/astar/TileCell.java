package astar;

import java.awt.Point;
import java.awt.Rectangle;

public class TileCell {

    public static final int WIDTH = 50;
    public static final int HEIGHT = 48;

    public static final double NORMAL = 1, EASY = 0.3, TOUGH = 5, VERY_TOUGH = 10, BLOCK = Double.MAX_VALUE;

    /**
     * The actual screen coordinates
     */
    private final Point point;

    /**
     * Map grid coordinates , that is two-dimensional array subscript map
     */
    private final Point cellPoint;

    private double cost;
    private double g = -1;
    private double h;

    private boolean isEnd;
    private boolean isShow;
    private boolean isPath;

    public TileCell(int x, int y) {
        this(x, y, NORMAL);
    }

    public TileCell(int x, int y, double cost) {
        this.point = new Point(x, y);
        this.cellPoint = transformCellPoint(point);
        this.cost = cost;
    }

    public boolean isCanPass() {
        return cost != BLOCK;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getF() {
        return g + h;
    }

    public double getCost() {
        return cost;
    }

    public Double getG() {
        return g > 9 ? Math.round(g * 10) / 10.0 : Math.round(g * 100) / 100.0;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public Point getPosition() {
        return point;
    }

    public Point getCellPosition() {
        return cellPoint;
    }

    public static Point transformCellPoint(Point point) {
        return new Point((int) point.x / WIDTH, (int) point.y / HEIGHT);
    }

    /**
     * Rounding to get near the box to represent a starting point, not rounded ,
     * then the starting point for selection bias in the box when the
     * lower-right corner to go back , but this problem is not added into the
     * starting path when you add the path to be resolved
     *
     * @param point
     * @return
     */
    public static Point transformCellPointRound(Point point) {
        return new Point(((int) Math.round(1.0 * point.x / WIDTH)), (int) Math.round(1.0 * point.y / HEIGHT));
    }

    public int getX() {
        return (int) point.getX();
    }

    public int getY() {
        return (int) point.getY();
    }

    /**
     * It is the start / Start block
     *
     * @return
     */
    public boolean isStart() {
        return g == 0;
    }

    /**
     * Is the goal / end box
     *
     * @return
     */
    public boolean isEnd() {
        return isEnd;
    }

    public void notEnd() {
        isEnd = false;
    }

    /**
     * Whether to display the value of G
     *
     * @return
     */
    public boolean isShow() {
        return isShow;
    }

    /**
     * 显示G值
     */
    public void show() {
        this.isShow = true;
    }

    public void notPath() {
        this.isPath = false;
    }

    /**
     * Whether to show the path
     *
     * @return
     */
    public boolean isPath() {
        return isPath;
    }

    public void markStart() {
        g = 0;
    }

    public void markEnd() {
        isEnd = true;
    }

    public void markPath() {
        isPath = true;
    }

    /**
     * This map is set to the beginning of the plate distance value
     *
     * @param cell
     */
    public void addG(TileCell cell) {
        double cellG = cell.getG();

        if (g == -1) {
            g = cellG + cost;
            return;
        }
        if (cellG + cost < g) { //Minimum distance value
            g = cellG + cost;
        }
    }

    public Rectangle getRectangle() {
        return new Rectangle(getX(), getY(), WIDTH, HEIGHT);
    }

    @Override
    public String toString() {
        return "(x=" + getX() + ", y=" + getY() + ")" + " [x=" + (int) cellPoint.getX() + ", y=" + (int) cellPoint.getY() + "]" + " {cost=" + cost + ", g=" + getG() + ", h=" + h + ", f=" + getF() + "}";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((point == null) ? 0 : point.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TileCell other = (TileCell) obj;
        if (point == null) {
            if (other.point != null) {
                return false;
            }
        } else if (!point.equals(other.point)) {
            return false;
        }
        return true;
    }

    public void reset() {
        this.cost = NORMAL;
        this.g = -1;
    }
}
