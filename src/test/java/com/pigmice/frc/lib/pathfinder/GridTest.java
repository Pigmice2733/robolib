package com.pigmice.frc.lib.pathfinder;

import org.junit.jupiter.api.Test;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Filesystem;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GridTest {
    private static final double epsilon = 1e-6;

    // private static final NodeGrid grid = new NodeGrid(new Translation2d(-1.5,
    // -1.5), new Translation2d(1.5, 1.5), 0.5, createTestDistanceMap());

    @Test
    public void SizeTest() {

    }

    private static BufferedImage createTestDistanceMap() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(Filesystem.getDeployDirectory(),
                    "C:\\Programming\\Robotics\\Projects\\robolib\\src\\test\\java\\com\\pigmice\\frc\\lib\\pathfinder\\TestDistanceMap.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
