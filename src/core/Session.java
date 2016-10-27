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
        sm.getNewBestDescription();
    }


    private void testOneCycle() {
        topPersons = Person.getAll();
        topPersons.forEach(System.out::println);
        Description bestQuestion = getNewBestDescription();
        System.out.println(bestQuestion.getQuestion());
        Answer answer = Answer.get("no");
        answerDescription(bestQuestion, answer);
        for (List<Cell> filteredCells : modifiedCells.values()) {
            for (Cell cell : filteredCells) {
                System.out.println(cell);
            }
        }
        bestQuestion = getNewBestDescription();
        topPersons.forEach(System.out::println);
        System.out.println(bestQuestion.getQuestion());
    }


    private void testResponseCycles() {
        // setup
        topPersons = Person.getAll();
        topPersons.forEach(System.out::println);
        System.out.println("\n");
        while (true) {
            performCycle();
        }
    }


    private void performCycle() {
        Description bestQuestion = getNewBestDescription();
        System.out.println(bestQuestion.getQuestion());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Answer answer = Answer.getInputted(input);
        answerDescription(bestQuestion, answer);
        System.out.println("\n--------");
        for (Person person : topPersons) {
            System.out.println(person + " " + person.getScore());
        }
        System.out.println("\n");
    }


    public Description getNewBestDescription() {
        List<Description> descriptions = Description.getAll();
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
        System.out.println("Best: " + descriptions.get(bestDescIndex) + "\n");
        return descriptions.get(bestDescIndex);
    }


    public void reset() {
        modifiedCells = new HashMap<>();
        askedDescriptions = new ArrayList<>();
        answers = new ArrayList<>();
        topPersons = Person.getAll();
    }


    public void answerDescription(Description description, Answer answer) {
        askedDescriptions.add(description);
        answers.add(answer);
        List<Cell> descriptionCells = Cell.getCells(description);
        descriptionCells = includeTopPersonOnly(descriptionCells);
        List<Cell> cells = new ArrayList<>();
        for (Cell cell : descriptionCells) {
            Person currentPerson = topPersons.get(topPersons.indexOf(cell.getPerson()));
            if (cell.getScore() * answer.getScore() >= 0) { // scores are same
//                topPersons.remove(cell.getPerson());
                currentPerson.addToScore(Math.abs(answer.getScore()));
            } else {
                currentPerson.addToScore(-Math.abs(answer.getScore()));
            }
            cell.addScore(answer.getScore()); // TODO: verify if valid
            cells.add(cell);
        }
        Collections.sort(topPersons, new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getScore() > o2.getScore() ? 1
                        : o1.getScore() < o2.getScore() ? -1
                        : 0;
            }
        });
        modifiedCells.put(description, cells);
    }


    public List<Person> getTopPersons() {
        return  topPersons;
    }


    private List<Cell> includeTopPersonOnly(List<Cell> descriptionCells) {
        List<Cell> filteredCells = new ArrayList<>();
        for (Cell cell : descriptionCells) {
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
