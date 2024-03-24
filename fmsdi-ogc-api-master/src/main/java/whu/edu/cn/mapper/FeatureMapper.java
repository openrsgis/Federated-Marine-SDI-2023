package whu.edu.cn.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import whu.edu.cn.bean.Image;
import whu.edu.cn.bean.Vector;

import java.sql.Timestamp;
import java.util.List;

@Service
@Mapper
public interface FeatureMapper {

    /**
     * 返回全部collections
     */
    @Select("select t1.id as id, t1.product_name as productName, t1.path as path, t1.crs as crs, t1.title as title," +
            "t1.upper_right_lat as upperRightLat, t1.upper_right_long as upperRightLong, t1.lower_left_lat as lowerLeftLat, " +
            "t1.lower_left_long as lowerLeftLong, t1.description as description " +
            "from fmsdi_vector as t1")
    List<Vector> getCollection();

    /**
     * 根据CoverageId获取一个collections
     */
    @Select("select t1.id as id, t1.product_name as productName, t1.path as path, t1.crs as crs, t1.title as title," +
            "t1.upper_right_lat as upperRightLat, t1.upper_right_long as upperRightLong, t1.lower_left_lat as lowerLeftLat, " +
            "t1.lower_left_long as lowerLeftLong, t1.description as description " +
            "from fmsdi_vector as t1 where t1.id=#{name}")
    Vector getCollectionByName(@Param("name") String name);


    @Select("select t1.path as path,t1.title as title from fmsdi_vector as t1 where t1.title=#{name}")
    Vector downloadCollectionsByName(@Param("name") String name);
}
