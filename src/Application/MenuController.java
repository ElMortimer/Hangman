package Application;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

public class MenuController {

    @FXML private VBox menuBox;
    @FXML private Label titleLabel;
    @FXML private Button newGameButton;
    @FXML private Button highScoresButton;
    @FXML private Button quitButton;

    private static MediaPlayer mediaPlayer;
    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @FXML
    public void initialize() {
        newGameButton.setOnAction(e -> startGame());
        highScoresButton.setOnAction(e -> showHighScores());
        quitButton.setOnAction(e -> quitGame());

        initializeSounds();
        addPulsingGlow();

        menuBox.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                setupBindings(newScene);
            }
        });
    }

    private void showHighScores() {
        try {
            java.net.URL fxmlUrl = getClass().getResource("Highscore.fxml");
            if (fxmlUrl == null) {
                System.out.println("ERROR: HighScores.fxml not found!");
                System.out.println("Looking in: " + getClass().getPackage().getName());
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent highScoresRoot = loader.load();

            Stage stage = (Stage) highScoresButton.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene newScene = new Scene(highScoresRoot, width, height);
            stage.setScene(newScene);
            stage.setTitle("High Scores");
        } catch (Exception e) {
            System.out.println("Error loading high scores:");
            e.printStackTrace();
        }
    }

    private void addPulsingGlow() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.RED);
        glow.setRadius(20);
        glow.setSpread(0.5);
        titleLabel.setEffect(glow);

        TranslateTransition floating = new TranslateTransition(Duration.seconds(2), titleLabel);
        floating.setByY(-15);
        floating.setCycleCount(Timeline.INDEFINITE);
        floating.setAutoReverse(true);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 20),
                        new KeyValue(glow.spreadProperty(), 0.5)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(glow.radiusProperty(), 35),
                        new KeyValue(glow.spreadProperty(), 0.8)
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        floating.play();
        timeline.play();
    }

    public void initializeSounds() {
        if (mediaPlayer == null) {
            try {
                String soundsfile = getClass().getResource("/sounds/sound.wav").toExternalForm();
                Media sound = new Media(soundsfile);
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setVolume(0.5);
                mediaPlayer.play();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void setupBindings(Scene scene) {
        titleLabel.styleProperty().bind(
                scene.heightProperty().divide(10).asString("-fx-font-size: %.0fpx;")
        );

        newGameButton.styleProperty().bind(
                scene.heightProperty().divide(25).asString("-fx-font-size: %.0fpx;")
        );
        highScoresButton.styleProperty().bind(
                scene.heightProperty().divide(25).asString("-fx-font-size: %.0fpx;")
        );
        quitButton.styleProperty().bind(
                scene.heightProperty().divide(25).asString("-fx-font-size: %.0fpx;")
        );

        newGameButton.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        highScoresButton.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        quitButton.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
    }

    private void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameUI.fxml"));
            Parent gameRoot = loader.load();

            Stage stage = (Stage) newGameButton.getScene().getWindow();

            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene newScene = new Scene(gameRoot, width, height);
            stage.setScene(newScene);
            stage.setTitle("Hangman Game");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void quitGame() {
        System.exit(0);
    }
}