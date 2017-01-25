import core.Answer;
import core.Description;
import core.Person;
import core.Session;
import ui.MainFrame;
import ui.MainPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 10/12/2016.
 */
public class Main {

    private Session session;
    private MainPanel panel;
    private Description bestDescription;
    private List<Description> allDescriptions;

    public static void main(String[] args) {
        new Main();
    }


    Main() {
        MainFrame frame = new MainFrame();
        panel = new MainPanel();
        frame.setPanel(panel);
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
        for (Person person : session.getTopPersons()) {
            candidates.add(person.toString());
        }
        panel.setCandidates(candidates);

        bestDescription = session.getNewBestDescription();
        panel.setTopDescription(bestDescription.getQuestion());
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
        }
    }
}

