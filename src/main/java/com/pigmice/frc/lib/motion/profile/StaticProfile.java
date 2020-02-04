package com.pigmice.frc.lib.motion.profile;

import java.util.ArrayList;

import com.pigmice.frc.lib.motion.setpoint.Setpoint;
import com.pigmice.frc.lib.utils.Utils;

public class StaticProfile implements IProfile {
    private final ArrayList<Chunk> chunks;
    private final double maxAccel, maxDecel, maxVelocity, startingPosition;
    private final double duration, distance;

    private int currentChunk = 0;
    private double chunkEndTime, chunkStartTime, previousDistance;

    private Chunk chunk;

    public StaticProfile(double currentVelocity, double currentPosition, double targetDistance, double maxVelocity,
            double maxAccel, double maxDecel) {
        final double targetDisplacement = targetDistance - currentPosition;
        startingPosition = currentPosition;
        this.maxAccel = maxAccel;
        this.maxDecel = maxDecel;
        this.maxVelocity = maxVelocity;

        chunks = computeChunks(new ArrayList<Chunk>(), currentVelocity, targetDisplacement);

        double totalDuration = 0.0;
        double totalDistance = currentPosition;
        for (Chunk chunk : chunks) {
            totalDuration += chunk.getDuration();
            totalDistance += chunk.getTotalDistance();
        }

        this.distance = totalDistance;
        this.duration = totalDuration;

        previousDistance = currentPosition;
        chunk = chunks.get(0);
        chunkEndTime = chunk.getDuration();
        chunkStartTime = 0.0;
    }

    private ArrayList<Chunk> computeChunks(ArrayList<Chunk> chunks, double currentVelocity, double remainingDistance) {
        final Chunk chunk;

        double stoppingDistance = 0.5 * (Math.abs(currentVelocity) / maxDecel) * currentVelocity;
        double targetDirection = Math.signum(remainingDistance);
        double currentDirection = Math.signum(currentVelocity);

        // If going in the wrong direction and at start of profile
        // --- After this check, remainingDistance, targetDirection, currentVelocity,
        // --- stoppingDistance, currentDirecton will all have the same sign
        if (currentDirection != targetDirection && currentVelocity != 0.0 && chunks.size() == 0) {
            // transition to stopped
            chunk = Chunk.createVelocityTransition(currentVelocity, 0.0, maxAccel, maxDecel);
        }
        // Else if going to overshoot and at start of profile
        else if (Math.abs(stoppingDistance) > Math.abs(remainingDistance) && chunks.size() == 0) {
            // transition to stopped
            chunk = Chunk.createVelocityTransition(currentVelocity, 0.0, maxAccel, maxDecel);
        }
        // Else if going faster than max speed
        else if (Math.abs(currentVelocity) - maxVelocity > 1e-6) {
            // transition to max speed
            chunk = Chunk.createVelocityTransition(currentVelocity, maxVelocity * targetDirection, maxAccel, maxDecel);
        }
        // Else if going slower than max speed
        else if (maxVelocity - Math.abs(currentVelocity) > 1e-6) {
            // If there is excess time to stop
            if (Math.abs(stoppingDistance) < Math.abs(remainingDistance)) {
                // transition to max speed
                chunk = Chunk.createVelocityTransition(currentVelocity, maxVelocity * targetDirection, maxAccel,
                        maxDecel);
            } else {
                // transiton to stopped
                chunk = Chunk.createVelocityTransition(currentVelocity, 0.0, maxAccel, maxDecel);
            }
        }
        // Otherwise, must be going at max speed
        else {
            // If there is excess time to stop
            if (Math.abs(stoppingDistance) < Math.abs(remainingDistance)) {
                // max velocity transition - continue at max speed for efficiency
                chunk = Chunk.createConstantVelocity(maxVelocity * targetDirection,
                        remainingDistance - stoppingDistance);
            }
            // Else if stopping distance == remaining distance
            else if (Utils.almostEquals(stoppingDistance, remainingDistance)) {
                // transition to stopped
                chunk = Chunk.createVelocityTransition(maxVelocity * targetDirection, 0.0, maxAccel, maxDecel);
            }
            // Else, not enough time to stop - must be triangular profile b/c overshoot
            // would have been handled already
            else {
                // Remove previous chunk
                remainingDistance += chunks.get(chunks.size() - 1).getTotalDistance();
                currentVelocity = chunks.get(chunks.size() - 1).getVelocity(0.0);
                stoppingDistance = 0.5 * (currentVelocity / maxDecel) * currentVelocity;
                targetDirection = Math.signum(remainingDistance);
                currentDirection = Math.signum(currentVelocity);
                chunks.remove(chunks.size() - 1);

                // Account for non-zero velocities going into triangular section of profile
                double precedingTriangleArea = 0.5 * (currentVelocity * currentVelocity) / maxAccel;
                double fullTriangleDistance = Math.abs(remainingDistance + precedingTriangleArea);

                // Calculate ratio of accel distance to full distance of triangular profile
                double fullAccelerationDistance = 0.5 * maxVelocity * (maxVelocity / maxAccel);
                double fullDecelerationDistance = 0.5 * maxVelocity * (maxVelocity / maxDecel);
                double ratio = fullAccelerationDistance / (fullAccelerationDistance + fullDecelerationDistance);

                double accelerationDistance = ratio * fullTriangleDistance;

                // Max speed robot can reach during this section of profile without overshooting
                double triangleMaxSpeed = Math.sqrt(2 * accelerationDistance * maxAccel) * targetDirection;

                chunks.add(Chunk.createVelocityTransition(currentVelocity, triangleMaxSpeed, maxAccel, maxDecel));
                chunks.add(Chunk.createVelocityTransition(triangleMaxSpeed, 0.0, maxAccel, maxDecel));

                // Target distance has been reached, return all chunks
                return chunks;
            }
        }

        chunks.add(chunk);
        if (!Utils.almostEquals(remainingDistance, chunk.getTotalDistance())) {
            // still have farther to go
            return computeChunks(chunks, chunk.getEndVelocity(), remainingDistance - chunk.getTotalDistance());
        }
        return chunks;
    }

    public void reset() {
        previousDistance = startingPosition;
        currentChunk = 0;
        chunk = chunks.get(0);
        chunkEndTime = chunk.getDuration();
        chunkStartTime = 0.0;
    }

    public double getDuration() {
        return duration;
    }

    private Setpoint getEndSetpoint(double distance) {
        return new Setpoint(distance, 0.0, 0.0, 0.0, 0.0);
    }

    private void advanceChunk() {
        chunkStartTime = chunkEndTime;
        chunk = chunks.get(currentChunk);
        previousDistance += chunk.getTotalDistance();

        currentChunk += 1;
        chunk = chunks.get(currentChunk);
        chunkEndTime += chunk.getDuration();
    }

    private static Setpoint createSetpoint(Chunk chunk, double time, double previousDistance) {
        double position = chunk.getPosition(time) + previousDistance;
        double velocity = chunk.getVelocity(time);
        double acceleration = chunk.getAcceleration();

        return new Setpoint(position, velocity, acceleration, 0.0, 0.0);
    }

    public Setpoint getSetpoint(double time) {
        if (time >= duration) {
            return getEndSetpoint(distance);
        }

        while (time >= chunkEndTime) {
            advanceChunk();
        }

        return createSetpoint(chunk, time - chunkStartTime, previousDistance);
    }
}
