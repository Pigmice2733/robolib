package com.pigmice.frc.lib.pathfinder.field_loading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.pigmice.frc.lib.pathfinder.field_loading.Field.FieldConfig;
import com.pigmice.frc.lib.pathfinder.field_loading.SDFGenerator.Obstacle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.Filesystem;

public final class FieldParser {
    /**
     * Parses a field saved in a JSON file into a {@link Field} object
     * 
     * @param fieldName the name of the JSON file
     * @return a {@link Field} object containing data from the JSON file
     */
    public static Field parseField(String fieldName) {
        JSONParser parser = new JSONParser();

        JSONObject fieldData = null;
        try {
            fieldData = (JSONObject) parser.parse(
                    new FileReader(new File(Filesystem.getDeployDirectory(), "pathfinder/" + fieldName + ".json")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject configJSON = (JSONObject) fieldData.get("config");
        JSONArray obstaclesJSON = (JSONArray) fieldData.get("obstacles");

        ArrayList<Obstacle> obstacles = parseObstacles(obstaclesJSON);

        FieldConfig config = parseConfig(configJSON);

        obstacles.add(new SDFGenerator.FieldBoundaries(config.bottomLeftPositionMeters,
                config.bottomLeftPositionMeters.plus(config.fieldSizeMeters)));

        return new Field(config, obstacles);
    }

    /**
     * Parses through the obstacles in a saved field JSON
     * 
     * @param obstaclesJSON the JSON object containing the obstacles
     * @return an array of obstacles
     */
    private static ArrayList<Obstacle> parseObstacles(JSONArray obstaclesJSON) {
        ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();

        for (Object obstacle : obstaclesJSON) {
            obstacles.add(Obstacle.fromJSON((JSONObject) obstacle));
        }

        return obstacles;
    }

    /**
     * Parses through the config in a saved field JSON
     * 
     * @param config the JSON object containing field config
     * @return a {@link FieldConfig} object containing data from the JSON
     */
    private static FieldConfig parseConfig(JSONObject config) {
        JSONArray bottomLeftPositionMetersJSON = (JSONArray) config.get("bottomLeftPositionMeters");
        JSONArray fieldSizeMetersJSON = (JSONArray) config.get("fieldSizeMeters");

        Translation2d bottomLeftPositionMeters = new Translation2d(
                (double) bottomLeftPositionMetersJSON.get(0), (double) bottomLeftPositionMetersJSON.get(1));

        Translation2d fieldSizeMeters = new Translation2d(
                (double) fieldSizeMetersJSON.get(0), (double) fieldSizeMetersJSON.get(1));

        double nodeSpacingMeters = (double) config.get("nodeSpacingMeters");

        return new FieldConfig(bottomLeftPositionMeters, fieldSizeMeters, nodeSpacingMeters);
    }
}