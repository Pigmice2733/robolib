package com.pigmice.frc.lib.motion;

import java.util.ArrayList;

import com.pigmice.frc.lib.utils.Utils;

public class StaticProfile {

    private final ArrayList<Chunk> chunks;
    private double maxAccel, maxDecel, maxVelocity, startingPosition;
    private double profileDuration, profileDistance;

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

        profileDuration = 0.0;
        profileDistance = currentPosition;
        for (Chunk chunk : chunks) {
            profileDuration += chunk.getDuration();
            profileDistance += chunk.getTotalDistance();
        }

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

    public double getVelocity(double time) {
        return getSetpoint(time).getVelocity();
    }

    public double getPosition(double time) {
        return getSetpoint(time).getPosition();
    }

    public double getAcceleration(double time) {
        return getSetpoint(time).getAcceleration();
    }

    public double getDuration() {
        return profileDuration;
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

    public Setpoint getSetpoint(double time) {
        if (time >= profileDuration) {
            return getEndSetpoint(profileDistance);
        }

        while (time >= chunkEndTime) {
            advanceChunk();
        }

        return new Setpoint(chunk, time - chunkStartTime, previousDistance, 0.0, 0.0);
    }
}
