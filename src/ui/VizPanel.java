package ui;

import core.Answer;
import core.Description;
import core.Person;
import core.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static core.Session.*;

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
    private List<RectangleComponent> leftComponents;
    private List<RectangleComponent> rightComponents;
    int width;
    int height;


    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        VizPanel center = new VizPanel(frame.getSetWidth(), frame.getSetHeight());
        frame.setPanel(center);
//        center.test();
        center.testPersons();
        center.testDescriptions();
        center.testClear();
    }


    /* Tests */

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


    void testDescriptions() {
        Description d0 = new Description("white");
        Description d1 = new Description("bald");
        Description d2 = new Description("male");
        Description d3 = new Description("old");
        Description d4 = new Description("glasses");
        Description d5 = new Description("t-shirt");

        Answer yes = Answer.get("yes");
        Answer no = Answer.get("no");

        DescriptionAnswer da0 = new DescriptionAnswer(d0, yes);
        DescriptionAnswer da1 = new DescriptionAnswer(d1, no);
        DescriptionAnswer da2 = new DescriptionAnswer(d2, yes);
        DescriptionAnswer da3 = new DescriptionAnswer(d3, yes);
        DescriptionAnswer da4 = new DescriptionAnswer(d4, no);
        DescriptionAnswer da5 = new DescriptionAnswer(d5, yes);

        List<DescriptionAnswer> descriptions = new ArrayList<>();

        descriptions.add(da0);
        descriptions.add(da1);
        descriptions.add(da2);
        descriptions.add(da3);
        descriptions.add(da4);
        descriptions.add(da5);
        addDescriptions(descriptions);
    }


    void testClear() {
        clearLeft();
    }


    /* Actual class */

    public VizPanel(int width, int height) {
        setLayout(null);
        this.width = width;
        this.height = height;
        RectangleComponent line = new RectangleComponent(2, height);
        line.setLocation(width / 2 - 1, 0);
        line.setSize(line.getPreferredSize());
        add(line);
        repaint();
        leftComponents = new ArrayList<>();
        rightComponents = new ArrayList<>();
    }


    public void addDescriptions(List<Session.DescriptionAnswer> descriptions) {
        for (DescriptionAnswer struct : descriptions) {
            RectangleComponent cell = new RectangleComponent(struct.description.toString());
//            double location = normalize(struct.answer.getScore()/4);
            double location = struct.answer.getScore() / 4;
            addLeft(cell, location, leftComponents.size());
            leftComponents.add(cell);
            if (leftComponents.size() >= ROWS) {
                break;
            }
        }
    }


    public void addExtraDescriptions(List<Description> descriptions) {
        for (Description description : descriptions) {
            RectangleComponent cell = new RectangleComponent(description.toString());
            addLeft(cell, 0, leftComponents.size());
            leftComponents.add(cell);
            if (leftComponents.size() >= ROWS) {
                break;
            }
        }
    }


    public void addPersons(List<Person> persons) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Person person : persons) {
            if (person.getScore() > max) {
                max = person.getScore();
            }
            if (person.getScore() < min) {
                min = person.getScore();
            }
        }

        for (Person person : persons) {
            RectangleComponent cell = new RectangleComponent(person.toString());
            double location = (person.getScore() - min) / (max - min);
            addRight(cell, location, rightComponents.size());
            rightComponents.add(cell);
            if (rightComponents.size() >= ROWS) {
                break;
            }
        }
    }


    public void clearLeft() {
        for (RectangleComponent component : leftComponents) {
            remove(component);
        }
        leftComponents = new ArrayList<>();
        revalidate();
        repaint();
    }


    public void clearRight() {

        for (RectangleComponent component : rightComponents) {
            remove(component);
        }
        rightComponents = new ArrayList<>();
        revalidate();
        repaint();
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

    /**
     * Converts a [-1,1] value to [0,1]
     * @param location
     * @return
     */
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
