package core;

import java.util.*;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public final class Session {
    private final double DECAY = 0.7; // higher means more included in toplist
    private List<Person> allPersons = new ArrayList<>();
    private Map<Description, Answer> answeredDescriptionsMap = new HashMap<>(); // why didn't i do this first
    private Map<Description, List<Cell>> modifiedCells = new HashMap<>();


    public static void main(String[] args) {
//        new Session().testGetBestQuestion();
//        new Session().testOneCycle();
//        new Session().testResponseCycles();
        new Session().testGetBestDescriptions();
    }

    private void testGetBestQuestion() {
        Session sm = new Session();
        sm.getBestDescriptions();
    }


    private void testOneCycle() {
        allPersons = Person.getAll();
        allPersons.forEach(System.out::println);
        Description bestQuestion = getBestDescriptions().get(0);
        System.out.println(bestQuestion.getQuestion());
        Answer answer = Answer.get("no");
        answerDescription(bestQuestion, answer);
        for (List<Cell> filteredCells : modifiedCells.values()) {
            for (Cell cell : filteredCells) {
                System.out.println(cell);
            }
        }
        bestQuestion = getBestDescriptions().get(0);
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
        Description bestQuestion = getBestDescriptions().get(0);
        System.out.println(bestQuestion.getQuestion());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Answer answer = Answer.getInputted(input);
        answerDescription(bestQuestion, answer);
        System.out.println("\n--------");
        allPersons.forEach(System.out::println);
        System.out.println("\n");
    }


    private void testGetBestDescriptions() {
        allPersons = Person.getAll();
        Collections.shuffle(allPersons);
        int i = 0;
        List<Person> selectedPersons = new ArrayList<>();
        List<Person> unselectedPersons = new ArrayList<>();
        for (Person person : allPersons) {
            if (i < 1) {
                selectedPersons.add(person);
                System.out.println("selected: " + person);
            } else {
                unselectedPersons.add(person);
            }
            i++;
        }
        List<Description> best = getBestDescriptions(selectedPersons, unselectedPersons);
    }


    public List<Description> getBestDescriptions() {
        List<Description> descriptions = Description.getAll();
        Collections.shuffle(descriptions); // not sure if necessary
        List<DescriptionMargin> dms = new ArrayList<>();
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
            System.out.println(description + ": " + margin);
            dms.add(new DescriptionMargin(description, margin));
        }

        Collections.sort(dms);

        List<Description> sortedDescriptions = new ArrayList<>();
        for (DescriptionMargin dm : dms) {
            sortedDescriptions.add(dm.description);
        }
        System.out.println(sortedDescriptions.get(0));

        return sortedDescriptions;
    }


    public List<Description> getBestDescriptions(List<Person> selectedPersons, List<Person> unselectedPersons) {
        List<Description> descriptions = Description.getAll();
        Collections.shuffle(descriptions); // not sure if necessary
        List<DescriptionMargin> dms = new ArrayList<>();
        for (Description description : descriptions) {
            List<Cell> cells;
            if (modifiedCells.containsKey(description)) {
                cells = modifiedCells.get(description);
            } else {
                cells = Cell.getCells(description);
            }
            List<Cell> selectedPersonCells = new ArrayList<>();
            List<Cell> unselectedPersonCells = new ArrayList<>();
            for (Cell cell : cells) {
                if (selectedPersons.contains(cell.getPerson())) {
                    selectedPersonCells.add(cell);
                } else if (unselectedPersons.contains(cell.getPerson())) {
                    unselectedPersonCells.add(cell);
                }
            }
            double selectedMargin = getMargin(selectedPersonCells);
            double totalMargin = getMargin(selectedPersonCells, unselectedPersonCells);
            dms.add(new DescriptionMargin(description, selectedMargin, totalMargin));
//            System.out.println(description + " : " + selectedMargin + " : " + totalMargin);
        }
        Collections.sort(dms, Collections.reverseOrder());
        for (DescriptionMargin dm : dms) {
            System.out.println(dm);
        }

        List<Description> sortedDescriptions = new ArrayList<>();
        for (DescriptionMargin dm : dms) {
            sortedDescriptions.add(dm.description);
        }
        System.out.println(sortedDescriptions.get(0));
        return sortedDescriptions;
    }

    private class DescriptionMargin implements Comparable<DescriptionMargin> {
        Description description;
        double totalMargin;
        double selectedMargin = 0;

        DescriptionMargin(Description description, double totalMargin) {
            this.description = description;
            this.totalMargin = totalMargin;
        }

        DescriptionMargin(Description description, double selectedMargin, double totalMargin) {
            this.description = description;
            this.selectedMargin = selectedMargin;
            this.totalMargin = totalMargin;
        }



        @Override
        public int compareTo(DescriptionMargin other) {
            int comparison = Double.compare(this.selectedMargin, other.selectedMargin);
            if (comparison == 0) {
                comparison = Double.compare(this.totalMargin, other.totalMargin);
            }
            return comparison;
        }

        @Override
        public String toString() {
            return description + " : " + selectedMargin + " : "+ totalMargin;
        }
    }


    public void reset() {
        modifiedCells = new HashMap<>();
        answeredDescriptionsMap = new HashMap<>();
        allPersons = Person.getAll();
    }


    public void answerDescription(Description description, Answer answer) {
        if (answeredDescriptionsMap.containsKey(description)) {
            undoAnswer(description);
        }
        answeredDescriptionsMap.put(description, answer);
        List<Cell> personCells = Cell.getCells(description);
        personCells = getTopPersonCells(personCells);
        List<Cell> finishedCells = new ArrayList<>();
        for (Cell cell : personCells) {
            Person currentPerson = allPersons.get(allPersons.indexOf(cell.getPerson()));
            if (answer.getScore() > 0) {
                currentPerson.addScore(cell.getScore());
            } else {
                currentPerson.addScore(-cell.getScore());
            }
            cell.addScore(answer.getScore()); // TODO: verify if valid
            finishedCells.add(cell);
        }
        modifiedCells.put(description, finishedCells);
    }


    private void undoAnswer(Description description) {
        // Undo the added scores to the persons
        Answer oldAnswer = answeredDescriptionsMap.get(description);
        List<Cell> oldFinishedCells = modifiedCells.get(description);
        for (Cell oldCell : oldFinishedCells) {
            Person currentPerson = allPersons.get(allPersons.indexOf(oldCell.getPerson()));
            oldCell.addScore(-oldAnswer.getScore());
            if (oldAnswer.getScore() > 0) {
                currentPerson.addScore(-oldCell.getScore());
            } else {
                currentPerson.addScore(oldCell.getScore());
            }
        }
    }


    public void removeAnswer(Description description) {
        if (!answeredDescriptionsMap.containsKey(description)) {
            return;
        }
        undoAnswer(description);
        modifiedCells.remove(description);
        answeredDescriptionsMap.remove(description);
    }


    public List<Person> getTopPersons() {
        // filter via decay
        List<Person> filteredPersons = new ArrayList<>();
        Collections.sort(allPersons, Collections.reverseOrder());
        int totalToFilter = totalToFilter();
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


    public Map<Description, Answer> getAnsweredDescriptions() {
        return  answeredDescriptionsMap;
    }

    private List<Cell> getTopPersonCells(List<Cell> personCells) {
        List<Cell> filteredCells = new ArrayList<>();
//        List<Person> topPersons = getTopPersons();
        for (Cell cell : personCells) {
            if (allPersons.contains(cell.getPerson())) {
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


    private double getMargin(List<Cell> selectedCells, List<Cell> unselelctedCells) {
        double selectedCellsMargin = 0;
        double unselectedCellsMargin = 0;

        for (Cell cell : selectedCells) {
            selectedCellsMargin += cell.getScore();
        }
        for (Cell cell : unselelctedCells) {
            unselectedCellsMargin += cell.getScore();
        }
        return Math.abs(selectedCellsMargin - unselectedCellsMargin);
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
