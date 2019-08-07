// package name
package ships;

// necessary imports
import java.util.Vector;

// this is the parent class of all other Ships in the game

public class Ship {
	
	// size of the ship
    private int size;
    // the coordinates of the ship that is where the ship is located on Grid
    private Vector<Coordinate> shipCoordinates;
    
    public Ship(int s){
        size = s;
        shipCoordinates = new Vector<Coordinate>();
    }

    
    // returns the size of the ship
    public int getSize() {
        return size;
    }
    
    
    // add a coordinate of the ship
    public void addShipCoordinates(int x, int y){
        shipCoordinates.add(new Coordinate(x,y));
    }
    
    
    // remove a coordinate of the ship that is when ship is hit by an opponent
    public void removeShipCoordinates(int x, int y){
        for (int i = 0; i < shipCoordinates.size(); i++) {
            Coordinate shipCoordinate = shipCoordinates.get(i);
            if(shipCoordinate.getX() == x && shipCoordinate.getY() == y){
                shipCoordinates.remove(i);
                return;
            }
        }
    }
    
    // reduces the size of the ship when the ship is hit
    public void hitShip(){
        size--;
    }
    
    
    // checks that the ship has given coordinates or not
    public boolean hasCoordinates(int x, int y){
        for (Coordinate shipCoordinate : shipCoordinates) {
            if(shipCoordinate.getX() == x && shipCoordinate.getY() == y){
                return true;
            }
        }
        return false;
    }
    
    // tells that whether the ship has been destroyed or not
    public boolean isDestroyed(){
        return getSize() == 0;
    }
    
    // returns the list of all coordinates for this ship
    public Vector<Coordinate> getAllShipsCoordinates(){
        return shipCoordinates;
    }

    /* sets the coordinates of the ship to the given list. This method is very helpful when loading the game
     * the coordinates are read from the file and set through this method
    */
    public void setShipCoordinates(Vector<Coordinate> coordinates){
        shipCoordinates = coordinates;
    }
}
