package controller;

import cardmaster.Game;

import cardmaster.DiscardPile;
import cardmaster.cards.Card;
import cardmaster.interfaces.Item;
import javafx.stage.Stage;
import view.GameGui;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;

public class GameController {

    private Stage guiStage;
    private Game game;
    private Button selectedDiscardPileButton;

    /**
     * Setzt die stage des controller, damit der exit button die gui
     * schließen kann.
     */
    public void init(Stage guiStage, Game game) {

        this.guiStage = guiStage;
        this.game = game;

        this.updateUI();

        this.updateAblageStapel();
    }

    public void exitGame() {

        this.guiStage.close();
    }

    /*
     * Methoden und attribute die genutzt werden, wenn eine Aktion
     * passiert (Knopf wird) gedrückt, über label gehovert etc.
     */

    @FXML
    private Label currentMode;
    
    @FXML
    private Label currentCredits;
    
    @FXML
    private Label currentRound;

    @FXML
    private Label cardCounter;

    @FXML
    private VBox ablageStapel;

    @FXML
    private VBox buttonContainer;
    
    @FXML
    private ListView<String> shopList;

    @FXML
    private ListView<Card> handList;

    @FXML
    private VBox shopArea;

    @FXML
    private VBox pileArea;
    
    @FXML
    private VBox handArea;

    @FXML
    private Button playButton;

    private void addDiscardPileButton(String label) {

        Button button = new Button(label);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setMaxHeight(Double.MAX_VALUE);

        VBox.setVgrow(button, Priority.ALWAYS);
        
        button.setOnAction(e -> {
            highlightButton(button);
            selectedDiscardPileButton = button;
        });
        ablageStapel.getChildren().add(button);
    }

    private void highlightButton(Button button) {
        // Entferne Highlight von allen Knöpfen
        ablageStapel.getChildren().forEach(node -> node.getStyleClass().remove("highlight"));
        // Füge Highlight zum ausgewählten Knopf hinzu
        button.getStyleClass().add("highlight");
    }

    @FXML
    public void buyCard() {

        if (this.game.buy(this.shopList.getSelectionModel().getSelectedIndex())) {

            this.shopList.getItems().remove(this.shopList.getSelectionModel().getSelectedIndex());
            this.currentCredits.textProperty().set(String.valueOf(this.game.getCredits()));

            this.updateUI();
        }
    }

    @FXML
    private void playCard() {

        Card playedCard = this.handList.getItems().get(this.handList.getSelectionModel().getSelectedIndex());
        int stackIndex = this.ablageStapel.getChildren().indexOf(selectedDiscardPileButton);

        this.game.play(playedCard, stackIndex);
        this.updateUI();
    }

    
    /**
     * 
     * UPDATE GAME
     * UI updates ( für die verschiedenen Modes und credits)
     * UI updates für die jeweiligen Komponenten im Spiel wie:
     *      - Draw/Handcards, beides gleich gewertet, um den Shopvorgang taktischer gestalten zu können. 
     *        (Man sieht einfach immer alle Karten) {@link #updateHandCards()}
     *      - ShopUpdate mit den aktuellen Items im Shop {@link #updateShop()}
     *      - Ablagestapel werden geleert {@link #updateAblageStapel()}
     */
    
    private void updateUI() {
        if (this.game != null) {

            this.currentMode.setText("Mode: " + this.game.getMode().toString().toLowerCase());
            this.currentCredits.setText("Credits: " + this.game.getCredits() + "c");
            this.currentRound.setText("Round: " + this.game.getCurrentRound() + " / " + this.game.maxRounds());
            
            this.updateHandCards();

            if (this.game.getMode() == Game.Mode.SHOPPING) {
                
                this.openShop();
                this.updateShop();
            } else if (this.game.getMode() == Game.Mode.PLAYING) {
                
                this.updateAblageStapel();
            } else {
                
                this.endGameState();
                this.openScoreboard();
            }
        }
    }
    
    private void updateHandCards() {
        
        this.handList.getItems().clear();
        this.cardCounter.setText("Cards: " + this.game.getAllHandCardsCount(this.game.getAllHandCards()) + " / " + this.game.getAllHandCardsMax());

        for (Object card : this.game.getAllHandCards()) {
            
            this.handList.getItems().add((Card) card);
        }
    }
    
    private void updateShop() {

        this.shopList.getItems().clear();
        
        for (Object o : this.game.getAllShopItems()) {

            
            
            if (o instanceof Card) {

                Card card = (Card) o;

                String formattedItem = String.format("(%d) %s", card.getPrice(), card.toString());
                this.shopList.getItems().add(formattedItem);
            } else {

                Item item = (Item) o;
                
                this.shopList.getItems().add(item.toString());
            }
        }
    }
    
    private void updateAblageStapel() {
        
        DiscardPile[] discardPiles = this.game.getDiscardPiles();
        this.ablageStapel.getChildren().clear();

        for (int i = 0; i < discardPiles.length; i++) {
            addDiscardPileButton(discardPiles[i].toString() + " " + (i+1));
        }
    }

    /**
     * OPEN / CLOSE STUFF IN DER GUI
     * 
     * SHOP 
     * PLAY / DISCARD
     * SCOREBOARD
     * 
     * Shop wird ausgegraut, wenn der {@link Game.Mode} nicht auf {@link Game.Mode.SHOPPING} ist
     * 
     * Beides soll das aktuelle Spiel beenden
     * 
     * In der Scoreboard UI soll es einen button für das speichern oder für das verlassen geben
     * Initial für das Scoreboard muss eine Datei erstellt werden und 
     * der aktuelle Spieler muss dort mit seinem Score und seiner Platzierung
     * direkt vertreten sein.
     */
    
    @FXML
    private void openScoreboard() {
        try {
            GameGui gameGui = new GameGui();
            gameGui.showScoreboard();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeShop() {

        /**
         * Shop ausgegraut wenn verlassen
         */
        this.shopArea.setDisable(true);
        this.shopArea.setOpacity(0.5);

        this.handArea.setDisable(false);
        this.handArea.setOpacity(1.0);

        this.pileArea.setDisable(false);
        this.pileArea.setOpacity(1.0);

        this.playButton.setDisable(false);
        this.playButton.setOpacity(1);

        this.game.endShopping();
        this.updateUI();
    }
    
    private void openShop() {

        /**
         * Shop wieder entgrauen beim betreten
         */
        this.shopArea.setDisable(false);
        this.shopArea.setOpacity(1.0);

        this.pileArea.setDisable(true);
        this.pileArea.setOpacity(0.5);

        this.playButton.setDisable(true);
        this.playButton.setOpacity(0.5);
    }

    private void endGameState() {

        this.shopArea.setDisable(true);
        this.shopArea.setOpacity(0.5);

        this.pileArea.setDisable(true);
        this.pileArea.setOpacity(0.5);

        this.playButton.setDisable(true);
        this.playButton.setOpacity(0.5);

    }
}