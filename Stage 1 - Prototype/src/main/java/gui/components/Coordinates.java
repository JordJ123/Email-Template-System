package gui.components;

//import other.PythonObject;

import java.util.Objects;

/**
 * Class that represents coordinates.
 * @author Jordan Jones
 */
public class Coordinates {

    //CONSTANTS
    private static final String TO_STRING = "(%s, %s)";

    //Attributes
    private int x;
    private int y;

    /**
     * Creates a coordinates object.
     * @param x X Coordinate
     * @param y Y Coordinate
     */
    public Coordinates(int x, int y) {
        setX(x);
        setY(y);
    }

//    /**
//     * Converts a python coordinates object to a java coordinates object.
//     * @param pythonObject Python coordinates object
//     * @return Java coordinates object
//     */
//    public static Coordinates parsePythonObject(PythonObject pythonObject) {
//        int x = Integer.parseInt(pythonObject.executeMethod("getX").toString());
//        int y = Integer.parseInt(pythonObject.executeMethod("getY").toString());
//        return new Coordinates(x, y);
//    }

    /**
     * Sets x coordinate.
     * @param x X Coordinate
     */
    private void setX(int x) {
        this.x = x;
    }

    /**
     * Sets y coordinate.
     * @param y Y Coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets x coordinate.
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets y coordinate.
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if the given object the same coordinates.
     * @param obj Object to compare to
     * @return True if the coordinates are the same
     */
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates) {
            return getX() == ((Coordinates) obj).getX()
                && getY() == ((Coordinates) obj).getY();
        }
        return false;
    }

    /**
     * Gets the hash code of the coordinates.
     * @return Hash code of the coordinates
     */
    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }

    /**
     * Converts the coordinates to a string format.
     * @return String format of the coordinates
     */
    public String toString() {
        return (String.format(TO_STRING, getX(), getY()));
    }

}
