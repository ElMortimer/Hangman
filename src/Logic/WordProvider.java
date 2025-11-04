package Logic;//Gets words from the internet trough an API
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class WordProvider {

    public String getWordFromApi(int lenght) {
        try {
            URL url = new URL("https://random-word-api.herokuapp.com/word?length=" + lenght);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            //System.out.println("API Response: " + response);
            response = response.replaceAll("\\[|\\]|\"", "");
            if (!response.isEmpty()) return response.toLowerCase();
        } catch (Exception e) {
            System.out.println("API failed, using backup words");
        }

        return null;
    }

    public String getRandomWord(String difficulty) {
        int lenght;
        Random random = new Random();
        switch (difficulty.toLowerCase()) {
            case "easy":
                lenght = random.nextInt(2,6);
                break;
            case "medium":
                lenght = random.nextInt(6,9);
                break;
            case "hard":
                lenght = random.nextInt(9,13);
                break;
            default:
                lenght = 6;
        }


        String apiWord = getWordFromApi(lenght);
        if (apiWord != null) return apiWord;

        return null;
    }
}
