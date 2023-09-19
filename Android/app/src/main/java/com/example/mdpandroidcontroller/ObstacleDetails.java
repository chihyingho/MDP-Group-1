package com.example.mdpandroidcontroller;

public class ObstacleDetails {
    enum ObstacleFace {
        UNKNOWN,
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
    private int[] coordinates = new int[]{-1, -1};
    private ObstacleFace face = ObstacleFace.UNKNOWN;
    // getter and setter methods
    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }

    public int[] getCoordinates() {
        return this.coordinates;
    }

    public void setObstacleFace(ObstacleFace face) {
        this.face = face;
    }

    public ObstacleFace getObstacleFace() {
        return this.face;
    }
}
