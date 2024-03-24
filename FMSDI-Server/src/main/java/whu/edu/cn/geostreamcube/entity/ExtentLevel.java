package whu.edu.cn.geostreamcube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Objects;

/**
 * @author Ruixiang Liu
 */
public class ExtentLevel {
    private int extentLevelKey;
    private int tileSize;
    private String geom;
    private String crs;
    private String orderType;
    private String globe;
    private double xRes;
    private double yRes;
    private int level;

    public int getExtentLevelKey() {
        return extentLevelKey;
    }

    public void setExtentLevelKey(int extentLevelKey) {
        this.extentLevelKey = extentLevelKey;
    }

    public int getTileSize() {
        return tileSize;
    }

    public void setTileSize(int tileSize) {
        this.tileSize = tileSize;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getGlobe() {
        return globe;
    }

    public void setGlobe(String globe) {
        this.globe = globe;
    }

    public double getxRes() {
        return xRes;
    }

    public void setxRes(double xRes) {
        this.xRes = xRes;
    }

    public double getyRes() {
        return yRes;
    }

    public void setyRes(double yRes) {
        this.yRes = yRes;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "ExtentLevel{" +
                "extentLevelKey=" + extentLevelKey +
                ", tileSize=" + tileSize +
                ", geom='" + geom + '\'' +
                ", crs='" + crs + '\'' +
                ", orderType='" + orderType + '\'' +
                ", globe='" + globe + '\'' +
                ", xRes=" + xRes +
                ", yRes=" + yRes +
                ", level=" + level +
                '}';
    }
}
