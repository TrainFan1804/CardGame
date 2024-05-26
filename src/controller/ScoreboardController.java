package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import cardmaster.ScoreBoard;
import cardmaster.scoreboard.Bestenliste;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;


public class ScoreboardController {

    @FXML
    private ListView<String> scoreList;

    private String scoreFile = "scoreboard.txt";
    private ScoreBoard board = Bestenliste.getInstance();

    public void initialize() {
        
        loadScores();
        addScoresToList();
    }

    private void addScoresToList() {
        
        for (int i = 0; i < board.size(); i++) {

            String scoreEntry = String.format("%d. %f Points - %s", board.getPlace(i), board.getScore(i), board.getInstant(i).toString());
            scoreList.getItems().add(scoreEntry);
        }
    }

    private void loadScores() {
        
        try (FileInputStream reader = new FileInputStream(scoreFile)) {

            board.load(reader);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + scoreFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exitGame() {

        Platform.exit();
    }

    public void saveScoreboard() {

        ScoreBoard board = Bestenliste.getInstance();
        try (FileOutputStream writer = new FileOutputStream(scoreFile)) {

            board.save(writer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Platform.exit();
    }
}
