package whu.edu.cn.geostreamcube.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.geostreamcube.entity.Product;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Mapper
public interface ProductMapper {

    @Select("select * from product")
    List<Product> getAllProduct();

}
