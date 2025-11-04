package Logic;

import java.io.*;
import java.util.*;

public class HighScoreManager {
    private final String filePath = "HighScoreManager.txt";
    private final List<Score> scores = new ArrayList<>();

    public HighScoreManager() {
        File file = new File(filePath);
        System.out.println("=== HighScoreManager Debug ===");
        System.out.println("File path: " + file.getAbsolutePath());
        System.out.println("File exists: " + file.exists());

        loadScores();

        System.out.println("Loaded " + scores.size() + " scores:");
        for (Score s : scores) {
            System.out.println("  - " + s.getPlayerName() + ": " + s.getPoints() + " (" + s.getDifficulty() + ")");
        }
        System.out.println("==============================");
    }

    public void addScore(String playerName, int points, String difficulty) {
        Score newScore = new Score(playerName, points, difficulty);
        scores.add(newScore);
        System.out.println("Added score: " + playerName + " - " + points + " (" + difficulty + ")");
        saveScores();
        System.out.println("Saved to file. Total scores: " + scores.size());
    }

    public List<Score> getTopScores() {
        List<Score> sorted = new ArrayList<>(scores);
        sorted.sort((a, b) -> Integer.compare(b.getPoints(), a.getPoints()));
        return sorted;
    }

    public List<Score> getTopScoresByDifficulty(String difficulty) {
        List<Score> filtered = new ArrayList<>();
        for (Score s : scores) {
            if (s.getDifficulty().equalsIgnoreCase(difficulty)) {
                filtered.add(s);
            }
        }
        filtered.sort((a, b) -> Integer.compare(b.getPoints(), a.getPoints()));
        System.out.println("Found " + filtered.size() + " scores for difficulty: " + difficulty);
        return filtered;
    }

    public void displayHighScores(int topN) {
        System.out.println("\n ====== HIGH SCORES ====== ");
        System.out.printf("%-5s %-15s %-10s %-6s %s%n", "Rank", "Player", "Difficulty", "Score", "Date");
        System.out.println("--------------------------------------------------------");

        List<Score> topScores = getTopScores();
        for (int i = 0; i < Math.min(topN, topScores.size()); i++) {
            System.out.printf("%-5d %s%n", i + 1, topScores.get(i));
        }

        if (topScores.isEmpty()) {
            System.out.println("No scores yet — be the first!");
        }

        System.out.println("--------------------------------------------------------\n");
    }

    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("# Player;Difficulty;Score;Date\n");
            for (Score s : scores) {
                writer.write(String.format("%s;%s;%d;%s%n",
                        s.getPlayerName(),
                        s.getDifficulty(),
                        s.getPoints(),
                        s.getDate()));
            }
            System.out.println("Successfully saved " + scores.size() + " scores to " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving scores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadScores() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("No existing scores file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.startsWith("#") || line.trim().isEmpty()) continue;

                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String name = parts[0];
                    String difficulty = parts[1];
                    int points = Integer.parseInt(parts[2]);
                    String date = parts[3];
                    scores.add(new Score(name, points, difficulty, date));
                    System.out.println("Loaded score from line " + lineNumber + ": " + name + " - " + points);
                } else {
                    System.out.println("Skipping invalid line " + lineNumber + ": " + line);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading scores: " + e.getMessage());
            e.printStackTrace();
        }
    }
}