import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class WordManager {
    private List<String> wordList = new ArrayList<>();
    private String currentWord;
    private StringBuilder maskedWord;

    Dotenv dotenv = Dotenv.load();
    private final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");

    public WordManager() {
        loadNewWord();
    }

    public void loadNewWord() {
        try {
            currentWord = getWordFromOpenAI();
            maskedWord = new StringBuilder("_".repeat(currentWord.length()));
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback, falls API fehlschlägt
            currentWord = "default";
            maskedWord = new StringBuilder("_".repeat(currentWord.length()));
        }
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

    private String getWordFromOpenAI() throws Exception {
        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        conn.setDoOutput(true);

        String inputJson = """
    {
        "model": "gpt-4o",
        "messages": [
            {
                "role": "user",
                "content": "Gib mir genau ein anspruchsvolles, kreatives und seltenes deutsches Wort für ein Hangman-Spiel. Es darf nur aus einem Wort bestehen und soll nicht zu typisch oder zu bekannt für Hangman-Spiele sein (wie z.B. „Donaudampfschifffahrt“ oder ähnliche Wörter). Gib mir ausschließlich das Wort zurück, ohne jegliche Erklärungen, andere Wörter oder zusätzliche Informationen."
            }
        ]
    }
    """;

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = inputJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            // Erfolgreiche Antwort auslesen
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Antwort in der Konsole ausgeben zur Überprüfung
            System.out.println("Response from API: " + response.toString());

            // Antwort parsen
            JSONObject jsonResponse = new JSONObject(response.toString());

            // Die Struktur der Antwort könnte anders sein, daher holen wir uns zuerst das Array "choices"
            JSONObject firstChoice = jsonResponse.getJSONArray("choices").getJSONObject(0);
            // Der content ist im Feld "message" unter dem Unterobjekt "content"
            String generatedWord = firstChoice.getJSONObject("message").getString("content").trim();

            return generatedWord;
        } else {
            // Fehlerantwort auslesen
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine.trim());
            }

            // Fehlerantwort und HTTP-Code anzeigen
            throw new Exception("Error: HTTP " + responseCode + "\n" + "Response: " + errorResponse.toString());
        }
    }

}