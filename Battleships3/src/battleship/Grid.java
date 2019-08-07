package battleship;

// necessary imports

import java.util.Vector;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import ships.Coordinate;
import ships.Ship;


/* 	This is the parent class of UserGrid and ComptuerGrid which provides functions to draw the grid
	on the screen and provides an abstract method to add the ship on grid which is implemented by UserGrid and ComptuerGrid
	UserGrid implements this method in such a way its the users to add the ships to grid
	and ComputerGrid implements this method in such a way that the ships are added automatically on the grid
*/

public abstract class Grid extends GridPane {

	// size of the grid
    private int gridSize;
    // the boxes of the grid are represented by these Labels
    private Label[][] gridLabels;
    // the ships on this grid
    private Vector<Ship> allShips;
    
    // constructor
    protected Grid(int size) {
        this.gridSize = size;
        gridLabels = new Label[size][size];
        allShips = new Vector<>();
        initializeGridLabels();
        setGridLinesVisible(true);
        

    }

    // make the boxes and initialize them with the default color
    private void initializeGridLabels() {

        for (int i = 0; i < gridLabels.length; i++) {
            for (int j = 0; j < gridLabels[i].length; j++) {
                gridLabels[i][j] = new Label();

                add(gridLabels[i][j], j, i);
                gridLabels[i][j].setPrefSize(400, 100);
                gridLabels[i][j].setStyle("-fx-background-color: grey;");
                gridLabels[i][j].setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                GridPane.setFillHeight(gridLabels[i][j], true);
                GridPane.setFillWidth(gridLabels[i][j], true);
                gridLabels[i][j].setId(String.format("%d %d", i, j));

            }
        }
    }

    /* 	this method is called by addShip which actually draws the 
    	ship at given X and Y Coordinates in a specific direction.
    	The last parameter tells that whether this ship should be visible or not.
    */
    protected boolean drawShipAtPoint(Ship s, int x, int y, 
            int direction, boolean colorful) {
        boolean shipWasDrawn = false;
        String colors[] = {"green", "red", "brown", "yellow"};
        
        // Draw the ship to the left of given coordinates
        if (4 == direction) {
        	// check that if the ship can be drawn at given coordinates or not 
            if (y - (s.getSize() - 1) >= 0) {
            	// check for overlap with another ship
                for (int i = 0; i < s.getSize(); i++) {
                    if (!gridLabels[x][y - i].getText().isEmpty()) {
                        return false;
                    }
                }
                
                // draw the ship
                for (int i = 0; i < s.getSize(); i++) {
                    gridLabels[x][y - i].setText(s.toString());
                    if(colorful){
                        gridLabels[x][y - i].setStyle(String.format("-fx-background-color: %s;", colors[s.getSize() - 2]));
                    }
                    
                    s.addShipCoordinates(x, y - i);
                }
                shipWasDrawn = true;
            }
        }
        // Draw the ship to the Bottom of given coordinates
        else if (3 == direction) {
        	// check that if the ship can be drawn at given coordinates or not
            if (x + (s.getSize() - 1) < gridSize) {
            	// check for overlap with another ship
                for (int i = 0; i < s.getSize(); i++) {
                    if (!gridLabels[x + i][y].getText().isEmpty()) {
                        return false;
                    }
                }
                // draw the ship
                for (int i = 0; i < s.getSize(); i++) {
                    gridLabels[x + i][y].setText(s.toString());
                    if(colorful){
                        gridLabels[x + i][y].setStyle(String.format("-fx-background-color: %s;", colors[s.getSize() - 2]));
                    }
                    
                    s.addShipCoordinates(x + i, y);
                }
                shipWasDrawn = true;
            }
        }
        // draw the ship to the Right from the given coordinates
        else if (2 == direction) {
        	// check that if the ship can be drawn at given coordinates or not
            if (y + (s.getSize() - 1) < gridSize) {
            	// check for overlap with any other ship
                for (int i = 0; i < s.getSize(); i++) {
                    if (!gridLabels[x][y + i].getText().isEmpty()) {
                        return false;
                    }
                }
                // draw the ship
                for (int i = 0; i < s.getSize(); i++) {
                    gridLabels[x][y + i].setText(s.toString());
                    if(colorful){
                        gridLabels[x][y + i].setStyle(String.format("-fx-background-color: %s;", colors[s.getSize() - 2]));
                    }
                    
                    s.addShipCoordinates(x, y + i);
                }
                shipWasDrawn = true;
            }
        } 
        // draw the ship towards TOP from the given coordinates
        else if (1 == direction) {
        	// check that if the ship can be drawn at given coordinates or not
            if (x - (s.getSize() - 1) >= 0) {
            	// check for overlap with another ship
                for (int i = 0; i < s.getSize(); i++) {
                    if (!gridLabels[x - i][y].getText().isEmpty()) {
                        return false;
                    }
                }
             // draw the ship
                for (int i = 0; i < s.getSize(); i++) {
                    gridLabels[x - i][y].setText(s.toString());
                    if(colorful){
                        gridLabels[x - i][y].setStyle(String.format("-fx-background-color: %s;", colors[s.getSize() - 2]));
                    }
                    
                    s.addShipCoordinates(x - i, y);
                }
                shipWasDrawn = true;
            }
        }
        return shipWasDrawn;
    }

    
    // adds the ships to the grid from the given list of ships. (Used while loading game state)
    public void setShips(Vector<Ship> shipsList, boolean colorStatus){
        for (int i = 0; i < shipsList.size(); i++) {
            Ship theShip = shipsList.get(i);
            Vector<Coordinate> coordinates = theShip.getAllShipsCoordinates();
            for (int j = 0; j < coordinates.size(); j++) {
                Coordinate point = coordinates.get(j);
                if(colorStatus){
                    gridLabels[point.getX()][point.getY()].setStyle("-fx-background-color: green;");
                }
                
            }
        }
        allShips = shipsList;
    }
    
    // returns the grid size
    protected int getGridSize(){
        return gridSize;
    }

    // returns the list of all ships on this grid
    protected Vector<Ship> getAllShipsList(){
        return allShips;
    }
    
    
    // return the Labels that is Boxes of this grid
    public Label[][] getAllLabels(){
        return gridLabels;
    }
    
    // Adds the ship to the grid. This function is to be implemented by child class
    public abstract void addShip(Ship s);
    
    
    
}
