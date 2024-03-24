package whu.edu.cn.geostreamcube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whu.edu.cn.geostreamcube.entity.Product;
import whu.edu.cn.geostreamcube.mapper.ProductMapper;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {
    @Autowired
    ProductMapper productMapper;

    @GetMapping("/getAllProduct")
    public List<Product> getAllProduct() {
        return productMapper.getAllProduct();
    }

}
