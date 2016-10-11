package core;

import java.util.*;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public final class Session {
    private List<Description> askedDescriptions = new ArrayList<>();
    private List<Answer> answers = new ArrayList<>();
    private List<Person> topPersons = new ArrayList<>();
    private Map<Description, List<Cell>> modifiedCells = new HashMap<>();


    public static void main(String[] args) {
//        new Session().testGetBestQuestion();
//        new Session().testOneCycle();
        new Session().testResponseCycles();
    }


    private void testGetBestQuestion() {
        Session sm = new Session();
        List<Description> descriptions = Description.getAll();
        sm.getBestQuestion(descriptions);
    }


    private void testOneCycle() {
        topPersons = Person.getAll();
        topPersons.forEach(System.out::println);
        Description bestQuestion = getBestQuestion(Description.getAll());
        System.out.println(bestQuestion.getQuestion());
        Answer answer = Answer.get("no");
        answerDescription(bestQuestion, answer);
        for (List<Cell> filteredCells : modifiedCells.values()) {
            for (Cell cell : filteredCells) {
                System.out.println(cell);
            }
        }
        bestQuestion = getBestQuestion(Description.getAll());
        topPersons.forEach(System.out::println);
        System.out.println(bestQuestion.getQuestion());
    }


    private void testResponseCycles() {
        // setup
        topPersons = Person.getAll();
        topPersons.forEach(System.out::println);
        while (true) {
            performCycle();
        }
    }


    private void performCycle() {
        Description bestQuestion = getBestQuestion(Description.getAll());
        System.out.println(bestQuestion.getQuestion());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Answer answer = Answer.getInputted(input);
        answerDescription(bestQuestion, answer);
        topPersons.forEach(System.out::println);
    }


    private Description getBestQuestion(List<Description> descriptions) {
        Collections.shuffle(descriptions); // not sure if necessary
        List<Double> margins = new ArrayList<>();
        for (Description description : descriptions) {
            List<Cell> cells;
            if (modifiedCells.containsKey(description)) {
                cells = modifiedCells.get(description);
            } else {
                cells = Cell.getCells(description);
            }
            List<Cell> filteredCells = new ArrayList<>();
            for (Cell cell : cells) {
                if (topPersons.contains(cell.getPerson())) {
                    filteredCells.add(cell);
                }
            }
            double margin = getMargin(filteredCells);
            margins.add(margin);
//            System.out.println(description + ": " + margin);
        }
        double minMargin = Double.MAX_VALUE;
        int bestDescIndex = 0;
        for (int i = 0; i < margins.size(); i++) {
            if (minMargin > margins.get(i)) {
                bestDescIndex = i;
                minMargin = margins.get(i);
            }
        }
//        System.out.println("Best: " + descriptions.get(bestDescIndex));
        return descriptions.get(bestDescIndex);
    }


    void answerDescription(Description description, Answer answer) {
        askedDescriptions.add(description);
        answers.add(answer);
        List<Cell> personCells = Cell.getCells(description);
        personCells = getTopPersons(personCells);
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : personCells) {
            if (cell.getScore() * answer.getScore() < 0) { // if scores are opposite signs
                topPersons.remove(cell.getPerson());
            }
            cell.addScore(answer.getScore()); // TODO: verify if valid
            cells.add(cell);
        }
        modifiedCells.put(description, cells);
    }



    private List<Cell> getTopPersons(List<Cell> personCells) {
        List<Cell> filteredCells = new ArrayList<>();
        for (Cell cell : personCells) {
            if (topPersons.contains(cell.getPerson())) {
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
            if (score > 0) {
                positive += score;
            } else {
                negative += score;
            }
        }
        return Math.abs(positive + negative);
    }
}
