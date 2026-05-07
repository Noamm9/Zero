package com.ricedotwho.zero.util.rotation;

import com.ricedotwho.zero.util.Box;
import com.ricedotwho.zero.util.Pos;

public class RotUtil {
    public static Pos getLookVector(Rot rot) {
        float yaw = (float) Math.toRadians(wrapAngleTo180(rot.getYaw()));
        float pitch = (float) Math.toRadians(rot.getPitch());

        double x = -Math.sin(yaw) * Math.cos(pitch);
        double y = -Math.sin(pitch);
        double z =  Math.cos(yaw) * Math.cos(pitch);

        return new Pos(x, y, z);
    }
    public static boolean rayIntersectsAABB(Pos origin, Pos direction, Box box, double maxDistance) {
        double tMin = 0.0;
        double tMax = maxDistance;

        // x
        if (Math.abs(direction.x()) < 1e-8) {
            if (origin.x() < box.getMinX() || origin.x() > box.getMaxX()) return false;
        } else {
            double inv = 1.0 / direction.x();
            double t1 = (box.getMinX() - origin.x()) * inv;
            double t2 = (box.getMaxX() - origin.x()) * inv;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return false;
        }

        // y
        if (Math.abs(direction.y()) < 1e-8) {
            if (origin.y() < box.getMinY() || origin.y() > box.getMaxY()) return false;
        } else {
            double inv = 1.0 / direction.y();
            double t1 = (box.getMinY() - origin.y()) * inv;
            double t2 = (box.getMaxY() - origin.y()) * inv;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return false;
        }

        // z
        if (Math.abs(direction.z()) < 1e-8) {
            return !(origin.z() < box.getMinZ()) && !(origin.z() > box.getMaxZ());
        } else {
            double inv = 1.0 / direction.z();
            double t1 = (box.getMinZ() - origin.z()) * inv;
            double t2 = (box.getMaxZ() - origin.z()) * inv;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            return !(tMin > tMax);
        }
    }
    public static double wrapAngleTo180(double angle) {
        angle = angle % 360.0;

        while (angle >= 180) {
            angle -= 360.0;
        }
        while (angle < -180.0) {
            angle += 360.0;
        };
        return angle;
    }
}
