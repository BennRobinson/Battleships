
package battleship;

// necessary imports

import java.util.Random;
import java.util.Vector;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ships.Ship;

// the class which represents the ComptuerGrid
public class ComputerGrid extends Grid {

	// constructor
    public ComputerGrid(int size) {
        super(size);
        addActionListenerForEachLabel();
    }

    // this method adds the given Computer ship automatically to the grid
    @Override
    public void addShip(Ship s) {
        Vector<Ship> allShips = getAllShipsList();
        Random rand = new Random();
        int x;
        int y;

        int direction;
        do {
            int size = getGridSize();
            x = rand.nextInt(size);
            y = rand.nextInt(size);

            direction = rand.nextInt(4) + 1;
        } while (drawShipAtPoint(s, x, y, direction, true) != true);

        allShips.add(s);

    }

    
    /* 	sets action listener for every label on the computer grid 
     * so that when the user clicks on the computer grid to attack it
     * the action listener performs the necessary operations
    */
    private void addActionListenerForEachLabel() {
        Label[][] gridLabels = getAllLabels();
        for (int i = 0; i < gridLabels.length; i++) {
            for (int j = 0; j < gridLabels[i].length; j++) {
                gridLabels[i][j].setOnMousePressed(new MouseClickEvent());
            }
        }

    }

    // the action listener class which handles the user attack on computer grid
    private class MouseClickEvent implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {

        	// check if the game is initialized completely
            if (!Game.doAllShipsCreated()) {
                Alert userTurnAlert = new Alert(AlertType.INFORMATION);
                userTurnAlert.setTitle("Battleship Game");
                userTurnAlert.setHeaderText("Some ships still need to be added to your deck");
                userTurnAlert.setContentText("Please add all the ships to your fleet and then try to attack computer deck again.");
                userTurnAlert.showAndWait();
            } else {
            	/* if it is users turn then attack the computer ship as indicated by user
            	 * and prompt the message that what happened to the computer ships. Miss, Hit or Sink/Destroyed
            	*/
                if (!Game.isComputerTurn()) {

                    Vector<Ship> allShips = getAllShipsList();
                    Label[][] gridLabels = getAllLabels();
                    Label tempLabel = (Label) event.getSource();
                    String id = tempLabel.getId();

                    String gridId = "";
                    String message = "";
                    boolean doesUserWins = false;
                    if (id != null) {
                        String points[] = id.split(" ");
                        int x = Integer.parseInt(points[0]);
                        int y = Integer.parseInt(points[1]);
                        gridId = String.format("Target Choosen: (%d, %d)", x, y);
                        boolean shipWasHit = false;
                        Ship theShip = null;
                        for (int i = 0; !shipWasHit && i < allShips.size(); i++) {
                            theShip = allShips.get(i);
                            if (theShip.hasCoordinates(x, y)) {
                                shipWasHit = true;
                            }
                        }
                        if (theShip != null && shipWasHit) {
                            message = "A Computer Ship was Hit!";
                            gridLabels[x][y].setStyle("-fx-background-color: grey;");
                            theShip.hitShip();
                            theShip.removeShipCoordinates(x, y);
                            if (theShip.isDestroyed()) {
                                allShips.remove(theShip);
                                if (allShips.isEmpty()) {
                                    message = "User Wins!";
                                    doesUserWins = true;
                                } else {
                                    message = "A Computer Ship was Destroyed!!";
                                }
                            }
                        } else {
                            message = "Target Miss!";
                        }
                    } else {
                        message = "Target Miss!";
                    }
                    
                    // Prompts the result of user attack
                    Alert userTurnAlert = new Alert(AlertType.INFORMATION);
                    userTurnAlert.setTitle("User Turn");
                    userTurnAlert.setHeaderText(message);
                    userTurnAlert.setContentText(gridId);
                    userTurnAlert.showAndWait();
                    if (doesUserWins) {
                        System.exit(0);
                    }
                    Game.toggleTurn();
                    Game.getUserGrid().computerTurn();
                }
            }

        }
    }
}
