package gui.components;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Class that represents an extension for a grid pane.
 * @author Jordan Jones
 */
public class GridPaneExtension {

    //CONSTANTS
    private static final String NODE_ERROR = "There is no node at the given "
        + "coordinate %s";

    //Attributes
    private GridPane gridPane;

    /**
     * Creates a grid pane extension object.
     * @param gridPane Base grid pane
     */
    public GridPaneExtension(GridPane gridPane) {
        setGridPane(gridPane);
    }

    /**
     * Sets the base grid pane.
     * @param gridPane Base grid pane
     */
    private void setGridPane(GridPane gridPane) {
        this.gridPane = gridPane;
    }

    /**
     * Gets the nodes of the grid pane.
     * @return Nodes of the grid pane
     */
    public Node[] getNodes() {
        return gridPane.getChildren().toArray(new Node[]{});
    }

    /**
     * Gets the node of the given coordinates from the grid pane.
     * @param coordinates Coordinates of the node
     * @return Node of the given coordinates
     * @throws IllegalArgumentException Thrown if node does not exist
     */
    public Node getNode(Coordinates coordinates) {
        for (Node node : gridPane.getChildren()) {
            if (coordinates.equals(getNodeCoordinates(node))) {
                return node;
            }
        }
        throw new IllegalArgumentException(String.format(NODE_ERROR,
            coordinates));
    }

    /**
     * Gets the coordinates of the given node.
     * @param node Node from the grid pane
     * @return Coordinates of the given node
     */
    public Coordinates getNodeCoordinates(Node node) {
        int nodeX;
        try {
            nodeX = GridPane.getColumnIndex(node);
        } catch (NullPointerException error) {
            nodeX = 0;
        }
        int nodeY;
        try {
            nodeY = GridPane.getRowIndex(node);
        } catch (NullPointerException error) {
            nodeY = 0;
        }
        return new Coordinates(nodeX, nodeY);
    }

}
