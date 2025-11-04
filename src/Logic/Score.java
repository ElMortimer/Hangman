package Logic;//get scores
import java.time.LocalDate;

public class Score {
    private final String playerName;
    private final int points;
    private final String difficulty;
    private final String date;

    public Score(String playerName, int points, String difficulty) {
        this.playerName = playerName;
        this.points = points;
        this.difficulty = difficulty;
        this.date = LocalDate.now().toString();
    }

    public Score(String playerName, int points, String difficulty, String date) {
        this.playerName = playerName;
        this.points = points;
        this.difficulty = difficulty;
        this.date = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPoints() {
        return points;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("%-15s %-10s %-6d %s", playerName, difficulty, points, date);
    }
}
