package ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Ivan on 10/12/2016.
 */
public class MainPanel extends JPanel {

    private JTextArea candidatesArea;
    private JLabel topDescription;
    private JComboBox otherDescriptionsBox;
    private JButton yesTopDescription;
    private JButton noTopDescription;
    private JButton yesOtherDescription;
    private JButton noOtherDescription;
    private Listener listener;


    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        MainPanel panel = new MainPanel();
        frame.setPanel(panel);
    }


    MainPanel() {
        setLayout(new MigLayout("wrap 2"));
        // Top candidates panel
        JPanel candidatesPanel = new JPanel();
        candidatesPanel.setLayout(new MigLayout("wrap 1"));
        JLabel candidatesLabel = new JLabel("Top Candidates");
        int rows = 16;
        int cols = 20;
        candidatesArea = new JTextArea(rows, cols);
        candidatesPanel.add(candidatesLabel);
        candidatesPanel.add(candidatesArea);
        add(candidatesPanel);

        StringBuilder sb = new StringBuilder();
        sb.append("John");
        sb.append("\n");
        sb.append("Alice");
        sb.append("\n");
        sb.append("Bob");
        sb.append("\n");
        candidatesArea.setText(sb.toString());

        // Top descriptions Panel
        JPanel topDescriptionPanel = new JPanel();
        topDescriptionPanel.setLayout(new MigLayout("wrap 1"));
        JLabel topDescriptionLabel = new JLabel("Top Description");
        topDescription = new JLabel("This");
        yesTopDescription = new JButton("Yes");
        yesTopDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener == null) {
                    return;
                }
                listener.clickedTopYes();
            }
        });
        noTopDescription = new JButton("No");
        noTopDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    return;
                }
                listener.clickedTopNo();
            }
        });
        topDescriptionPanel.add(topDescriptionLabel);
        topDescriptionPanel.add(topDescription);
        topDescriptionPanel.add(yesTopDescription, "span, split 2");
        topDescriptionPanel.add(noTopDescription);

        JPanel otherDescriptionsPanel = new JPanel();
        otherDescriptionsPanel.setLayout(new MigLayout("wrap 1"));
        JLabel otherDescriptionsLabel = new JLabel("Other Descriptions");
        otherDescriptionsBox = new JComboBox();
        yesOtherDescription = new JButton("Yes");
        yesOtherDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    return;
                }
                listener.clickedOtherYes(otherDescriptionsBox.getSelectedIndex());
            }
        });
        noOtherDescription = new JButton("No");
        noOtherDescription.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener != null) {
                    return;
                }
                listener.clickedOtherNo(otherDescriptionsBox.getSelectedIndex());
            }
        });
        otherDescriptionsPanel.add(otherDescriptionsLabel);
        otherDescriptionsPanel.add(otherDescriptionsBox);
        otherDescriptionsPanel.add(yesOtherDescription, "span, split 2");
        otherDescriptionsPanel.add(noOtherDescription);

        JPanel descriptionsPanel = new JPanel();
        descriptionsPanel.setLayout(new MigLayout("wrap 1"));
        descriptionsPanel.add(topDescriptionPanel);
        descriptionsPanel.add(otherDescriptionsPanel);
        add(descriptionsPanel);
    }


    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void clickedTopYes();

        void clickedTopNo();

        void clickedOtherYes(int selectedIndex);

        void clickedOtherNo(int selectedIndex);
    }

}
