package view;

import cardmaster.Game;

import controller.ScoreboardController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScoreboardGui extends Application {
    
    private Game game;

    public void setGame(Game game) {

        this.game = game;
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/ScoreboardGUI.fxml"));
        Parent parent = loader.load();

        ScoreboardController boardController = loader.getController();
        boardController.initialize(this.game);

        stage.setScene(new Scene(parent));
        stage.show();
        stage.setTitle("Just another CardGame - Scoreboard");
    }
}
