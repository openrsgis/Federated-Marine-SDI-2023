package whu.edu.cn.geostreamcube.entity;

import java.io.Serializable;

/**
 * @author Ruixiang Liu
 */
public class MeteorologyTile implements Serializable {
    private int id;
    private int imageKey;
    private int extentKey;
    private int timeKey;
    private int variableKey;
    private String tilePath;
    private int productKey;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageKey() {
        return imageKey;
    }

    public void setImageKey(int imageKey) {
        this.imageKey = imageKey;
    }

    public int getExtentKey() {
        return extentKey;
    }

    public void setExtentKey(int extentKey) {
        this.extentKey = extentKey;
    }

    public int getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(int timeKey) {
        this.timeKey = timeKey;
    }

    public int getVariableKey() {
        return variableKey;
    }

    public void setVariableKey(int variableKey) {
        this.variableKey = variableKey;
    }

    public String getTilePath() {
        return tilePath;
    }

    public void setTilePath(String tilePath) {
        this.tilePath = tilePath;
    }

    public int getProductKey() {
        return productKey;
    }

    public void setProductKey(int productKey) {
        this.productKey = productKey;
    }

    @Override
    public String toString() {
        return "MeteorologyTile{" +
                "id=" + id +
                ", imageKey=" + imageKey +
                ", extentKey=" + extentKey +
                ", timeKey=" + timeKey +
                ", variableKey=" + variableKey +
                ", tilePath='" + tilePath + '\'' +
                ", productKey=" + productKey +
                '}';
    }
}
