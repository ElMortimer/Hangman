package Application;

import Logic.HighScoreManager;
import Logic.Score;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.util.List;

public class HighscoreController {

    @FXML private VBox highScoresBox;
    @FXML private Label titleLabel;
    @FXML private VBox easyScoresBox;
    @FXML private VBox mediumScoresBox;
    @FXML private VBox hardScoresBox;
    @FXML private Button backButton;

    private HighScoreManager highScoreManager;

    @FXML
    public void initialize() {
        highScoreManager = new HighScoreManager();
        backButton.setOnAction(e -> returnToMenu());

        loadHighScores();
    }

    private void loadHighScores() {
        displayScoresForDifficulty("easy", easyScoresBox);
        displayScoresForDifficulty("medium", mediumScoresBox);
        displayScoresForDifficulty("hard", hardScoresBox);
    }

    private void displayScoresForDifficulty(String difficulty, VBox container) {
        container.getChildren().clear();

        Label header = new Label(difficulty.toUpperCase());
        header.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ff4444; " +
                "-fx-effect: dropshadow(gaussian, rgba(255, 0, 0, 0.6), 10, 0.5, 0, 0);");
        container.getChildren().add(header);

        List<Score> scores = highScoreManager.getTopScoresByDifficulty(difficulty);

        int limit = Math.min(10, scores.size());

        if (scores.isEmpty()) {
            Label noScores = new Label("No scores yet!");
            noScores.setStyle("-fx-font-size: 18px; -fx-text-fill: #cccccc; -fx-padding: 10;");
            container.getChildren().add(noScores);
        } else {
            for (int i = 0; i < limit; i++) {
                Score entry = scores.get(i);

                HBox scoreRow = new HBox(15);
                scoreRow.setAlignment(Pos.CENTER_LEFT);
                scoreRow.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); " +
                        "-fx-padding: 10; -fx-background-radius: 5; " +
                        "-fx-border-color: #490816; -fx-border-radius: 5; -fx-border-width: 1;");

                Label rank = new Label("#" + (i + 1));
                rank.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffd700; -fx-min-width: 50;");

                Label name = new Label(entry.getPlayerName());
                name.setStyle("-fx-font-size: 18px; -fx-text-fill: #ffffff; -fx-min-width: 150;");

                Label score = new Label("Score: " + entry.getPoints());
                score.setStyle("-fx-font-size: 18px; -fx-text-fill: #00ff00; -fx-min-width: 100;");

                Label date = new Label(entry.getDate());
                date.setStyle("-fx-font-size: 14px; -fx-text-fill: #aaaaaa;");

                scoreRow.getChildren().addAll(rank, name, score, date);
                container.getChildren().add(scoreRow);
            }
        }
    }


    private void returnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));
            Parent menuRoot = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();

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