package com.ricedotwho.zero.util;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Box {
    private double maxX;
    private double maxY;
    private double maxZ;
    private double minX;
    private double minY;
    private double minZ;

    public Box(double maxX, double maxY, double maxZ, double minX, double minY, double minZ) {
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        this.maxZ = Math.max(maxZ, minZ);

        this.minX = Math.min(maxX, minX);
        this.minY = Math.min(maxY, minY);
        this.minZ = Math.min(maxZ, minZ);
    }

    public Box(Pos max, Pos min) {
        this.maxX = Math.max(max.x(), min.x());
        this.maxY = Math.max(max.y(), min.y());
        this.maxZ = Math.max(max.z(), min.z());

        this.minX = Math.min(max.x(), min.x());
        this.minY = Math.min(max.y(), min.y());
        this.minZ = Math.min(max.z(), min.z());
    }

    public Pos getMax() {
        return new Pos(maxX, maxY, maxZ);
    }

    public Pos getMin() {
        return new Pos(minX, minY, minZ);
    }
}
