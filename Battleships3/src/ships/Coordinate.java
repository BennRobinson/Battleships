
package ships;


/* this class represents a point .i.e. Coordinate on the grid
 * The coordinates are used to locate the ships on the grid
 */
public class Coordinate {
	
	// the X-Coordinate of the point
    private int x;
    // the Y-Coordinate of the point
    private int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    // this method returns the X-Component of the coordinate
    public int getX() {
        return x;
    }

    // this method returns the Y-Component of the coordinate
    public int getY() {
        return y;
    }
    
    
}
