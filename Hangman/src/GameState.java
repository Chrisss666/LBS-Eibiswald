import java.util.HashSet;
import java.util.Set;

public class GameState {
    private int maxAttempts = 8;
    private int failedAttempts = 0;
    private Set<Character> letterHistory = new HashSet<>();

    public GameState(int wordLength) {
    }

    public void addLetterToHistory(char letter) {
        letterHistory.add(letter);
    }

    public String getLetterHistory() {
        return letterHistory.toString();
    }

    public boolean hasAlreadyGuessed(char letter) {
        return letterHistory.contains(letter);
    }

    public void incrementFailedAttempts() {
        failedAttempts++;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public boolean isGameOver() {
        return failedAttempts >= maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void reset(int wordLength) {
        failedAttempts = 0;
        letterHistory.clear();
    }
}
