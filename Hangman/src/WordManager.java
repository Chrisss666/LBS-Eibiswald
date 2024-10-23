import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class WordManager {
    private List<String> wordList = new ArrayList<>();
    private String currentWord;
    private StringBuilder maskedWord;
    private final String OPENAI_API_KEY = "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"; // Ersetze dies durch deinen API-Schlüssel

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
                "role": "system",
                "content": "Du bist ein Generator für schwierige deutsche Wörter für ein Hangmanspiel. Gib dem Nutzer jedoch nur und zwar nur dasd eine Wort zurück ohne jeglichen anderen Text. Das ist wichtig!"
            },
            {
                "role": "user",
                "content": "Gib mir bitte ein schweres, langes Wort für ein Hangmanspiel auf Deutsch. Das Wort darf jedoch nur aus einem Wort bestehen, da mein System keine mehreren Wörter verarbeiten kann."
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
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = in.readLine()) != null) {
                response.append(responseLine.trim());
            }

            JSONObject jsonResponse = new JSONObject(response.toString());
            String generatedWord = jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content").trim();

            return generatedWord;
        } else {
            throw new Exception("Failed to get a valid response from OpenAI API. HTTP Response code: " + responseCode);
        }
    }
}