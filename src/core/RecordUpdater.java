package core;

import java.util.List;
import java.util.Map;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public class RecordUpdater {
    private final int DEFAULT_SCORE = 4;

    public static void main(String[] args) {
        RecordUpdater ru = new RecordUpdater();
        ru.updateAll();
    }


    void updateAll() {
        for (Disease disease : Disease.getAll()) {
            updatePerson(disease);
        }

    }


    void updatePerson(Disease disease) {
        Record record = Record.get(disease);
        Map<Symptom, Boolean> recordDescriptions = record.getDescriptionAnswers();
        List<Symptom> allSymptoms = Symptom.getAll();
        for (Symptom symptom : allSymptoms) {
            double score = -4;
            if (recordDescriptions.containsKey(symptom)) {
                score = getScore(recordDescriptions.get(symptom));
            }
            Cell toUpdate = new Cell(disease, symptom, score);
            toUpdate.updateCell();
        }
    }


    private double getScore(boolean answer) {
        if (answer) {
            return DEFAULT_SCORE;
        } else {
            return -DEFAULT_SCORE;
        }
    }

}
