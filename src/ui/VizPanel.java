package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Created by Ivan on 1/16/2017.
 */
public class VizPanel extends JPanel {
    private final int CIRCLE_RADIUS = 800;

    public static void main(String[] args) {

        VizPanel center = new VizPanel();

        MainFrame frame = new MainFrame();
        frame.setPanel(center);
        center.test();
        center.test();
        center.test();
    }

    void VizPanel() {

    }

    void test() {
        setLayout(null);
        Random random = new Random();
        int xCoord = random.nextInt(50);
        int yCoord = random.nextInt(50);
        CircleComponent circle = new CircleComponent(10);
        circle.setLocation(xCoord, yCoord);
        circle.setSize(circle.getPreferredSize());
        add(circle);
        repaint();
    }


    public static class CircleComponent extends JPanel {
        Ellipse2D.Double circle;

        CircleComponent(int radius) {
            circle = new Ellipse2D.Double(0, 0, radius, radius);
            setOpaque(false);
        }

        public Dimension getPreferredSize() {
            Rectangle bounds = circle.getBounds();
            return new Dimension(bounds.width, bounds.height);
        }


        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor( Color.black );
            g2.fill(circle);
        }



    }
}
