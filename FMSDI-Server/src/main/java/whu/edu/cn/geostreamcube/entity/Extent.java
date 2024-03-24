package whu.edu.cn.geostreamcube.entity;

/**
 * @author Ruixiang Liu
 */
public class Extent {
    private int extentKey;
    private int extentLevelKey;
    private String geom;
    private double upperLeftLon;
    private double upperLeftLat;
    private double upperRightLon;
    private double upperRightLat;
    private double lowerLeftLon;
    private double lowerLeftLat;
    private double lowerRightLon;
    private double lowerRightLat;

    public int getExtentKey() {
        return extentKey;
    }

    public void setExtentKey(int extentKey) {
        this.extentKey = extentKey;
    }

    public int getExtentLevelKey() {
        return extentLevelKey;
    }

    public void setExtentLevelKey(int extentLevelKey) {
        this.extentLevelKey = extentLevelKey;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
    }

    public double getUpperLeftLon() {
        return upperLeftLon;
    }

    public void setUpperLeftLon(double upperLeftLon) {
        this.upperLeftLon = upperLeftLon;
    }

    public double getUpperLeftLat() {
        return upperLeftLat;
    }

    public void setUpperLeftLat(double upperLeftLat) {
        this.upperLeftLat = upperLeftLat;
    }

    public double getUpperRightLon() {
        return upperRightLon;
    }

    public void setUpperRightLon(double upperRightLon) {
        this.upperRightLon = upperRightLon;
    }

    public double getUpperRightLat() {
        return upperRightLat;
    }

    public void setUpperRightLat(double upperRightLat) {
        this.upperRightLat = upperRightLat;
    }

    public double getLowerLeftLon() {
        return lowerLeftLon;
    }

    public void setLowerLeftLon(double lowerLeftLon) {
        this.lowerLeftLon = lowerLeftLon;
    }

    public double getLowerLeftLat() {
        return lowerLeftLat;
    }

    public void setLowerLeftLat(double lowerLeftLat) {
        this.lowerLeftLat = lowerLeftLat;
    }

    public double getLowerRightLon() {
        return lowerRightLon;
    }

    public void setLowerRightLon(double lowerRightLon) {
        this.lowerRightLon = lowerRightLon;
    }

    public double getLowerRightLat() {
        return lowerRightLat;
    }

    public void setLowerRightLat(double lowerRightLat) {
        this.lowerRightLat = lowerRightLat;
    }

    @Override
    public String toString() {
        return "Extent{" +
                "extentKey=" + extentKey +
                ", extentLevelKey=" + extentLevelKey +
                ", geom='" + geom + '\'' +
                ", upperLeftLon=" + upperLeftLon +
                ", upperLeftLat=" + upperLeftLat +
                ", upperRightLon=" + upperRightLon +
                ", upperRightLat=" + upperRightLat +
                ", lowerLeftLon=" + lowerLeftLon +
                ", lowerLeftLat=" + lowerLeftLat +
                ", lowerRightLon=" + lowerRightLon +
                ", lowerRightLat=" + lowerRightLat +
                '}';
    }
}
