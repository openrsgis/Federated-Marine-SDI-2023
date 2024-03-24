package whu.edu.cn.geostreamcube.entity;

import java.util.Objects;

/**
 * @author Ruixiang Liu
 */
public class TimeLevel {
    private int timeLevelKey;
    private double resolution;
    private String unit;

    public int getTimeLevelKey() {
        return timeLevelKey;
    }

    public void setTimeLevelKey(int timeLevelKey) {
        this.timeLevelKey = timeLevelKey;
    }

    public double getResolution() {
        return resolution;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
