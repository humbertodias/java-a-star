package astar;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final int WIN_WIDTH = TileCell.WIDTH * 16;
    public static final int WIN_HEIGHT = TileCell.HEIGHT * 16;

    public Main() {
        super("AStar PathFinding");

        GamePanel gamePanel = new GamePanel();
        gamePanel.initGUI();


        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        
        container.add(statusPanel(), BorderLayout.NORTH);
        container.add(gamePanel);
        add(new JScrollPane(container));
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setVisible(true);
        setLocationRelativeTo(null);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gamePanel.offScreenImage = null;
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }

    private JPanel statusPanel() {
        JPanel status = new JPanel();
        status.setLayout( new BorderLayout() );
        status.setBorder(BorderFactory.createEmptyBorder() );
        String msg = "<html>Click on Left Mouse Button to <font color=orange>SELECT</font> or Right to <font color=green>FOLLOW</font>.</html>";
        JLabel label = new JLabel(msg);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        status.add(label);
        return status;
    }

}
