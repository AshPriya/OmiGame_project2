package omiProject;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class GameController {

    private static int noOfPlayers = 4;
    private static int activePlayer = 0;
    private static String[] allCards = {"0_1", "0_2", "0_3", "0_4", "0_5", "0_6", "0_7", "0_8", "0_9", "0_10", "0_11", "0_12", "0_13",
        "1_1", "1_2", "1_3", "1_4", "1_5", "1_6", "1_7", "1_8", "1_9", "1_10", "1_11", "1_12", "1_13",
        "2_1", "2_2", "2_3", "2_4", "2_5", "2_6", "2_7", "2_8", "2_9", "2_10", "2_11", "2_12", "2_13",
        "3_1", "3_2", "3_3", "3_4", "3_5", "3_6", "3_7", "3_8", "3_9", "3_10", "3_11", "3_12", "3_13"};

    private static HashMap<Integer, String[]> playerCards = new HashMap<>();
    private static String[] cardsOnHand = new String[noOfPlayers];
    private static int trumpSuit;

    public static void deal() {
        Collections.shuffle(Arrays.asList(allCards));
        for (int i = 0; i < noOfPlayers; i++) {
            playerCards.put(i, new String[13]);
        }
        for (int i = 0; i < allCards.length; i++) {
            playerCards.get(i % noOfPlayers)[i / noOfPlayers] = allCards[i];
        }
        trumpSuit = Integer.parseInt(allCards[allCards.length - 1].split("_")[0]);
    }

    public static String[] getCards(int player) {
        return playerCards.get(player);
    }

    public static int getActivePlayer() {
        return activePlayer;
    }

    public static int playCard(String card, int player) {
        for (int i = 0; i < playerCards.get(player).length; i++) {
            if (playerCards.get(player)[i].equals(card)) {
                playerCards.get(player)[i] = null;
            }
        }
        cardsOnHand[player] = card;
        activePlayer++;
        if (activePlayer == noOfPlayers) {
            //Hand complete
            return 1;
        } else {
            return 0;
        }
    }

    public static int getWinner() {
        int score, max = 0, maxIndex = 0;

        for (int i = 0; i < noOfPlayers; i++) {
            int suit = Integer.parseInt(cardsOnHand[i].split("_")[0]);
            int card = Integer.parseInt(cardsOnHand[i].split("_")[1]);

            if (suit == trumpSuit) {
                score = 100 + card == 1 ? 13 : card - 1;
            } else {
                score = card == 1 ? 13 : card - 1;
            }

            if (score > max) {
                max = score;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public static String[] getCardsOnHand() {
        return cardsOnHand;
    }
}
