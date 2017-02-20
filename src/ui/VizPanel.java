package ui;

import core.Person;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 1/16/2017.
 */
public class VizPanel extends JPanel {
    private final int CIRCLE_RADIUS = 10;
    private final int RECTANGLE_WIDTH = 100;
    private final int RECTANGLE_HEIGHT = 40;
    private final int MARGIN = 10;
    private final int ROWS = 8;
    private final String DEFAULT_TEXT = "DefaultText";
    private final int CELL_GUTTER = 0;
    int width;
    int height;


    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        VizPanel center = new VizPanel(frame.getSetWidth(), frame.getSetHeight());
        frame.setPanel(center);
//        center.test();
        center.testPersons();
    }


    VizPanel(int width, int height) {
        setLayout(null);
        this.width = width;
        this.height = height;
        RectangleComponent line = new RectangleComponent(2, height);
        line.setLocation(width / 2 - 1, 0);
        line.setSize(line.getPreferredSize());
        add(line);
        repaint();
    }


    public void addPersons(List<Person> persons) {
        int total = 0;
        for (Person person : persons) {
            RectangleComponent cell = new RectangleComponent(person.toString());
            addRight(cell, 0, total);
            total += 1;
            if (total >= ROWS) {
                break;
            }
        }
    }


    void test() {
        RectangleComponent rectangle = new RectangleComponent(RECTANGLE_WIDTH, RECTANGLE_HEIGHT, "bald");
        addLeft(rectangle, 1, 0);
//        addRight(rectangle, 0.5);

        RectangleComponent rectangle2 = new RectangleComponent(RECTANGLE_WIDTH, RECTANGLE_HEIGHT, "white");
        addLeft(rectangle2, -1, 1);

        RectangleComponent rectangle3 = new RectangleComponent(RECTANGLE_WIDTH, RECTANGLE_HEIGHT, "hasMoustache");
        addLeft(rectangle3, 0, 2);

        CircleComponent circle = new CircleComponent(CIRCLE_RADIUS);
//        addLeft(circle, -1);
//        addRight(circle, -0.5);
    }


    void testPersons() {
        Person p0 = new Person("Alice");
        Person p1 = new Person("Bob");
        Person p2 = new Person("Carol");
        Person p3 = new Person("Dan");
        Person p4 = new Person("Eve");
        Person p5 = new Person("Frank");
        Person p6 = new Person("Grace");
        Person p7 = new Person("Heidi");
        Person p8 = new Person("Iris");
        Person p9 = new Person("Juliet");
        Person p10 = new Person("Karen");
        Person p11 = new Person("Larry");

        List<Person> list = new ArrayList<>();
        list.add(p0);
        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p8);
        list.add(p9);
        list.add(p10);
        list.add(p11);
        addPersons(list);
    }


    void addLeft(JPanel component, double location, int row) {
        // location
        int leftWidth = width / 2;
        int leftCenter = width / 4;
        int start = MARGIN;
        int componentWidth = (int) component.getPreferredSize().getWidth();
        int range = leftWidth - MARGIN * 2 - componentWidth;
        int xCoord = (int) (normalize(location) * range + start);
        addToPanel(component, xCoord, row);
    }


    void addRight(JPanel component, double location, int row) {
        // location
        int rightWidth = width / 2;
        int rightCenter = width * 3 / 4;
        int start = MARGIN + rightWidth;
        int componentWidth = (int) component.getPreferredSize().getWidth();
        int range = rightWidth - MARGIN * 2 - componentWidth;
        int xCoord = (int) (normalize(-location) * range + start);
        addToPanel(component, xCoord, row);
    }


    private void addToPanel(JPanel component, int xCoord, int row) {
        int range = height - MARGIN * 2;
        int cellHeight = range / ROWS ;
        int yCoord = MARGIN + row * cellHeight;

        component.setLocation(xCoord, yCoord);
        component.setSize(component.getPreferredSize());
        add(component);
        repaint();
    }


    double normalize(double location) {
        return (location + 1) / 2;
    }


    class CircleComponent extends JPanel {
        Ellipse2D.Double circle;

        CircleComponent() {
            this(CIRCLE_RADIUS);
        }

        CircleComponent(int radius) {
            circle = new Ellipse2D.Double(0, 0, radius, radius);
            setOpaque(false);
        }


        @Override
        public Dimension getPreferredSize() {
            Rectangle bounds = circle.getBounds();
            return new Dimension(bounds.width, bounds.height);
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor( getForeground() );
            g2.fill(circle);
        }
    }


    class RectangleComponent extends JPanel {
        Rectangle2D.Double rectangle;
        String text = "";

        RectangleComponent(String text) {
            this(RECTANGLE_WIDTH, RECTANGLE_HEIGHT, text);
        }

        RectangleComponent(int width, int height) {
            this(width, height, DEFAULT_TEXT);
        }


        RectangleComponent(int width, int height, String text) {
            rectangle = new Rectangle2D.Double(0, 0, width, height);
            setOpaque(false);
            this.text = text;
        }


        @Override
        public Dimension getPreferredSize() {
            Rectangle bounds = rectangle.getBounds();
            return new Dimension(bounds.width, bounds.height);
        }


        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor( getForeground() );
            g2.fill(rectangle);
            g2.drawRect(10, 10, width, height);
            g2.setColor(Color.white);
            g2.drawString(text, 10, 25);
        }
    }
}
