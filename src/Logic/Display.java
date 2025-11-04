package Logic;//hangman animation and overall display
import java.util.Set;

public class Display {
    private final String[] hangmanStages = {
            """
          +---+
          |   |
              |
              |
              |
              |
        =========""",
            """
          +---+
          |   |
          O   |
              |
              |
              |
        =========""",
            """
          +---+
          |   |
          O   |
          |   |
              |
              |
        =========""",
            """
          +---+
          |   |
          O   |
         /|   |
              |
              |
        =========""",
            """
          +---+
          |   |
          O   |
         /|\\  |
              |
              |
        =========""",
            """
          +---+
          |   |
          O   |
         /|\\  |
         /    |
              |
        =========""",
            """
          +---+
          |   |
          O   |
         /|\\  |
         / \\  |
              |
        ========="""
    };

    public void showWelcomeMessage() {
        System.out.println("=== Welcome to Hangman ===");
    }

    public void showCurrentProgress(String progress, int wrongGuesses, Set<Character> usedLetters) {
        System.out.println(hangmanStages[wrongGuesses]);
        System.out.println("Word: " + progress);
        System.out.println("Wrong guesses: " + wrongGuesses + "/6");
        System.out.println("Used letters : " + usedLetters);
        System.out.println("-----------------------------");
    }

    public void showGameResult(String word, int wrongGuesses) {
        if (wrongGuesses >= 6) {
            System.out.println(hangmanStages[6]);
            System.out.println("You lost! The word was: " + word);
        } else {
            System.out.println("🎉 Congratulations! You guessed the word: " + word);
        }
        System.out.println();
    }
}