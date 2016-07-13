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
            this.updatePerson(person);
        }

    }


    void updatePerson(Person person) {
        ScoreManager sm = new ScoreManager();
        Record record = Record.get(person);
        Map<Description, Boolean> descriptions = record.getDescriptionAnswers();
        for (Description description : descriptions.keySet()) {
            double score = this.getScore(descriptions.get(description));
            sm.updateScore(person, description, score);
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
