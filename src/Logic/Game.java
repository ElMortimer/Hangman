package Logic;// Logic.Game logic implementation
import java.util.HashSet;
import java.util.Set;

public class Game {
    private WordProvider wordProvider;
    private Player player;
    private Display display;
    private InputHandler inputHandler;
    private HighScoreManager highScoreManager;

    private String wordToGuess;
    private StringBuilder currentProgress;
    private int wrongGuesses;
    private final int maxWrong = 6;
    private Set<Character> usedLetters;

    public Game() {
        display = new Display();
        inputHandler = new InputHandler();
        highScoreManager = new HighScoreManager();
        wordProvider = new FallbackWordProvider();
        player = new Player();
    }

    public void start() {
        display.showWelcomeMessage();
        player.setName(inputHandler.getPlayerName());

        boolean playAgain;
        do {
            playOneGame();
            playAgain = inputHandler.askPlayerAgain();
        } while (playAgain);

        highScoreManager.displayHighScores(10);
        System.out.println("Thanks for playing, " + player.getName() + "!");
    }

    private void playOneGame() {
        String difficulty = inputHandler.getDifficultyString();
        usedLetters = new HashSet<>();

        wordToGuess = wordProvider.getRandomWord(difficulty);
       // System.out.println("DEBUG: Difficulty = " + difficulty + ", Word = " + wordToGuess + " (" + wordToGuess.length() + " letters)");
        currentProgress = new StringBuilder("_".repeat(wordToGuess.length()));
        wrongGuesses = 0;

        while (!isGameOver()) {
            display.showCurrentProgress(currentProgress.toString(), wrongGuesses, usedLetters);
            char guess = inputHandler.getGuess();
            guess = Character.toLowerCase(guess);

            if (usedLetters.contains(guess)) {
                System.out.println("You already guessed that letter!");
                continue;
            }

            usedLetters.add(guess);

            if (wordToGuess.indexOf(guess) >= 0) {
                updateProgress(guess);
            } else {
                wrongGuesses++;
            }
        }

        display.showGameResult(wordToGuess, wrongGuesses);

        if (won()) {
            int score = maxWrong - wrongGuesses;
            highScoreManager.addScore(player.getName(), score, difficulty);
        }
    }

    private boolean isGameOver() {
        return wrongGuesses >= maxWrong || currentProgress.toString().equals(wordToGuess);
    }

    private boolean won() {
        return currentProgress.toString().equals(wordToGuess);
    }

    private void updateProgress(char guess) {
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                currentProgress.setCharAt(i, guess);
            }
        }
    }
}
