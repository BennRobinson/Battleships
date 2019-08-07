
package battleship;

import java.util.Random;

import java.util.Scanner;
import java.util.Vector;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import ships.Coordinate;
import ships.Ship;

// the class which represents the UserGrid

public class UserGrid extends Grid {

	// the points at which the computer targeted previously on UserGrid
    private Vector<Coordinate> previousTargets;
    // keeps track that whether the computer last attack was successful or not
    private boolean userShipWasHit;

    public UserGrid(int size) {
        super(size);
        previousTargets = new Vector<>();
        userShipWasHit = false;
    }

    // this method adds asks the user for the coordinates and adds the specified ship to UserGrid
    @Override
    public void addShip(Ship s) {
        Scanner keyboard = new Scanner(System.in);
        Vector<Ship> allShips = getAllShipsList();

        boolean shipWasDrawn = false;
        int x, y, d;
        
        // get the ship coordinates
        
        while (!shipWasDrawn) {
            do {
                System.out.print("Enter the initial X-Coordinates of this ship: ");
                x = keyboard.nextInt();
            } while (x < 0 || x >= getGridSize());

            do {
                System.out.print("Enter the initial Y-Coordinates of this ship: ");
                y = keyboard.nextInt();
            } while (y < 0 || y >= getGridSize());
            do {
                System.out.print("Enter the direction in which to draw ship from initial cooridnates\n1 = Top\n2 = Right\n3 = Bottom\n4 = Left\n>>> ");
                d = keyboard.nextInt();
            } while (d < 1 || d > 4);
            shipWasDrawn = drawShipAtPoint(s, x, y, d, true);
            if (!shipWasDrawn) {
                System.out.println("The ships coordinates overlaps with another ship or the ship can not be drawn in specified direction. Please enter coordinates again.");
            }
        }

        // add the ship to the user fleet
        allShips.add(s);
    }

    // this method is called by computer when it is going to attack user grid
    public void computerTurn() {
    	/* 	if it is computers turn then attack a user ship 
    		and prompt the message that what happened to the user ships. Miss, Hit or Sink/Destroyed
    	*/
        if (Game.isComputerTurn()) {

            Vector<Ship> allShips = getAllShipsList();
            Label[][] gridLabels = getAllLabels();
            Random rand = new Random();
            int size = getGridSize();

            int x;
            int y;

            /* pick a nearby point from the previous attack point if it was successful.
             * There is a chance the user ship will be hit again. 
             */
            
            if (userShipWasHit) {
                Coordinate temp = previousTargets.lastElement();
                x = temp.getX();
                y = temp.getY();

                int axis = rand.nextInt(2);
                int direction = rand.nextInt(2);

                if (axis == 0) {
                    if (direction == 0) {
                        x--;
                    } else {
                        x++;
                    }
                    if (x < 0 || x > size) {
                        x = rand.nextInt(size);
                    }

                } else {
                    if (direction == 0) {
                        y--;
                    } else {
                        y++;
                    }
                    if (y < 0 || y > size) {
                        y = rand.nextInt(size);
                    }
                }

            } else {
                x = rand.nextInt(size);
                y = rand.nextInt(size);
            }

            // if the point is already been attacked on then pick a new one
            
            int index = 0;
            while (index < previousTargets.size()) {
                Coordinate point = previousTargets.get(index);
                if (point.getX() == x && point.getY() == y) {
                    x = rand.nextInt(size);
                    y = rand.nextInt(size);
                    index = 0;
                } else {
                    index++;
                }
            }

            String gridId = String.format("Target Choosen: (%d, %d)", x, y);
            String message;
            boolean shipWasHit = false;
            Ship theShip = null;
            boolean doesComputerWins = false;
            
            // Do attack and check whether the user ship was hit, miss or destroyed
            
            for (int i = 0; !shipWasHit && i < allShips.size(); i++) {
                theShip = allShips.get(i);
                if (theShip.hasCoordinates(x, y)) {
                    shipWasHit = true;
                }
            }
            if (theShip != null && shipWasHit) {
                message = "A User Ship was Hit!";
                userShipWasHit = true;
                gridLabels[x][y].setStyle("-fx-background-color: grey;");
                theShip.hitShip();
                theShip.removeShipCoordinates(x, y);
                if (theShip.isDestroyed()) {
                    allShips.remove(theShip);
                    if (allShips.isEmpty()) {
                        message = "Computer Wins!";
                        doesComputerWins = true;
                    } else {
                        message = "A User Ship was Destroyed!";
                    }
                }
            } else {
                message = "Target Miss!";
                userShipWasHit = false;
            }
            previousTargets.add(new Coordinate(x, y));
            
            // Prompts the result of computer attack
            
            Alert computerTurnAlert = new Alert(Alert.AlertType.INFORMATION);
            computerTurnAlert.setTitle("Computer Turn");
            computerTurnAlert.setHeaderText(message);
            computerTurnAlert.setContentText(gridId);
            computerTurnAlert.showAndWait();
            if (doesComputerWins) {
                System.exit(0);
            }
            Game.toggleTurn();
        }
    }

}
