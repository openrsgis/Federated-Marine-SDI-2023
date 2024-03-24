package whu.edu.cn.geostreamcube.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.geostreamcube.entity.MeteorologyTile;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Mapper
public interface MeteorologyTileMapper {
    @Select("select * from meteorology_tile_fact where product_key = ${productKey} and variable_key in ${variableKey} and extent_key in ${extentKey} and time_key in ${timeKey}")
    List<MeteorologyTile> getMeteorologyTile(int productKey, String variableKey, String extentKey, String timeKey);
}
