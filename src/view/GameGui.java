package view;

import cardmaster.Game;
import cardmaster.CardFactory;
import controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameGui extends Application {

    public static void main(String[] args) {
        
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        Game game = new Game(5, CardFactory.getDefaultFactory());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/GameGUI.fxml"));
        
        Parent parent = loader.load();

        GameController gameController = loader.getController();
        gameController.init(stage, game);
        
        stage.setScene(new Scene(parent));
        stage.show();
        stage.setTitle("Just another CardGame");
    }

    public void showScoreboard() throws Exception {

        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/ScoreboardGUI.fxml"));
        Parent parent = loader.load();

        // Scene scene = new Scene(parent);
        // scene.getStylesheets().add("resources/styles.css");

        // stage.setScene(scene);
        stage.setScene(new Scene(parent));
        stage.show();
        stage.setTitle("Just another CardGame - Scoreboard");
    }
}
