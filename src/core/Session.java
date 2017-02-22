package core;

import java.util.*;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public final class Session {
    private final double DECAY = 0.7; // higher means more included in toplist
    private List<Person> allPersons = new ArrayList<>();
    private List<DescriptionAnswer> answeredDescriptions = new ArrayList<>(); // deprecates the two lists above
    private Map<Description, Answer> answeredDescriptionsMap = new HashMap<>(); // why didn't i do this first
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
        allPersons = Person.getAll();
        allPersons.forEach(System.out::println);
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
        allPersons.forEach(System.out::println);
        System.out.println(bestQuestion.getQuestion());
    }


    private void testResponseCycles() {
        // setup
        allPersons = Person.getAll();
        allPersons.forEach(System.out::println);
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
        allPersons.forEach(System.out::println);
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
            List<Person> topPersons = getTopPersons();
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


    public List<Description> getBestDescriptions() {
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
            List<Person> topPersons = getTopPersons();
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

        Collections.sort(descriptions, new Comparator<Description>() {
            @Override
            public int compare(Description left, Description right) {
                return Double.compare(margins.get(descriptions.indexOf(left)), margins.get(descriptions.indexOf(right)));
            }
        });

        System.out.println(descriptions.get(0));
        return descriptions;
    }


    public void reset() {
        modifiedCells = new HashMap<>();
        answeredDescriptions = new ArrayList<>();
        allPersons = Person.getAll();
    }


    public void answerDescription(Description description, Answer answer) {
        answeredDescriptions.add(new DescriptionAnswer(description, answer));
        answeredDescriptionsMap.put(description, answer);
        List<Cell> personCells = Cell.getCells(description);
        personCells = getTopPersonCells(personCells);
        List<Cell> finishedCells = new ArrayList<>();
        for (Cell cell : personCells) {
//            if (cell.getScore() * answer.getScore() < 0) { // if scores are opposite signs
//                topPersons.remove(cell.getPerson());
//            }
            Person currentPerson = allPersons.get(allPersons.indexOf(cell.getPerson()));
            currentPerson.addScore(cell.getScore());
            cell.addScore(answer.getScore()); // TODO: verify if valid
            finishedCells.add(cell);
        }
        modifiedCells.put(description, finishedCells);
    }


    public List<Person> getTopPersons() {
        // filter via decay
        List<Person> filteredPersons = new ArrayList<>();
        Collections.sort(allPersons, Collections.reverseOrder());
        int totalToFilter = totalToFilter();
        System.out.println(totalToFilter);
        for (int i = 0; i < totalToFilter; i++) {
            filteredPersons.add(allPersons.get(i));
        }
        return  filteredPersons;
    }


    private int totalToFilter() {
        int total = (int) (Math.pow(DECAY, answeredDescriptionsMap.size()) * allPersons.size());
        if (total < 3) {
            total = 3;
        }
        return total;
    }

    public List<Person> getAllPersons() {
        Collections.sort(allPersons, Collections.reverseOrder());
        return  allPersons;
    }


    public List<DescriptionAnswer> getAnsweredDescriptions() {
        return  answeredDescriptions;
    }

    private List<Cell> getTopPersonCells(List<Cell> personCells) {
        List<Cell> filteredCells = new ArrayList<>();
        List<Person> topPersons = getTopPersons();
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


    public static class DescriptionAnswer {
        public Description description;
        public Answer answer;

        public DescriptionAnswer(Description description, Answer answer) {
            this.description = description;
            this.answer = answer;
        }
    }
}

