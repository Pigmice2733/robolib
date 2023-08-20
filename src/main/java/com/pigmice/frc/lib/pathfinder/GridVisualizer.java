package com.pigmice.frc.lib.pathfinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pigmice.frc.lib.utils.Utils;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory.State;

public class GridVisualizer {
    private final NodeGrid grid;
    private final BufferedImage image;

    /**
     * Creates a new grid visual and adds obstacles
     * 
     * @param pathfinder the pathfinder to get a grid from to visualize
     */
    public GridVisualizer(Pathfinder pathfinder) {
        this.grid = pathfinder.grid;
        this.image = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);

        addObstacles();
    }

    /**
     * Saves this image as a PNG
     * 
     * @param outputLocation the complete file location to save at including
     *                       FileName.PNG
     */
    public void saveAsPNG(String outputLocation) {
        File outputfile = new File(outputLocation);
        try {
            ImageIO.write(image, "PNG", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved to " + outputLocation);
    }

    /**
     * Adds obstacles to the image covering the entire grid
     * 
     * @return this instance to allow chaining methods
     */
    private GridVisualizer addObstacles() {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Node node = grid.getNodeAt(x, y);
                if (node.driveable) {
                    double col = Utils.lerp(0.5, 0.2, node.distanceWeight);
                    image.setRGB(x, y, getIntFromColor(col, col, col));
                } else {
                    if (node.edgeToCenterDistance < 0)
                        image.setRGB(x, y, getIntFromColor(0, 0, 0));
                    else
                        image.setRGB(x, y, getIntFromColor(.3, .3, .3));
                }

            }
        }
        return this;
    }

    /**
     * Adds waypoints to the image
     * 
     * @param result a {@link PathfinderResult} containing waypoints to add
     * @return this instance to allow chaining methods
     */
    public GridVisualizer addWaypoints(PathfinderResult result) {
        for (Translation2d waypoint : result.getPositionList()) {
            Node node = grid.FindCloseNode(waypoint);
            image.setRGB(node.gridX, node.gridY, getIntFromColor(0, 1, 0));
        }
        return this;
    }

    /**
     * Adds a trajectorys points to the image
     * 
     * @param trajectory a {@link PathPlannerTrajectory} to add to the image
     * @return this instance to allow chaining methods
     */
    public GridVisualizer addTrajectory(PathPlannerTrajectory trajectory) {
        for (State state : trajectory.getStates()) {
            Node node = grid.FindCloseNode(state.poseMeters.getTranslation());
            image.setRGB(node.gridX, node.gridY, getIntFromColor(1, 0, 0));
        }
        return this;
    }

    /**
     * Adds a single point to the image
     * 
     * @param fieldPosition the position in field space to draw the point
     * @return this instance to allow chaining methods
     */
    public GridVisualizer addPoint(Translation2d fieldPosition) {
        Node node = grid.FindCloseNode(fieldPosition);
        image.setRGB(node.gridX, node.gridY, getIntFromColor(1, 0, 0));
        return this;
    }

    /**
     * Converts RGB values to an integer representing a color
     * 
     * @param Red   the red component of the color (0.0 - 1.0)
     * @param Green the green component of the color (0.0 - 1.0)
     * @param Blue  the blue component of the color (0.0 - 1.0)
     * @return an integer representing a color
     */
    public static int getIntFromColor(double Red, double Green, double Blue) {
        int R = (int) Math.round(255 * Red);
        int G = (int) Math.round(255 * Green);
        int B = (int) Math.round(255 * Blue);

        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return 0xFF000000 | R | G | B;
    }
}
