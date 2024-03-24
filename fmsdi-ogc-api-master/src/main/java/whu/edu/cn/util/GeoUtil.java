package whu.edu.cn.util;

import org.springframework.stereotype.Component;
import whu.edu.cn.entity.ProductDao;
import whu.edu.cn.entity.Measurement;
import whu.edu.cn.entity.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GeoUtil {
    /**
     * Transform a spatial extent to WKT format.
     * @param minx 最小x
     * @param miny 最小y
     * @param maxx 最大x
     * @param maxy 最大y
     * @return 返回wkt
     */
    public String DoubleToWKT(double minx,double miny,double maxx,double maxy){
        return "POLYGON(("+minx+" "+miny+", "+minx+" "+maxy+", "+maxx+" "+maxy+", "+maxx+" "+miny+","+minx+" "+miny+"))";
    }

    /**
     * 根据crsCode获取对应的Href
     * @param crsCode 例如 EPSG：4326
     * @return href
     */
    public String getCRSHref(String crsCode){
        if(crsCode.equals("EPSG:4326")){
            return "http://www.opengis.net/def/crs/OGC/1.3/CRS84";
        }else{
            return null;
        }
    }

    public List<ProductDao> TransformProducts(List<Product> productList){
        List<ProductDao> productDaos = new ArrayList<ProductDao>();
        for(Product product:productList){
            List<Measurement> measurements= product.getMeasurements();
            if(measurements.size()>=1){
                for(Measurement measurement:measurements){
                    ProductDao productDao = new ProductDao();
                    productDao.setdType(measurement.getdType());
                    productDao.setMeasurementName(measurement.getMeasurementName());

                    productDao.setProductKey(product.getProductKey());
                    productDao.setProductName(product.getProductName());
                    productDao.setPlatformName(product.getPlatformName());
                    productDao.setSensorName(product.getSensorName());
                    productDao.setPhenomenonTime(product.getPhenomenonTime());
                    productDao.setResultTime(product.getResultTime());

                    productDao.setCrs(product.getCrs());

                    productDao.setImagingLength(product.getImagingLength());
                    productDao.setImagingWidth(product.getImagingWidth());
                    productDao.setLowerLeftLong(product.getLowerLeftLong());
                    productDao.setLowerLeftLat(product.getLowerLeftLat());
                    productDao.setLowerRightLat(product.getLowerRightLat());
                    productDao.setLowerRightLong(product.getLowerRightLong());
                    productDao.setUpperRightLong(product.getUpperRightLong());
                    productDao.setUpperRightLat(product.getUpperRightLat());
                    productDao.setUpperLeftLong(product.getUpperLeftLong());
                    productDao.setUpperLeftLat(product.getUpperLeftLat());
                    productDaos.add(productDao);
                }
            }
        }
        System.out.println(productDaos.size());
        Map<String,List<ProductDao>> collect = productDaos.stream().collect(Collectors.groupingBy(e->fetchGroupKey(e)));
        List<ProductDao> productDaoReturn = new ArrayList<ProductDao>();
        for(List<ProductDao> productDaos1:collect.values()){
            productDaoReturn.add(productDaos1.get(0));
        }
        return productDaoReturn;
    }

    public static String fetchGroupKey(ProductDao productDao){
        return productDao.getPhenomenonTime()+"#"+productDao.getProductName()+"#"+productDao.getLowerLeftLong()+"#"+
                productDao.getLowerLeftLat()+"#"+productDao.getUpperRightLong()+"#"+productDao.getUpperRightLat()+"$"+productDao.getMeasurementName();
    }
}
