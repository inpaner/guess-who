import core.Answer;
import core.Description;
import core.Person;
import core.Session;
import sun.security.krb5.internal.crypto.Des;
import ui.MainFrame;
import ui.MainPanel;
import ui.VizPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 10/12/2016.
 */
public class Main {

    private Session session;
    private MainPanel panel;
    private VizPanel vizPanel;
    private Description bestDescription;
    private List<Description> allDescriptions;

    public static void main(String[] args) {
        new Main();
    }


    Main() {
        MainFrame frame = new MainFrame();
        panel = new MainPanel();
        frame.setPanel(panel);
        MainFrame vizFrame = new MainFrame();
        vizPanel = new VizPanel(frame.getSetWidth(), frame.getSetHeight());
        vizFrame.setPanel(vizPanel);

        session = new Session();
        session.reset();

        updateUi();
        List<String> descriptionStrings = new ArrayList<>();
        allDescriptions = Description.getAll();
        for (Description description : allDescriptions) {
            descriptionStrings.add(description.toString());
        }
        panel.setOtherDescriptions(descriptionStrings);
        panel.setListener(new PanelListener());
    }


    private void updateUi() {
        List<String> candidates = new ArrayList<>();
        for (Person person : session.getAllPersons()) {
            candidates.add(person.toString());
        }
        panel.setCandidates(candidates);

//        bestDescription = session.getNewBestDescription();
//        panel.setTopDescription(bestDescription.getQuestion());
        List<Description> bestDescriptions = session.getBestDescriptions();
        bestDescription = bestDescriptions.get(0);
        panel.setTopDescription(bestDescription.getQuestion());

        vizPanel.clearLeft();
        vizPanel.addDescriptions(session.getAnsweredDescriptions());
        vizPanel.addExtraDescriptions(bestDescriptions);

        vizPanel.clearRight();
        vizPanel.addPersons(session.getAllPersons());
    }


    class PanelListener implements MainPanel.Listener {

        @Override
        public void clickedTopYes() {
            session.answerDescription(bestDescription, Answer.get("yes"));
            updateUi();
        }


        @Override
        public void clickedTopNo() {
            session.answerDescription(bestDescription, Answer.get("no"));
            updateUi();
        }

        @Override
        public void clickedOtherYes(int selectedIndex) {
            session.answerDescription(allDescriptions.get(selectedIndex), Answer.get("yes"));
            updateUi();
        }

        @Override
        public void clickedOtherNo(int selectedIndex) {
            session.answerDescription(allDescriptions.get(selectedIndex), Answer.get("no"));
            updateUi();
        }

        @Override
        public void clickedRemove(int selectedIndex) {
            session.removeAnswer(allDescriptions.get(selectedIndex));
            updateUi();
        }
    }
}

