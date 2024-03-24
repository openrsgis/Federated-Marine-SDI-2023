package whu.edu.cn.geostreamcube.entity;

/**
 * @author Ruixiang Liu
 */
public class Product {
    private int productKey;
    private String productName;
    private String productType;
    private String label;
    private int sensorKey;

    public int getProductKey() {
        return productKey;
    }

    public void setProductKey(int productKey) {
        this.productKey = productKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSensorKey() {
        return sensorKey;
    }

    public void setSensorKey(int sensorKey) {
        this.sensorKey = sensorKey;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productKey=" + productKey +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", label='" + label + '\'' +
                ", sensorKey=" + sensorKey +
                '}';
    }
}
