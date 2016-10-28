package omiProject;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UserController extends HttpServlet {

    int userCount = 0;
    int noOfPlayers = 4;
    String[] userIDs = new String[noOfPlayers];
    String cardPrefix = "{\"image\": \"cards/";
    String cardSuffix = ".png\" }";

    private int getUser(String sessionID) {
        int currentUser = -1;

        for (int i = 0; i < noOfPlayers; i++) {
            if (userIDs[i] != null) {
                if (userIDs[i].equals(sessionID)) {
                    currentUser = i;
                    break;
                }
            }
        }
        return currentUser;
    }

    private String getUserCardsString(int currentUser) {
        String[] cards = GameController.getCards(currentUser);
        String cardString = "";
        for (String card : cards) {
            if (card != null) {
                cardString = cardString + ", " + cardPrefix + card + cardSuffix;
            }
        }

        return cardString.substring(2);
    }

    private String getCardsOnHandString(int player) {
        String cardsOnHandString = "";
        String[] cardsOnHand = GameController.getCardsOnHand();
        int index = 1;

        for (int i = 0; i < noOfPlayers; i++) {
            if (cardsOnHand[i] != null) {
                if (i == player) {
                    cardsOnHandString = cardsOnHandString + ", \"mycard\":\"cards/" + cardsOnHand[player] + ".png\"";
                } else {
                    cardsOnHandString = cardsOnHandString + ", \"card" + (index++) + "\":\"cards/" + cardsOnHand[i] + ".png\"";
                }
            }
        }
        if (cardsOnHand[player] != null) {
            cardsOnHandString = cardsOnHandString + ", \"mycard\":\"cards/" + cardsOnHand[player] + ".png\"";
        }
        return cardsOnHandString;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            String playing = request.getParameter("playing");
            HttpSession session = request.getSession();
            String sessionId = session.getId();
            int currentUser;

            // String cards = "{\"image\": \"cards/0_2.png\" },{\"image\": \"cards/1_2.png\"},{\"image\": \"cards/3_7.png\"}";
            if (playing.equals("false")) {
                //update requests
                if (userCount == noOfPlayers) {
                    //4 players have already connected
                    //check this user in userIds
                    currentUser = getUser(sessionId);

                    if (currentUser == -1) {
                        // send maximum number of players.
                        out.println("{\"playing\":false, \"cards\":[],\"showHand\" : false, \"showCards\" : false , \"message\" : \"4 players already connected.\"}");
                    } else {
                        //user requesting update
                        if (currentUser == GameController.getActivePlayer()) {
                            String cardsString = getUserCardsString(currentUser);
                            String cardsOnHand = getCardsOnHandString(currentUser);

                            out.println("{\"playing\":true, \"cards\":[" + cardsString + "]" + cardsOnHand + ",\"showHand\" : true, \"showCards\" : true ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Play your card.\"}");
                        } else {
                            String cardsString = getUserCardsString(currentUser);
                            String cardsOnHand = getCardsOnHandString(currentUser);

                            out.println("{\"playing\":false, \"cards\":[" + cardsString + "]" + cardsOnHand + ",\"showHand\" : true, \"showCards\" : true ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Wait for others to play\"}");
                        }
                    }
                } else {
                    //not enough playes
                    //check this user in userIds
                    currentUser = getUser(sessionId);

                    if (currentUser == -1) {
                        userIDs[userCount] = sessionId;
                        currentUser = userCount;
                        userCount++;

                        if (userCount == noOfPlayers) {
                            //Deal cards
                            GameController.deal();
                            //Send this users cards
                            String cardsString = getUserCardsString(currentUser);

                            out.println("{\"playing\":false, \"cards\":[" + cardsString + "],\"showHand\" : true, \"showCards\" : true ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Starting Game...\"}");
                        } else {
                            out.println("{\"playing\":false, \"cards\":[],\"showHand\" : false, \"showCards\" : false ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Waiting for others to connect. Only " + userCount + " players connected ..\"}");
                        }
                    } else {
                        out.println("{\"playing\":false, \"cards\":[],\"showHand\" : false, \"showCards\" : false ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Waiting for others to connect. Only " + userCount + " players connected ..\"}");
                    }
                }
            } else {
                //playCard requests
                currentUser = getUser(sessionId);

                String card = request.getParameter("card");
                
                int state = 0;

                if (card != null) {
                    card = card.split("/")[1].split(".p")[0];
                    System.out.println("card = " + card);
                    state = GameController.playCard(card, currentUser);
                }

                if(state == 1) {
                    int winnerOfTheTrick = GameController.getWinner();
                    String cardsString = getUserCardsString(currentUser);
                    String cardsOnHand = getCardsOnHandString(currentUser);

                    out.println("{\"playing\":false, \"cards\":[" + cardsString + "]" + cardsOnHand + ",\"showHand\" : true, \"showCards\" : true ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Winner of the trick is " + winnerOfTheTrick + "\"}");
                } else {
                    String cardsString = getUserCardsString(currentUser);
                    String cardsOnHand = getCardsOnHandString(currentUser);

                    out.println("{\"playing\":false, \"cards\":[" + cardsString + "]" + cardsOnHand + ",\"showHand\" : true, \"showCards\" : true ,\"userName\" : \"Player : " + currentUser + "\", \"message\" : \"Wait for others to play\"}");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
