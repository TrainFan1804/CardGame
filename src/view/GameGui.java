package view;

import cardmaster.Game;
import cardmaster.CardFactory;

import controller.GameController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class GameGui extends Application {
    
    // private Game game = new Game(5, CardFactory.getDefaultFactory());
    private Game game = new Game(5);


    public static void main(String[] args) {
        
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/GameGUI.fxml"));
        
        Parent parent = loader.load();

        GameController gameController = loader.getController();
        gameController.initialize(this.game);
        
        stage.setScene(new Scene(parent));
        stage.show();
        stage.setTitle("Just another CardGame");
    }
}
