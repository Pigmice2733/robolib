package com.pigmice.frc.lib.pathfinder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GridVisualizer {
    private static BufferedImage generatePathVisual(NodeGrid grid) {
        BufferedImage image = new BufferedImage(grid.getWidth(), grid.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (grid.getNodeAt(x, y).distFromObj > 0) {
                    image.setRGB(x, y, getIntFromColor(1, 0, 0));
                }
                else image.setRGB(x, y, getIntFromColor(1, 1, 1));
            }
        }
        return image;
    }

    public static void visualizePathAsPNG(NodeGrid grid, String outputLocation) {
        BufferedImage image = generatePathVisual(grid);
        
        File outputfile = new File("C:/Users/lucah/Desktop/test/test.png");
        try {
            ImageIO.write(image, "PNG", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getIntFromColor(float Red, float Green, float Blue){
        int R = Math.round(255 * Red);
        int G = Math.round(255 * Green);
        int B = Math.round(255 * Blue);
    
        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;
    
        return 0xFF000000 | R | G | B;
    }
}
