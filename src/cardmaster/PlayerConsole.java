package cardmaster;

import java.util.Random;
import java.util.Scanner;
import cardmaster.Game.Mode;
import cardmaster.cards.Card;

public class PlayerConsole extends Player {

    private Scanner scanner;

    public PlayerConsole() {
        scanner = new Scanner(System.in);
    }

    @Override
    public int shop(Game game) {
        System.out.println("Was möchten Sie kaufen? Aktuelle Credits: " + game.getCredits());
        for (int i = 0; i < game.getShopItemCount(); i++) {

            System.out.println((i + 1) + ": (" + game.getShopItemPrice(i) + ") " + game.getShopItemDescription(i));
        }

        System.out.println((game.getShopItemCount() + 1) + ": Nichts. Mit dem Spielen beginnen.");
        System.out.print(">>> ");

        int index = scanner.nextInt() - 1; // -1 weil die Eingabe 1-basiert ist, Index aber 0-basiert
        if (index < 0 || index >= game.getShopItemCount()) {

            return -1; // Beendet den Shopping-Modus
        }
        return index;
    }

    @Override
    public void playCard(Game game) {

        System.out.println("========================");
        System.out.println("Credits: " + game.getCredits());
        System.out.print("  ");

        for (int i = 0; i < game.getStacksCount(); i++) {

            Card topCard = game.getDiscardPileTopCard(i);
            System.out.print(topCard != null ? topCard.getShape().toString().substring(0, 1) + " " : "_ ");
        }
        System.out.println("\nWählen Sie eine Karte zum Spielen:");
    
        // Zeige die Karten, die gespielt werden können.
        for (int i = 0; i < game.getHandCardsCount(); i++) {

            Card card = game.getHandCard(i);
            System.out.println((i + 1) + ": " + card.toString());
        }
    
        System.out.print(">>> ");
        int cardIndex = scanner.nextInt() - 1;
    
        if (cardIndex >= 0 && cardIndex < game.getHandCardsCount()) {

            Card card = game.getHandCard(cardIndex);

            System.out.println("Wählen Sie den gewünschten Stapel (1-" + game.getStacksCount() + ").");
            System.out.print(">>> ");

            int stackIndex = scanner.nextInt() - 1;
            if (stackIndex >= 0 && stackIndex < game.getStacksCount()) {
                
                game.play(card, stackIndex);
            } else {
                System.out.println("========================");
                System.out.println("\u001B[31m" + "Ungültige Stapelwahl." + "\u001B[0m");
            }
        } else {
            System.out.println("========================");
            System.out.println("\u001B[31m" + "Ungültige Kartenauswahl." + "\u001B[0m");
        }
    }
    

    @Override
    public void end(double credits) {
        System.out.println("Das Spiel ist zu Ende. Ihr endgültiger Punktestand ist: " + credits);
    }

    @Override
    public void run(Game game) {
        System.out.println("Ein neues Spiel beginnt!");
        while (game.getMode() != Mode.END) {

            if (game.getMode() == Mode.SHOPPING) {

                int shopItemIndex;
                do {
                    shopItemIndex = shop(game);
                    if (shopItemIndex >= 0 && shopItemIndex < game.getShopItemCount()) {

                        game.buy(shopItemIndex);
                        System.out.println("Gekauft: " + game.getShopItemDescription(shopItemIndex));
                    } else {
                        break;
                    }
                } while (true);

                game.endShopping();
            }
            while (game.getMode() == Mode.PLAYING) {

                playCard(game);
                if (game.getHandCardsCount() == 0) {

                    game.setMode(Mode.SHOPPING);
                }
            }
        }
        end(game.getCredits());
    }

    public static void main(String[] args) {
        Game game = new Game(2, new CardFactory(){
            @Override
            public Card createRandom() {

            Random random = new Random();
            String[] allCardTypes = {"Chance", "Paar", "Tripel"};
            Shape[] allCardShapes = Shape.values();

            String type = allCardTypes[random.nextInt(allCardTypes.length)];
            Shape shape = allCardShapes[random.nextInt(allCardShapes.length)];

            return create(type, shape);
            }
        });

        PlayerConsole player = new PlayerConsole();
        player.run(game);
    }
}
