import core.Answer;
import core.Symptom;
import core.Disease;
import core.Session;
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
    private Symptom bestSymptom;
    private List<Symptom> allSymptoms;

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
        allSymptoms = Symptom.getAll();
        for (Symptom symptom : allSymptoms) {
            descriptionStrings.add(symptom.toString());
        }
        panel.setOtherDescriptions(descriptionStrings);
        panel.setListener(new PanelListener());
    }


    private void updateUi() {
        List<String> candidates = new ArrayList<>();
        for (Disease disease : session.getAllDiseases()) {
            candidates.add(disease.toString());
        }
        panel.setCandidates(candidates);

//        bestSymptom = session.getNewBestDescription();
//        panel.setTopDescription(bestSymptom.getQuestion());
        List<Symptom> bestSymptoms = session.getBestDescriptions();
        bestSymptom = bestSymptoms.get(0);
        panel.setTopDescription(bestSymptom.getQuestion());

        vizPanel.clearLeft();
        vizPanel.addDescriptions(session.getAnsweredDescriptions());
        vizPanel.addExtraDescriptions(bestSymptoms);

        vizPanel.clearRight();
        vizPanel.addPersons(session.getAllDiseases());
    }


    class PanelListener implements MainPanel.Listener {

        @Override
        public void clickedTopYes() {
            session.answerDescription(bestSymptom, Answer.get("yes"));
            updateUi();
        }


        @Override
        public void clickedTopNo() {
            session.answerDescription(bestSymptom, Answer.get("no"));
            updateUi();
        }

        @Override
        public void clickedOtherYes(int selectedIndex) {
            session.answerDescription(allSymptoms.get(selectedIndex), Answer.get("yes"));
            updateUi();
        }

        @Override
        public void clickedOtherNo(int selectedIndex) {
            session.answerDescription(allSymptoms.get(selectedIndex), Answer.get("no"));
            updateUi();
        }

        @Override
        public void clickedRemove(int selectedIndex) {
            session.removeAnswer(allSymptoms.get(selectedIndex));
            updateUi();
        }
    }
}

