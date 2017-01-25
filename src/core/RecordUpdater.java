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
        for (Person person : Person.getAll()) {
            updatePerson(person);
        }

    }


    void updatePerson(Person person) {
        Record record = Record.get(person);
        Map<Description, Boolean> recordDescriptions = record.getDescriptionAnswers();
        List<Description> allDescriptions = Description.getAll();
        for (Description description : allDescriptions) {
            double score = -4;
            if (recordDescriptions.containsKey(description)) {
                score = getScore(recordDescriptions.get(description));
            }
            Cell toUpdate = new Cell(person, description, score);
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
