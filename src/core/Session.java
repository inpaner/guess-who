package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public class Session {
    private List<Description> askedDescriptions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    private List<Person> topPersons = new ArrayList<>();
    private List<Cell> modifiedCells = new ArrayList<>();

    public static void main(String[] args) {
        new Session().testOneCycle();
    }

    void testGet() {
        Session sm = new Session();
        List<Description> descriptions = Description.getAll();
        sm.getBestQuestion(descriptions);
    }

    void testOneCycle() {
        Description bestQuestion = this.getBestQuestion(Description.getAll());
        System.out.println(bestQuestion.getQuestion());
        Answer answer = Answer.get("no");
        this.answerDescription(bestQuestion, answer);
        for (Cell cell : modifiedCells) {
            System.out.println(cell);
        }
    }


    Description getBestQuestion(List<Description> descriptions) {
        Collections.shuffle(descriptions); // not sure if necessary
        List<Double> margins = new ArrayList<>();
        for (Description description : descriptions) {
            //TODO remove other persons
            List<Cell> cells = Cell.getCells(description);
            double margin = this.getMargin(cells);
            margins.add(margin);
            System.out.println(description + ": " + margin);
        }
        double minMargin = Double.MAX_VALUE;
        int bestDescIndex = 0;
        for (int i = 0; i < margins.size(); i++) {
            if (minMargin > margins.get(i)) {
                bestDescIndex = i;
                minMargin = margins.get(i);
            }
        }
        System.out.println("Best: " + descriptions.get(bestDescIndex));
        return descriptions.get(bestDescIndex);
    }


    void answerDescription(Description description, Answer answer) {
        askedDescriptions.add(description);
        answers.add(answer);
        List<Cell> personCells = Cell.getCells(description);
        personCells = this.removePersons(personCells);
        for (Cell cell : personCells) {
            cell.addScore(answer.getScore());
            modifiedCells.add(cell);
        }
    }


    private List<Cell> removePersons(List<Cell> personCells) {
        List<Cell> filteredCells = new ArrayList<>();
        for (Cell cell : personCells) {
            if (!topPersons.contains(cell.getPerson())) {
                filteredCells.add(cell);
            }
        }
        return filteredCells;
    }


    private double getMargin(List<Cell> cells) {
        double positive = 0;
        double negative = 0;

        for (Cell  cell : cells) {
            double score = cell.getScore();
            if (score >= 0) {
                positive += score;
            } else {
                negative += score;
            }
        }
        return Math.abs(positive + negative);
    }
}
