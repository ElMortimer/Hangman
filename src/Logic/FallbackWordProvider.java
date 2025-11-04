package Logic;//In case of API fail uses fallback
import java.util.Random;

public class FallbackWordProvider extends WordProvider {
    private final Random random = new Random();

    private final String[] easyWords   = {"cat", "dog", "sun", "tree", "book"};
    private final String[] mediumWords = {"computer", "java", "hangman", "puzzle", "garden"};
    private final String[] hardWords   = {"programming", "encyclopedia", "microscope", "astronomy", "architecture"};

    @Override
    public String getRandomWord(String difficulty) {
        String apiWord = super.getRandomWord(difficulty);
        if (apiWord != null && !apiWord.isEmpty()) {
            //System.out.println("Using API word: " + apiWord);
            return apiWord;
        }

        return switch(difficulty.toLowerCase()) {
            case "easy" -> easyWords[random.nextInt(easyWords.length)];
            case "medium" -> mediumWords[random.nextInt(mediumWords.length)];
            case "hard" -> hardWords[random.nextInt(hardWords.length)];
            default -> mediumWords[random.nextInt(mediumWords.length)];
        };
    }
}


