package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cardmaster.Game;
import cardmaster.ScoreBoard;
import cardmaster.scoreboard.Bestenliste;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

public class ScoreboardController {

    private Game game;

    @FXML
    private ListView<String> scoreList;

    @FXML
    private Button saveButton;

    private String scoreFile = "scoreboard.dat";
    private ScoreBoard board = Bestenliste.getInstance();

    public void initialize(Game game) {
        
        this.game = game;
        
        this.loadScores();
        System.out.println("BOARD SIZE on INIT: " + this.board.size());

        if (this.game.getMode() == Game.Mode.END && !GameController.addedOnce) {

            this.board.add(this.game.getCredits());
            GameController.addedOnce = true;
        }
        
        this.addScoresToList();
    }

    private void addScoresToList() {
        
        this.scoreList.getItems().clear();
        
        for (int i = 0; i < board.size(); i++) {

            String scoreEntry = String.format("%d. %f Points - %s", this.board.getPlace(i), 
                                                this.board.getScore(i), this.board.getInstant(i).toString());
            this.scoreList.getItems().add(scoreEntry);
        }
    }

    private void loadScores() {

        try (FileInputStream reader = new FileInputStream(this.scoreFile)) {

            this.board.load(reader);
        } catch (FileNotFoundException e) {

            System.err.println("File not found: " + this.scoreFile);
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

        if (!GameController.savedOnce) {

            try (FileOutputStream writer = new FileOutputStream(this.scoreFile)) {

                this.board.save(writer);
            } catch (IOException e) {

                e.printStackTrace();
            } catch (Exception e) {
                
                e.printStackTrace();
            }
    
            GameController.addedOnce = true;
        }
        
        try (FileOutputStream writer = new FileOutputStream(this.scoreFile)) {

            this.board.save(writer);
        } catch (IOException e) {

            e.printStackTrace();
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
}
