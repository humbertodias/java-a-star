package astar;

import astar.animation.Animation;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import astar.path.AStar;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.text.DecimalFormat;

public class GamePanel extends JPanel implements MouseInputListener, Runnable {

    private static final long serialVersionUID = 5898969301038642894L;

    private static final Random random = new Random();

    public Image offScreenImage; // Games for drawing an image
    private Thread gameLoop;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private TileMap map;
    private Set<Player> players;
    private Player player1;
    private Player player2;

    private final Set<Player> actors = new HashSet<>();
    private Point mousePressedPoint;
    private final Rectangle drawRouseDraggedRect = new Rectangle();

    private final Map<Integer, Future<?>> findPathFutureMap = new HashMap<>();
    private final Map<Integer, AStar> findPathTaskMap = new HashMap<>();

    public GamePanel() {
        init();
        addKeyListener(getKeyListener());
        addMouseListener(this);
        addMouseMotionListener(this);
        start();
    }

    private KeyListener getKeyListener() {
        return new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        player1.moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        player1.moveDown(getHeight());
                        break;
                    case KeyEvent.VK_LEFT:
                        player1.moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        player1.moveRight(getWidth());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                player1.idle();
            }

        };
    }

    public void initGUI() {
        // Set the mainPanel (JPanel subclass ) layout is empty, so before using the coordinates (Component of setBounds () method ) to set up their own arrangements for assembly position
        setLayout( new FlowLayout() );
        // Component of setBounds () method to move and resize the component . Specified by the x and y position of the upper left corner of the new , by the width and height
        // Specify the new size .
//        setBounds(0, 0, Main.WIN_WIDTH, Main.WIN_HEIGHT);
        setPreferredSize( new Dimension( Main.WIN_WIDTH, Main.WIN_HEIGHT) );
        // setBackground(new Color(128, 64, 0)); // Set the background color
        setFocusable(true); // Can be obtained initially focus Sketchpad

        firstSelection();
        
    }

    private void init() {
        map = new TileMap();

        player1 = new Player(1, 0, 0, "player1.png");
        player2 = new Player(2, TileCell.WIDTH * 7, TileCell.HEIGHT * 7, "player2.png");
        players = new HashSet<>();
        players.add(player1);
        players.add(player2);
    }

    private void firstSelection() {
        TileCell targetCell = map.getCell(0, 0);
        recollectActors(targetCell);
    }

    private void start() {
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        while (currentThread == gameLoop) {
            updateGame();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    private void updateGame() {
        updatePlayers();
    }

    private void updatePlayers() {
        for (Player currPlayer : players) {
            currPlayer.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        if (offScreenImage == null) {
            // Component of createImage () method is used to create a double buffering can be drawn off-screen image . This parameter specifies the width and height
            offScreenImage = createImage(getWidth(), getHeight());
        }
        // Image of getGraphics () method to create an image to use for drawing to an off-screen graphics context
        drawGame(offScreenImage.getGraphics());
        // Graphics of drawImage () method to draw the image specified in the currently available images.
        // The first parameter indicates the specified image to be drawn , and the second and third parameter indicates the specified image of x, y coordinates , the fourth parameter represents the object when converting more of the image to be notified .
        g.drawImage(offScreenImage, 0, 0, this);
    }

    private void drawGame(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        TileMap map = actors.isEmpty() ? this.map : actorsMap();
//        drawBackground(g2d);
        drawMap(g2d, map);
        drawPlayers(g2d, map);
        drawMouseDragged(g2d);
        drawActors(g2d);

    }

    private void drawBackground(Graphics2D g) {
//        Animation bg = new Animation(512, 512, 0, 0, "link-house.png");
//        Animation bg = new Animation(659,512,0,0, "SNES - The Legend of Zelda A Link to the Past - Library Area.png");
        Animation bg = new Animation(0,0, "hyrule.png");
        bg.draw(g);
    }

    private TileMap actorsMap() {
        Player player = actors.iterator().next();
        AStar astart = findPathTaskMap.get(player.getId());
        return astart == null ? this.map : astart.getMap();
    }

    private void drawMap(Graphics2D g2d, TileMap map) {
        for (TileCell[] columns : map.getCells()) {
            for (TileCell cell : columns) {
                if (cell.isStart()) {
                    drawCellWithColor(g2d, cell, Color.ORANGE);
                }
                if (cell.isEnd()) {
                    drawCellWithColor(g2d, cell, Color.GREEN);
                } else if (cell.isPath()) {
                    if (!cell.isStart()) {
                        drawCellWithColor(g2d, cell, Color.YELLOW);
                    }
                    drawCellG(g2d, cell);
                } else if (!cell.isCanPass()) {
//                    Color c = randomColor();
                    drawCellWithColor(g2d, cell, Color.DARK_GRAY);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(cell.getX(), cell.getY(), TileCell.WIDTH, TileCell.HEIGHT);
                    drawCellG(g2d, cell);
                }
            }
        }
    }

    private void drawCellWithColor(Graphics2D g2d, TileCell cell, Color color) {
        g2d.setColor(color);
        g2d.fill(new Rectangle(cell.getX() + 2, cell.getY() + 2, TileCell.WIDTH - 3, TileCell.HEIGHT - 3));
        g2d.setColor(Color.BLACK);
        g2d.drawRect(cell.getX(), cell.getY(), TileCell.WIDTH, TileCell.HEIGHT);
    }

    String str(Double n) {
        DecimalFormat df = new DecimalFormat("0");
        return df.format(n);
    }

    private void drawCellG(Graphics2D g2d, TileCell cell) {
        if (!cell.isShow() || cell.getG() < 0) {
            return;
        }
        g2d.drawString(str(cell.getG()), (cell.getX()), (cell.getY() + TileCell.WIDTH / 2));
    }

    private void drawPlayers(Graphics2D g2d, TileMap map) {
//		g2d.setColor(Color.LIGHT_GRAY);
//		g2d.fill(new Rectangle(player.getX(), player.getY(), TileCell.WIDTH, TileCell.HEIGHT));
//		g2d.setColor(Color.BLACK);
//		g2d.drawRect(player.getX(), player.getY(), TileCell.WIDTH, TileCell.HEIGHT);

        for (Player currPlayer : players) {
            currPlayer.draw(g2d);
        }
    }

    private void drawMouseDragged(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.drawRect(drawRouseDraggedRect.x, drawRouseDraggedRect.y,
                drawRouseDraggedRect.width, drawRouseDraggedRect.height);
    }

    private void drawActors(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        for (Player actor : actors) {
            g2d.drawRect(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
        }
    }
    
    public Color randomColor(){
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return new Color(red, green, blue);
    }

    private void randomDraw(Graphics2D g2d) {
        int w = random.nextInt(100);
        int h = random.nextInt(100);
        int x = random.nextInt(getSize().width - w);
        int y = random.nextInt(getSize().height - h);
        Rectangle rectangle = new Rectangle(x, y, w, h);

        g2d.setColor(randomColor());
        g2d.fill(rectangle);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // System.out.println("mouseClicked:"+e.getX()+", "+e.getY());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // System.out.println("mouseEntered:"+e.getX()+", "+e.getY());
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // System.out.println("mouseExited:"+e.getX()+", "+e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressedPoint = e.getPoint();

        TileCell targetCell = map.getCell(TileCell.transformCellPoint(e.getPoint()));
        if (targetCell == null || !targetCell.isCanPass()) {
            return;
        }

        if (isMouseLeftKey(e)) {
            recollectActors(targetCell);
            return;
        }

        actorsCancelTaskAndSubmitNew(targetCell);

    }

    private void recollectActors(TileCell targetCell) {
        actors.clear();
        for (Player player : players) {
            if (!player.isCollision(targetCell.getRectangle())) {
                continue;
            }
            actors.add(player);
        }
    }

    private void actorsCancelTaskAndSubmitNew(TileCell targetCell) {
        for (Player actor : actors) {
            cancelTaskAndSubmitNew(actor, targetCell);
        }
    }

    private void cancelTaskAndSubmitNew(Player actor, TileCell targetCell) {
        AStar lastFindPath = findPathTaskMap.get(actor.getId());
        // If the end point and the end of the current mandate does not look like a re- find
        if (lastFindPath != null && lastFindPath.isEnd(targetCell)) {
            return;
        }

        Future<?> lastfindPathTask = findPathFutureMap.get(actor.getId());
        if (lastfindPathTask != null
                && !lastfindPathTask.isDone()
                && lastfindPathTask.cancel(true)) {
            System.out.println("cancel——" + lastfindPathTask);
        }

        AStar task = actor.newFindPathTask(map, targetCell);
        Future<?> findPathFuture = executor.submit(task);
        findPathFutureMap.put(actor.getId(), findPathFuture);
        findPathTaskMap.put(actor.getId(), task);
        System.out.println("init——" + findPathFutureMap.get(actor.getId()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // System.out.println("mouseReleased:"+e.getX()+", "+e.getY());
        drawRouseDraggedRect.setSize(0, 0);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //actors.clear();

        Point mouseDraggedPoint = e.getPoint();
        int mouseDraggedWidth = (int) (mouseDraggedPoint.x - mousePressedPoint.x);
        int mouseDraggedHeight = (int) (mouseDraggedPoint.y - mousePressedPoint.y);

        // According to the player drag the mouse to drag the mouse to draw a rectangle transformation point
        if (mouseDraggedWidth < 0 && mouseDraggedHeight < 0) {
        } else if (mouseDraggedWidth < 0) {
            mouseDraggedPoint.y -= mouseDraggedHeight;
        } else if (mouseDraggedHeight < 0) {
            mouseDraggedPoint.x -= mouseDraggedWidth;
        } else {
            mouseDraggedPoint = mousePressedPoint;
        }

        // Draw mouse drag a rectangle the length and width must be positive
        mouseDraggedWidth = Math.abs(mouseDraggedWidth);
        mouseDraggedHeight = Math.abs(mouseDraggedHeight);

        drawRouseDraggedRect.setBounds(mouseDraggedPoint.x, mouseDraggedPoint.y, mouseDraggedWidth, mouseDraggedHeight);;
        for (Player player : players) {
            if (!player.isCollision(drawRouseDraggedRect)) {
                continue;
            }
            actors.add(player);
        }
    }

    private boolean isMouseLeftKey(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1;
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
