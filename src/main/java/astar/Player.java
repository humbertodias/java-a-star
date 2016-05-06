package astar;

import java.awt.Point;
import java.awt.Rectangle;

import astar.animation.Animation;
import astar.path.AStar;
import astar.path.FudgeAStar;

public class Player extends Animation {

    private static final int SPEED = 5;

    /**
     * Player can not let each have their own separate A star pathfinding
     * mission and then return , because the use of Executor when each get the
     * same A star Runnable, Cancel time, will lead to the original is the
     * pathfinding or mobile only to interrupt the Runnable
     */
    private final int id;

    public Player(int id, int x, int y, String imgPath){
        super(TileCell.WIDTH,TileCell.HEIGHT, x, y, imgPath);
        this.id = id;
    }
    
    public Player(int id, int width, int height, int x, int y, String imgPath) {
        super(width, height, x, y, imgPath);
        this.id = id;
    }

    public void moveUp() {
        int oldY = y;
        y = Math.max(y - SPEED, 0);
        changeDirection(x, oldY, x, y);
    }

    public void moveDown(int height) {
        int oldY = y;
        y = Math.min(y + SPEED, height - TileCell.HEIGHT - 1);
        changeDirection(x, oldY, x, y);
    }

    public void moveLeft() {
        int oldX = x;
        x = Math.max(x - SPEED, 0);
        changeDirection(oldX, y, x, y);
    }

    public void moveRight(int width) {
        int oldX = x;
        x = Math.min(x + SPEED, width - TileCell.WIDTH - 1);
        changeDirection(oldX, y, x, y);
    }

    public void move(TileCell cell) {
        Point p = cell.getPosition();
        x = (int) p.getX();
        y = (int) p.getY();
        cell.notPath();
    }

    public boolean arrivedAt(TileCell cell) {
        return x == cell.getX() && y == cell.getY();
    }

    public void closeTo(TileCell cell) {
        int oldX = x;
        int oldY = y;

        if (x > cell.getX()) {
            x--;
        } else if (x < cell.getX()) {
            x++;
        }

        if (y > cell.getY()) {
            y--;
        } else if (y < cell.getY()) {
            y++;
        }

        changeDirection(oldX, oldY, x, y);
    }

    @Override
    public String toString() {
        return "Player (" + x + ", " + y + ")";
    }

    public AStar newFindPathTask(TileMap map, TileCell targetCell) {
        AStar astart = new FudgeAStar();
        astart.init(map.clone(), this, targetCell);
        return astart;
    }

    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        Player other = (Player) obj;
        return id == other.id;
    }

    public boolean isCollision(Rectangle drawRouseDraggedRect) {
        return new Rectangle(x, y, width, height).intersects(drawRouseDraggedRect);
    }

}
