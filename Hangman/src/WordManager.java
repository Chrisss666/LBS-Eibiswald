import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordManager {
    private List<String> wordList = new ArrayList<>();
    private String currentWord;
    private StringBuilder maskedWord;

    public WordManager() {
        loadWordsFromFile();
        loadNewWord();
    }

    public void loadWordsFromFile() {
        try (Scanner scanner = new Scanner(new File("words.txt"))) {
            while (scanner.hasNext()) {
                wordList.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadNewWord() {
        currentWord = wordList.get((int) (Math.random() * wordList.size()));
        maskedWord = new StringBuilder("_".repeat(currentWord.length()));
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public String getMaskedWord() {
        return maskedWord.toString();
    }

    public boolean processGuess(char guess) {
        boolean correctGuess = false;
        char lowerCaseGuess = Character.toLowerCase(guess);

        String lowerCaseWord = currentWord.toLowerCase();

        for (int i = 0; i < currentWord.length(); i++) {
            if (lowerCaseWord.charAt(i) == lowerCaseGuess) {
                maskedWord.setCharAt(i, currentWord.charAt(i));
                correctGuess = true;
            }
        }
        return correctGuess;
    }

    public boolean isWordGuessed() {
        return maskedWord.indexOf("_") == -1;
    }
}
