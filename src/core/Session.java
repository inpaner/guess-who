package core;

import java.util.*;

/**
 * Created by Ivan Paner on 7/13/2016.
 */
public final class Session {
    private final double DECAY = 0.7; // higher means more included in toplist
    private List<Disease> allDiseases = new ArrayList<>();
    private Map<Symptom, Answer> answeredDescriptionsMap = new HashMap<>(); // why didn't i do this first
    private Map<Symptom, List<Cell>> modifiedCells = new HashMap<>();

    static {
        Disease.initCache();
        Symptom.initCache();
        RuleManager.initCache();
        Disease.initParents();
    }

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
        allDiseases = Disease.getAll();
        allDiseases.forEach(System.out::println);
        Symptom bestQuestion = getBestDescriptions().get(0);
        System.out.println(bestQuestion.getQuestion());
        Answer answer = Answer.get("no");
        answerDescription(bestQuestion, answer);
        for (List<Cell> filteredCells : modifiedCells.values()) {
            for (Cell cell : filteredCells) {
                System.out.println(cell);
            }
        }
        bestQuestion = getBestDescriptions().get(0);
        allDiseases.forEach(System.out::println);
        System.out.println(bestQuestion.getQuestion());
    }


    private void testResponseCycles() {
        // setup
        allDiseases = Disease.getAll();
        allDiseases.forEach(System.out::println);
        System.out.println("\n");
        while (true) {
            performCycle();
        }
    }


    private void performCycle() {
        Symptom bestQuestion = getBestDescriptions().get(0);
        System.out.println(bestQuestion.getQuestion());
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Answer answer = Answer.getInputted(input);
        answerDescription(bestQuestion, answer);
        System.out.println("\n--------");
        allDiseases.forEach(System.out::println);
        System.out.println("\n");
    }


    private void testGetBestDescriptions() {
        allDiseases = Disease.getAll();
        Collections.shuffle(allDiseases);
        int i = 0;
        List<Disease> selectedDiseases = new ArrayList<>();
        List<Disease> unselectedDiseases = new ArrayList<>();
        for (Disease disease : allDiseases) {
            if (i < 1) {
                selectedDiseases.add(disease);
                System.out.println("selected: " + disease);
            } else {
                unselectedDiseases.add(disease);
            }
            i++;
        }
        List<Symptom> best = getBestDescriptions(selectedDiseases, unselectedDiseases);
    }


    public List<Symptom> getBestDescriptions() {
        List<Symptom> symptoms = Symptom.getAll();
        Collections.shuffle(symptoms); // not sure if necessary
        List<DescriptionMargin> dms = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            List<Cell> cells;
            if (modifiedCells.containsKey(symptom)) {
                cells = modifiedCells.get(symptom);
            } else {
                cells = Cell.getCells(symptom);
            }
            List<Cell> filteredCells = new ArrayList<>();
            List<Disease> topDiseases = getTopPersons();
            for (Cell cell : cells) {
                if (topDiseases.contains(cell.getDisease())) {
                    filteredCells.add(cell);
                }
            }
            double margin = getMargin(filteredCells);
            System.out.println(symptom + ": " + margin);
            dms.add(new DescriptionMargin(symptom, margin));
        }

        Collections.sort(dms);

        List<Symptom> sortedSymptoms = new ArrayList<>();
        for (DescriptionMargin dm : dms) {
            sortedSymptoms.add(dm.symptom);
        }
        System.out.println(sortedSymptoms.get(0));

        return sortedSymptoms;
    }


    public List<Symptom> getBestDescriptions(List<Disease> selectedDiseases, List<Disease> unselectedDiseases) {
        List<Symptom> symptoms = Symptom.getAll();
        Collections.shuffle(symptoms); // not sure if necessary
        List<DescriptionMargin> dms = new ArrayList<>();
        for (Symptom symptom : symptoms) {
            List<Cell> cells;
            if (modifiedCells.containsKey(symptom)) {
                cells = modifiedCells.get(symptom);
            } else {
                cells = Cell.getCells(symptom);
            }
            List<Cell> selectedPersonCells = new ArrayList<>();
            List<Cell> unselectedPersonCells = new ArrayList<>();
            for (Cell cell : cells) {
                if (selectedDiseases.contains(cell.getDisease())) {
                    selectedPersonCells.add(cell);
                } else if (unselectedDiseases.contains(cell.getDisease())) {
                    unselectedPersonCells.add(cell);
                }
            }
            double selectedMargin = getMargin(selectedPersonCells);
            double totalMargin = getMargin(selectedPersonCells, unselectedPersonCells);
            dms.add(new DescriptionMargin(symptom, selectedMargin, totalMargin));
//            System.out.println(symptom + " : " + selectedMargin + " : " + totalMargin);
        }
        Collections.sort(dms, Collections.reverseOrder());
        for (DescriptionMargin dm : dms) {
            System.out.println(dm);
        }

        List<Symptom> sortedSymptoms = new ArrayList<>();
        for (DescriptionMargin dm : dms) {
            sortedSymptoms.add(dm.symptom);
        }
        System.out.println(sortedSymptoms.get(0));
        return sortedSymptoms;
    }

    private class DescriptionMargin implements Comparable<DescriptionMargin> {
        Symptom symptom;
        double totalMargin;
        double selectedMargin = 0;

        DescriptionMargin(Symptom symptom, double totalMargin) {
            this.symptom = symptom;
            this.totalMargin = totalMargin;
        }

        DescriptionMargin(Symptom symptom, double selectedMargin, double totalMargin) {
            this.symptom = symptom;
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
            return symptom + " : " + selectedMargin + " : "+ totalMargin;
        }
    }


    public void reset() {
        modifiedCells = new HashMap<>();
        answeredDescriptionsMap = new HashMap<>();
        allDiseases = Disease.getAll();
    }


    public void answerDescription(Symptom symptom, Answer answer) {
        if (answeredDescriptionsMap.containsKey(symptom)) {
            undoAnswer(symptom);
        }
        answeredDescriptionsMap.put(symptom, answer);
        List<Cell> personCells = Cell.getCells(symptom);
        personCells = getTopPersonCells(personCells);
        List<Cell> finishedCells = new ArrayList<>();
        for (Cell cell : personCells) {
            Disease currentDisease = allDiseases.get(allDiseases.indexOf(cell.getDisease()));
            if (answer.getScore() > 0) {
                currentDisease.addScore(cell.getScore());
            } else {
                currentDisease.addScore(-cell.getScore());
            }
            cell.addScore(answer.getScore()); // TODO: verify if valid
            finishedCells.add(cell);
        }
        modifiedCells.put(symptom, finishedCells);
    }


    private void undoAnswer(Symptom symptom) {
        // Undo the added scores to the persons
        Answer oldAnswer = answeredDescriptionsMap.get(symptom);
        List<Cell> oldFinishedCells = modifiedCells.get(symptom);
        for (Cell oldCell : oldFinishedCells) {
            Disease currentDisease = allDiseases.get(allDiseases.indexOf(oldCell.getDisease()));
            oldCell.addScore(-oldAnswer.getScore());
            if (oldAnswer.getScore() > 0) {
                currentDisease.addScore(-oldCell.getScore());
            } else {
                currentDisease.addScore(oldCell.getScore());
            }
        }
    }


    public void removeAnswer(Symptom symptom) {
        if (!answeredDescriptionsMap.containsKey(symptom)) {
            return;
        }
        undoAnswer(symptom);
        modifiedCells.remove(symptom);
        answeredDescriptionsMap.remove(symptom);
    }


    public List<Disease> getTopPersons() {
        // filter via decay
        List<Disease> filteredDiseases = new ArrayList<>();
        Collections.sort(allDiseases, Collections.reverseOrder());
        int totalToFilter = totalToFilter();
        for (int i = 0; i < totalToFilter; i++) {
            filteredDiseases.add(allDiseases.get(i));
        }
        return filteredDiseases;
    }


    private int totalToFilter() {
        int total = (int) (Math.pow(DECAY, answeredDescriptionsMap.size()) * allDiseases.size());
        if (total < 3) {
            total = 3;
        }
        return total;
    }

    public List<Disease> getAllDiseases() {
        Collections.sort(allDiseases, Collections.reverseOrder());
        return allDiseases;
    }


    public Map<Symptom, Answer> getAnsweredDescriptions() {
        return  answeredDescriptionsMap;
    }

    private List<Cell> getTopPersonCells(List<Cell> personCells) {
        List<Cell> filteredCells = new ArrayList<>();
//        List<Disease> topPersons = getTopPersons();
        for (Cell cell : personCells) {
            if (allDiseases.contains(cell.getDisease())) {
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
        public Symptom symptom;
        public Answer answer;

        public DescriptionAnswer(Symptom symptom, Answer answer) {
            this.symptom = symptom;
            this.answer = answer;
        }
    }
}
