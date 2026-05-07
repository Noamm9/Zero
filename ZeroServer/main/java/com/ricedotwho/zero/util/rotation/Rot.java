package com.ricedotwho.zero.util.rotation;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Rot {
    public float pitch;
    public float yaw;

    public Rot(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public Rot(Rot other) {
        this.pitch = other.getPitch();
        this.yaw = other.getYaw();
    }

    public float getValue() {
        return Math.abs(this.yaw) + Math.abs(this.pitch);
    }

    public void set(float pitch, float yaw) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public String toString() {
        return "Rotation{pitch=" + pitch +
                ", yaw=" + yaw + "}";
    }

    public String toPretty() {
        return "Pitch: " + pitch + ", Yaw: " + yaw;
    }

    public boolean equals(Rot other) {
        return this.pitch == other.getPitch() && this.yaw == other.getYaw();
    }
    public float distance() {
        return this.pitchSq() + this.yawSq();
    }
    public float yawSq() {
        return this.yaw * this.yaw;
    }
    public float pitchSq() {
        return this.pitch * this.pitch;
    }
}
