import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.random.RandomGenerator;

public class EndOfSemester {
    /**
     * Checks if the program should display the title screen; only does so on
     * the program's first loop.
     * <p> Program does not redisplay title screen if main(args) is called again
     * by help menu or abnormal input.
    */
    static Boolean displayTitleScreen = true;
    static Boolean testHistory = false;
    static String helpMenu = """
                First command-line argument must be the subject name (history or chemistry)
                Second commmand-line argument is optional, and must be the number of questions that will be asked (for example, java EndOfSemester.java history 10)
                Capitalization never matters ("a" and "A" are equivalent)
                For multiple choice questions, only type the letter ("A", "B", "C", or "D")
                For true-false questions, type "T" or "F" """;
    static HistoryQuestionList historyQuestionList = new HistoryQuestionList(HistoryQuestionList.rawHQList);
    static Boolean testChemistry = false;
    static ChemistryQuestionList chemistryQuestionList = new ChemistryQuestionList(ChemistryQuestionList.rawCQList);
    static Scanner scanner = new Scanner(System.in);
    static RandomGenerator randomGenerator = RandomGenerator.getDefault();
    
    static List <String> historyArgList = new ArrayList<String>(); // list of arguments that are accepted to start the History test
    static List <String> chemistryArgList = new ArrayList <String>();
    /**
     * @param args should be given as: subject, (optional) number of questions 
     */
    public static void main(String[] args) throws InterruptedException {
        historyArgList.addAll(Arrays.asList("history", "world history"));
        chemistryArgList.addAll(Arrays.asList("chemistry", "chem"));

        // args should be subject, then number of questions in test
        if (args.length == 0 || args.length > 2) { // if wrong number of args
            System.out.println("Wrong number of args!\nFor help, type \"java EndOfSemester.java help\"");
            System.exit(1); // terminates
        }
        if (displayTitleScreen == true) {
            if (historyArgList.contains(args[0])) {
                clearScreen();
                System.out.println("World History H\nSemester 2 Practice Exam\nReady to start? Y/H (for help)/N");
                testHistory = true;
            }
            else if (chemistryArgList.contains(args[0])) {
                clearScreen();
                System.out.println("Chemistry H\nSemester 2 Practice Exam\nReady to start? Y/H (for help)/N");
                testChemistry = true;
            }
        }
        if (historyArgList.contains(args[0])) {
            testHistory = true;
        }
        else if (chemistryArgList.contains(args[0])) {
            testChemistry = true;
        }
        else if ("help".equalsIgnoreCase(args[0])) {
            clearScreen();
            System.out.println(helpMenu);
            System.exit(0);
        }
        else {
            System.out.println("Wrong type of args: invalid subject.\nFor help, type \"java EndOfSemester.java help\"");
            System.exit(2);
        }

        String start = scanner.nextLine();
        if (start.equalsIgnoreCase("H")
        || start.equalsIgnoreCase("help")) {
            System.out.println(helpMenu + "\n");
            System.out.println("Ready to start? Y/H (for help)/N");
            displayTitleScreen = false;
            main(args);
        } else if (start.equalsIgnoreCase("y")) {
            if (testHistory && args.length == 2) {
                try {
                    test(historyQuestionList, Integer.parseInt(args[1]));
                } catch (NumberFormatException exception) {
                    System.err.println("Wrong type of args: second argument must be an integer!");
                    System.exit(2);
                }
            } else if (testHistory && args.length == 1) {
                test(historyQuestionList);
            
            } else if (testChemistry && args.length == 2) {
                try {
                    test(chemistryQuestionList, Integer.parseInt(args[1]));
                } catch (NumberFormatException exception) {
                    System.err.println("Wrong type of args: second argument must be an integer!");
                    System.exit(2);
                }
            } else if (testChemistry) {
                test(chemistryQuestionList);

            } else {
                System.err.println("what...how...what did you do");
                System.exit(3);
            }
        } else if (start.equalsIgnoreCase("N")) {
            System.exit(0);
        } else {
            // probably not the most efficient way to exit but who cares
            displayTitleScreen = false;
            main(args);
        }
        scanner.close();
    }
    
    /**
     * <del>Unlike other test() function, this <b> can and will </b> repeat questions.</del> this implementation only
     * repeats if it has gone through all questions already
     * @param questionList
     * @param numQuestions
     * @throws InterruptedException
     */
    public static void test(QuestionList questionList, Integer numQuestions) throws InterruptedException {
        clearScreen();
        Integer correctCount = 0; // counts how many answers were correctly given
        Double correctPercent = 0.0;
        Integer randomNumber; // which question will be taken from QuestionList.questionList?
        List<Integer> nList = new ArrayList<Integer>(15); // list of question indices; used since .remove() doesn't work on 
        Integer nInt;
        for (Integer totalCount = 1; totalCount <= numQuestions; totalCount++) {
            if (nList.isEmpty()) {
                for (nInt = 0; nInt < questionList.size(); nInt++) {
                    nList.add(nInt);
                }
            }
            randomNumber = nList.remove(randomGenerator.nextInt(nList.size()));
            // System.out.println("question will be given soon...");
            if (questionList.getQuestion(randomNumber).askQuestion(totalCount)) {
                correctCount++;
                clearScreen();
            } else {
                System.out.print("Continuing in ");
                for (Integer sec = 5; sec > 0; sec--) {
                    System.out.printf("%d... ", sec);
                    Thread.sleep(1000);
                }
                clearScreen();
            }
            correctPercent = (double) correctCount/totalCount * 100;
            System.out.printf("%1$d correct out of %2$d (%3$.2f%%)\n", correctCount, totalCount, correctPercent);
            // System.out.println("question has been asked");
        }
        if (correctPercent == 100) {
            System.out.println("Great job!");
        }
    }

    public static void test(QuestionList questionList) throws InterruptedException {
        clearScreen();
        Integer correctCount = 0; // counts how many answers were correctly given
        Double correctPercent = 0.0;
        Integer randomNumber; // which question will be taken from QuestionList.questionList?
        List<Integer> nList = new ArrayList<Integer>(15); // list of question indices; used since .remove() doesn't work on 
        for (Integer nInt = 0; nInt < questionList.size(); nInt++) {
            nList.add(nInt);
        }
        for (Integer totalCount = 1; totalCount <= questionList.size(); totalCount++) {
            randomNumber = nList.remove(randomGenerator.nextInt(nList.size()));
            // System.out.println("question will be given soon...");
            if (questionList.getQuestion(randomNumber).askQuestion(totalCount)) {
                correctCount++;
                clearScreen();
            } else {
                System.out.print("Continuing in ");
                for (Integer sec = 5; sec > 0; sec--) {
                    System.out.printf("%d... ", sec);
                    Thread.sleep(1000);
                }
                clearScreen();
            }
            correctPercent = (double) correctCount/totalCount * 100;
            System.out.printf("%1$d correct out of %2$d (%3$.2f%%)\n", correctCount, totalCount, correctPercent);
            // System.out.println("question has been asked");
        }
        if (correctPercent == 100) {
            System.out.println("Great job!");
        }
    }

    /**
     * Clears the screen.
     */
    public static void clearScreen() {  
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }  
}