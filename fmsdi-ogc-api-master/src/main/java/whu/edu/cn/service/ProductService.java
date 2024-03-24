package whu.edu.cn.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.entity.NameDao;
import whu.edu.cn.entity.MeasurementName;
import whu.edu.cn.entity.Product;
import whu.edu.cn.mapper.ProductMapper;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * Product service class
 */
@Service
public class ProductService {
    @Resource
    ProductMapper productMapper;

    /**
     * Get all products info.
     *
     * @return
     */
    public List<Product> getAllProducts() {
        List<Product> products = productMapper.getAllProducts();
        products.forEach(product -> System.out.println(product.toString()));

        products.forEach(product -> product.setMeasurements(productMapper.getMeasurements(Integer.parseInt(product.getProductKey()))));
        return products;
    }

    /**
     * Get product measurements by product name.
     *
     * @param name
     * @return
     */
    public List<MeasurementName> getMeasurementsByName(String name){
        List<MeasurementName> measurements = productMapper.getMeasurementNamesByName(name);
        return measurements;
    }

    /**
     * Get raster product info by name.
     * @param ProductName
     * @return
     */
    public List<Product> getProductsByName(String ProductName){
        List<Product> products=productMapper.getEOProductsByName(ProductName);
        products.forEach(product -> product.setMeasurements(productMapper.getMeasurements(Integer.parseInt(product.getProductKey()))));
        return products;
    }

    /**
     * Get all product names.
     * @return
     */
    public List<NameDao> getAllProductNames(){
        return productMapper.getAllProductNames();
    }

    /**
     * Get product info by product name, space, time and measurements.
     *
     * @param ProductName
     * @param StartTime
     * @param EndTime
     * @param WKT
     * @return
     */
    public List<Product> getProductsByParams(String ProductName, Timestamp StartTime, Timestamp EndTime, String WKT) {
        List<Product> products = productMapper.getProductsByParams(ProductName,StartTime,EndTime,WKT);
        products.forEach(product -> product.setMeasurements(productMapper.getMeasurements(Integer.parseInt(product.getProductKey()))));
        return  products;
    }

}
