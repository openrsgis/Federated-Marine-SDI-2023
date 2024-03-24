package whu.edu.cn.geostreamcube.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Mapper
public interface ExtentMapper {
    @Insert("insert into extent(extent_key, extent_level_key, extent_z_order_key) VALUES(${extentKey}, ${extentLevelKey}, ${extentZOrderKey})")
    void insertExtent(int extentKey, int extentLevelKey, int extentZOrderKey);

    @Select("select extent_key from extent join extentlevel on extent.extentlevel_key = extentlevel.extentlevel_key where crs='EPSG:4326' and globe = 1 and level = ${extentLevelKey} and st_intersects(extent.geom, st_geomfromtext('${geom}', 4326))")
    List<Integer> getExtentKey(String geom, int extentLevelKey);

    @Select("select extent_key from extent join extentlevel on extent.extentlevel_key = extentlevel.extentlevel_key where crs='EPSG:4326' and globe = 1 and level = ${extentLevelKey} and st_intersects(extent.geom, st_geomfromtext('${geom}', 4326))")
    List<String> getExtentKeyString(String geom, int extentLevelKey);

}
