package Application;

import Logic.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.*;
import javafx.scene.layout.StackPane;

import java.util.*;

public class GameController {

    @FXML private VBox difficultyBox;
    @FXML private Button easyButton;
    @FXML private Button mediumButton;
    @FXML private Button hardButton;

    @FXML private VBox nameBox;
    @FXML private TextField nameField;
    @FXML private Button startButton;

    @FXML private VBox gameBox;
    @FXML private StackPane gameRoot;
    @FXML private Pane hangmanPane;
    @FXML private Label wordLabel;
    @FXML private Label livesLabel;
    @FXML private Label usedLettersLabel;
    @FXML private TextField guessField;
    @FXML private Button guessButton;
    @FXML private Label messageLabel;
    @FXML private Button newGameButton;
    @FXML private Button menuButton;
    @FXML private ImageView enemyImage;

    @FXML private VBox gameOverBox;
    @FXML private Label gameOverLabel;
    @FXML private Label finalScoreLabel;
    @FXML private Button playAgainButton;
    @FXML private Button mainMenuButton;
    @FXML private Button changeDifficultyButton;

    private WordProvider wordProvider;
    private HighScoreManager highScoreManager;
    private String playerName;
    private String difficulty;

    private String wordToGuess;
    private StringBuilder currentProgress;
    private int wrongGuesses;
    private final int maxWrong = 6;
    private Set<Character> usedLetters;

    private List<Shape> hangmanParts;
    private Image[] enemyStages;
    private Rectangle blackOverlay;
    private MediaPlayer heartbeatPlayer;

    @FXML
    private void onChangeDifficultyButton() {
        showDifficultySelection();
    }

    @FXML
    public void initialize() {
        wordProvider = new FallbackWordProvider();
        highScoreManager = new HighScoreManager();
        usedLetters = new HashSet<>();
        hangmanParts = new ArrayList<>();

        blackOverlay = new Rectangle(2000, 2000);
        blackOverlay.setFill(Color.BLACK);
        blackOverlay.setVisible(false);
        blackOverlay.setMouseTransparent(true);
        gameRoot.getChildren().add(0, blackOverlay);

        showDifficultySelection();
        stopHeartbeat();

        easyButton.setOnAction(e -> selectDifficulty("easy"));
        mediumButton.setOnAction(e -> selectDifficulty("medium"));
        hardButton.setOnAction(e -> selectDifficulty("hard"));

        startButton.setOnAction(e -> startGameWithName());
        guessButton.setOnAction(e -> makeGuess());
        guessField.setOnAction(e -> makeGuess());
        newGameButton.setOnAction(e -> resetToStart());
        menuButton.setOnAction(e -> returnToMenu());

        playAgainButton.setOnAction(e -> playAgain());
        mainMenuButton.setOnAction(e -> returnToMenu());

        addPulsingGlow2();
    }

    private void showDifficultySelection() {
        difficultyBox.setVisible(true);
        difficultyBox.setManaged(true);
        nameBox.setVisible(false);
        nameBox.setManaged(false);
        gameBox.setVisible(false);
        gameBox.setManaged(false);
        gameOverBox.setVisible(false);
        gameOverBox.setManaged(false);
    }

    private void selectDifficulty(String diff) {
        blackOverlay.setVisible(false);
        blackOverlay.setMouseTransparent(true);
        difficulty = diff;
        difficultyBox.setVisible(false);
        difficultyBox.setManaged(false);

        if (playerName == null || playerName.isEmpty()) {
            nameBox.setVisible(true);
            nameBox.setManaged(true);
            nameField.requestFocus();
        } else {
            nameBox.setVisible(false);
            nameBox.setManaged(false);
            gameBox.setVisible(true);
            gameBox.setManaged(true);
            initializeGame();
        }
    }


    private void startGameWithName() {
        playerName = nameField.getText().trim();
        if (playerName.isEmpty()) {
            playerName = "Player";
        }

        nameBox.setVisible(false);
        nameBox.setManaged(false);
        gameBox.setVisible(true);
        gameBox.setManaged(true);

        initializeGame();
    }

    private void drawHangmanBase() {
        hangmanPane.getChildren().clear();
        hangmanParts.clear();

        Line base = new Line(20, 280, 180, 280);
        base.setStroke(Color.WHITE);
        base.setStrokeWidth(4);

        Line pole = new Line(60, 280, 60, 30);
        pole.setStroke(Color.WHITE);
        pole.setStrokeWidth(4);

        Line beam = new Line(60, 30, 140, 30);
        beam.setStroke(Color.WHITE);
        beam.setStrokeWidth(4);

        Line rope = new Line(140, 30, 140, 60);
        rope.setStroke(Color.WHITE);
        rope.setStrokeWidth(3);

        hangmanPane.getChildren().addAll(base, pole, beam, rope);
    }

    private void loadEnemyImages() {
        enemyStages = new Image[6];
        for (int i = 0; i < 6; i++) {
            String path = "/images/enemy" + (i + 1) + ".png";
            enemyStages[i] = new Image(getClass().getResourceAsStream(path));
        }
        enemyImage.setVisible(false);
        enemyImage.setOpacity(0.0);
        enemyImage.setPreserveRatio(true);
        enemyImage.setFitWidth(600);
    }

    private void initializeGame() {
        usedLetters.clear();
        wrongGuesses = 0;

        wordToGuess = wordProvider.getRandomWord(difficulty);
        if (wordToGuess == null || wordToGuess.isEmpty()) {
            wordToGuess = "hangman";
        }

        currentProgress = new StringBuilder("_".repeat(wordToGuess.length()));

        drawHangmanBase();
        loadEnemyImages();

        updateDisplay();
        messageLabel.setText("Good luck, " + playerName + "!");
        guessField.setDisable(false);
        guessButton.setDisable(false);
        guessField.clear();
        guessField.requestFocus();

        if (gameRoot != null) {
            gameRoot.setStyle("-fx-background-color: black;");
        }

        enemyImage.setVisible(false);
        enemyImage.setOpacity(0.0);
        enemyImage.setScaleX(1.0);
        enemyImage.setScaleY(1.0);

        MediaPlayer bgMusic = MenuController.getMediaPlayer();
        if (bgMusic != null) {
            bgMusic.setRate(1.0);
        }
    }

    private void makeGuess() {
        String input = guessField.getText().trim().toLowerCase();
        guessField.clear();

        if (input.isEmpty()) {
            messageLabel.setText("Please enter a letter!");
            return;
        }

        char guess = input.charAt(0);

        if (!Character.isLetter(guess)) {
            messageLabel.setText("Please enter a valid letter!");
            return;
        }

        if (usedLetters.contains(Optional.of(guess))) {
            messageLabel.setText("You already guessed '" + guess + "'!");
            return;
        }

        usedLetters.add(Character.valueOf(guess));

        if (wordToGuess.indexOf(guess) >= 0) {
            updateProgress(guess);
            messageLabel.setText("✓ Good guess!");
        } else {
            wrongGuesses++;
            updateEnemyStage(wrongGuesses);
            messageLabel.setText("✗ Wrong guess!");
        }

        updateDisplay();

        if (isGameOver()) {
            endGame();
        }
    }

    private void updateProgress(char guess) {
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guess) {
                currentProgress.setCharAt(i, guess);
            }
        }
    }

    private void updateDisplay() {
        StringBuilder displayWord = new StringBuilder();
        for (int i = 0; i < currentProgress.length(); i++) {
            displayWord.append(currentProgress.charAt(i));
            if (i < currentProgress.length() - 1) displayWord.append(" ");
        }
        wordLabel.setText(displayWord.toString());

        livesLabel.setText("Lives: " + (maxWrong - wrongGuesses) + "/" + maxWrong);

        if (usedLetters.isEmpty()) {
            usedLettersLabel.setText("Used: none");
        } else {
            StringBuilder used = new StringBuilder("Used: ");
            usedLetters.stream().sorted().forEach(c -> used.append(c).append(" "));
            usedLettersLabel.setText(used.toString().trim());
        }
    }

    private void startHeartbeat() {
        try {
            String heartbeatFile = getClass().getResource("/sounds/heartbeat.wav").toExternalForm();
            Media heartbeat = new Media(heartbeatFile);
            heartbeatPlayer = new MediaPlayer(heartbeat);
            heartbeatPlayer.setVolume(0.5);
            heartbeatPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            heartbeatPlayer.play();
        } catch (Exception e) {
            System.out.println("Could not load heartbeat sound: " + e.getMessage());
        }
    }

    private void updateHeartbeatSpeed(int wrongGuessCount) {
        if (heartbeatPlayer == null) return;

        double[] rates = {1.0, 1.2, 1.4, 1.6, 1.8, 2.0};

        if (wrongGuessCount > 0 && wrongGuessCount <= rates.length) {
            heartbeatPlayer.setRate(rates[wrongGuessCount - 1]);

            double volume = 0.4 + (wrongGuessCount * 0.1);
            heartbeatPlayer.setVolume(Math.min(volume, 1.0));
        }
    }

    private void stopHeartbeat() {
        if (heartbeatPlayer != null) {
            heartbeatPlayer.stop();
            heartbeatPlayer.dispose();
            heartbeatPlayer = null;
        }
    }

    private void updateBackgroundMusicSpeed(int wrongGuessCount) {
        MediaPlayer bgMusic = MenuController.getMediaPlayer();
        if (bgMusic == null) return;

        double[] rates = {1.0, 1.1, 1.2, 1.3, 1.4, 1.5};
        if (wrongGuessCount > 0 && wrongGuessCount <= rates.length) {
            bgMusic.setRate(rates[wrongGuessCount - 1]);
        }
    }

    private void updateEnemyStage(int stage) {
        if (stage < 1 || stage > maxWrong) return;

        if (stage == 1) {
            hangmanPane.getChildren().add(
                    new Circle(140, 80, 20, Color.WHITE) {{
                        setStroke(Color.WHITE);
                        setStrokeWidth(3);
                        setFill(Color.TRANSPARENT);
                    }}
            );

            enemyImage.setVisible(true);
            enemyImage.setImage(enemyStages[0]);
            enemyImage.setOpacity(0.0);
            enemyImage.setScaleX(0.5);
            enemyImage.setScaleY(0.5);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(800), enemyImage);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(0.3);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), enemyImage);
            scaleIn.setFromX(0.5);
            scaleIn.setFromY(0.5);
            scaleIn.setToX(1.0);
            scaleIn.setToY(1.0);

            ParallelTransition appear = new ParallelTransition(fadeIn, scaleIn);
            appear.setOnFinished(e -> startHeartbeat());
            appear.play();
            return;
        }

        updateHeartbeatSpeed(stage);
        updateBackgroundMusicSpeed(stage);
        enemyImage.setImage(enemyStages[stage - 1]);

        double newOpacity = 0.3 + (stage - 1) * 0.12;
        double newScale = 1.0 + (stage - 1) * 0.15;

        FadeTransition fade = new FadeTransition(Duration.millis(400), enemyImage);
        fade.setToValue(newOpacity);

        ScaleTransition zoom = new ScaleTransition(Duration.millis(400), enemyImage);
        zoom.setToX(newScale);
        zoom.setToY(newScale);

        ParallelTransition creep = new ParallelTransition(fade, zoom);
        creep.play();

        switch (stage) {
            case 2:
                hangmanPane.getChildren().add(
                        new Line(140, 100, 140, 170) {{
                            setStroke(Color.WHITE); setStrokeWidth(3);
                        }}
                );
                break;
            case 3:
                hangmanPane.getChildren().add(
                        new Line(140, 120, 110, 150) {{
                            setStroke(Color.WHITE); setStrokeWidth(3);
                        }}
                );
                break;
            case 4:
                hangmanPane.getChildren().add(
                        new Line(140, 120, 170, 150) {{
                            setStroke(Color.WHITE); setStrokeWidth(3);
                        }}
                );
                break;
            case 5:
                hangmanPane.getChildren().add(
                        new Line(140, 170, 120, 210) {{
                            setStroke(Color.WHITE); setStrokeWidth(3);
                        }}
                );
                break;
            case 6:
                hangmanPane.getChildren().add(
                        new Line(140, 170, 160, 210) {{
                            setStroke(Color.WHITE); setStrokeWidth(3);
                        }}
                );
                break;
        }
    }

    private boolean isGameOver() {
        return wrongGuesses >= maxWrong || currentProgress.toString().equals(wordToGuess);
    }

    private void endGame() {
        guessField.setDisable(true);
        guessButton.setDisable(true);

        boolean won = currentProgress.toString().equals(wordToGuess);

        if (won) {
            int score = maxWrong - wrongGuesses;
            highScoreManager.addScore(playerName, score, difficulty);
            showWinScreen(score);
        } else {
            playJumpscareAndGameOver();
        }

    }

    private void showWinScreen(int score) {
        messageLabel.setText("YOU WON! The word was: " + wordToGuess + " | Score: " + score);
        stopHeartbeat();

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            gameBox.setVisible(false);
            gameBox.setManaged(false);
            gameOverLabel.setText("VICTORY!");
            finalScoreLabel.setText("Word: " + wordToGuess + "\nScore: " + score);
            gameOverBox.setVisible(true);
            gameOverBox.setManaged(true);
        });
        pause.play();
    }

    private void playJumpscareAndGameOver() {
        stopHeartbeat();
        enemyImage.setImage(enemyStages[maxWrong - 1]);
        enemyImage.setVisible(true);

        Rectangle flashOverlay = new Rectangle(2000, 2000);
        flashOverlay.setFill(Color.TRANSPARENT);
        gameRoot.getChildren().add(flashOverlay);

        try {
            String audioFile = getClass().getResource("/sounds/scream.wav").toExternalForm();
            Media jumpscareSound = new Media(audioFile);
            MediaPlayer mediaPlayer = new MediaPlayer(jumpscareSound);
            mediaPlayer.setVolume(0.7);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Could not play jumpscare sound: " + e.getMessage());
        }

        ScaleTransition zoom = new ScaleTransition(Duration.millis(300), enemyImage);
        zoom.setFromX(enemyImage.getScaleX());
        zoom.setFromY(enemyImage.getScaleY());
        zoom.setToX(8.0);
        zoom.setToY(8.0);

        FadeTransition fullVisible = new FadeTransition(Duration.millis(100), enemyImage);
        fullVisible.setFromValue(enemyImage.getOpacity());
        fullVisible.setToValue(1.0);

        Timeline enemyFlash = new Timeline(
                new KeyFrame(Duration.millis(0), e -> enemyImage.setOpacity(1.0)),
                new KeyFrame(Duration.millis(100), e -> enemyImage.setOpacity(0.0)),
                new KeyFrame(Duration.millis(200), e -> enemyImage.setOpacity(1.0)),
                new KeyFrame(Duration.millis(300), e -> enemyImage.setOpacity(0.0)),
                new KeyFrame(Duration.millis(400), e -> enemyImage.setOpacity(1.0))
        );

        Timeline bgFlash = new Timeline(
                new KeyFrame(Duration.millis(0),
                        e -> flashOverlay.setFill(Color.TRANSPARENT)),
                new KeyFrame(Duration.millis(150),
                        e -> flashOverlay.setFill(Color.rgb(139, 0, 0, 0.9))),
                new KeyFrame(Duration.millis(300),
                        e -> flashOverlay.setFill(Color.TRANSPARENT)),
                new KeyFrame(Duration.millis(450),
                        e -> flashOverlay.setFill(Color.rgb(255, 0, 0, 0.95))),
                new KeyFrame(Duration.millis(600),
                        e -> flashOverlay.setFill(Color.TRANSPARENT)),
                new KeyFrame(Duration.millis(750),
                        e -> flashOverlay.setFill(Color.rgb(139, 0, 0, 0.9))),
                new KeyFrame(Duration.millis(900),
                        e -> flashOverlay.setFill(Color.TRANSPARENT)),
                new KeyFrame(Duration.millis(1050),
                        e -> flashOverlay.setFill(Color.rgb(255, 0, 0, 0.95))),
                new KeyFrame(Duration.millis(1200),
                        e -> flashOverlay.setFill(Color.TRANSPARENT)),
                new KeyFrame(Duration.millis(1350),
                        e -> flashOverlay.setFill(Color.rgb(139, 0, 0, 0.8))),
                new KeyFrame(Duration.millis(1500),
                        e -> flashOverlay.setFill(Color.TRANSPARENT))
        );

        ParallelTransition jumpscare = new ParallelTransition(
                fullVisible,
                zoom,
                enemyFlash,
                bgFlash
        );

        jumpscare.setOnFinished(e -> {
            gameRoot.getChildren().remove(flashOverlay);

            blackOverlay.setVisible(true);
            blackOverlay.setOpacity(0.0);

            FadeTransition fadeBlack = new FadeTransition(Duration.millis(600), blackOverlay);
            fadeBlack.setFromValue(0.0);
            fadeBlack.setToValue(1.0);

            FadeTransition fadeEnemy = new FadeTransition(Duration.millis(600), enemyImage);
            fadeEnemy.setFromValue(enemyImage.getOpacity());
            fadeEnemy.setToValue(0.0);

            FadeTransition fadeGameBox = new FadeTransition(Duration.millis(600), gameBox);
            fadeGameBox.setFromValue(gameBox.getOpacity());
            fadeGameBox.setToValue(0.0);

            ParallelTransition allFade = new ParallelTransition(fadeBlack, fadeEnemy, fadeGameBox);
            allFade.setOnFinished(evt -> {
                enemyImage.setVisible(false);
                enemyImage.setOpacity(0.0);
                gameBox.setOpacity(1.0);

                PauseTransition pause = new PauseTransition(Duration.millis(300));
                pause.setOnFinished(event -> showGameOverScreen());
                pause.play();
            });

            allFade.play();
        });

        jumpscare.play();
    }


    private void showGameOverScreen() {
        gameBox.setVisible(false);
        gameBox.setManaged(false);

        MediaPlayer bgMusic = MenuController.getMediaPlayer();
        if (bgMusic != null) {
            bgMusic.setRate(1.0);
        }

        blackOverlay.setVisible(true);
        blackOverlay.setOpacity(1.0);
        blackOverlay.setMouseTransparent(false);

        gameOverLabel.setText("GAME OVER");
        finalScoreLabel.setText("The word was: " + wordToGuess.toUpperCase());

        gameOverBox.setVisible(true);
        gameOverBox.setManaged(true);


        gameOverBox.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), gameOverBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void playAgain() {
        MediaPlayer bgMusic = MenuController.getMediaPlayer();
        if (bgMusic != null) {
            bgMusic.setRate(1.0);
        }

        blackOverlay.setVisible(false);
        blackOverlay.setMouseTransparent(true);

        gameOverBox.setVisible(false);
        gameOverBox.setManaged(false);
        gameBox.setVisible(true);
        gameBox.setManaged(true);
        initializeGame();
    }

    private void addPulsingGlow2() {
        DropShadow glow2 = new DropShadow();
        glow2.setColor(Color.RED);
        glow2.setRadius(8);
        glow2.setSpread(0.5);
        gameOverLabel.setEffect(glow2);

        TranslateTransition floating = new TranslateTransition(Duration.seconds(2), gameOverLabel);
        floating.setByY(-15);
        floating.setCycleCount(Timeline.INDEFINITE);
        floating.setAutoReverse(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow2.radiusProperty(), 3),
                        new KeyValue(glow2.spreadProperty(), 0.3)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(glow2.radiusProperty(), 5),
                        new KeyValue(glow2.spreadProperty(), 0.5)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        floating.play();
        timeline.play();
    }

    private void resetToStart() {
        showDifficultySelection();
        nameField.clear();
        guessField.clear();
        messageLabel.setText("");
    }

    private void returnToMenu() {
        try {
            MediaPlayer bgMusic = MenuController.getMediaPlayer();
            if (bgMusic != null) {
                bgMusic.setRate(1.0);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent menuRoot = loader.load();
            Stage stage = (Stage) gameRoot.getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            Scene newScene = new Scene(menuRoot, width, height);
            stage.setScene(newScene);
            stage.setTitle("Hangman - Main Menu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}