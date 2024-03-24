package whu.edu.cn.geostreamcube.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Mapper
public interface TimeMapper {
    @Select("select time_key from time where timelevel_key = 1 and timestamp >= '${startTime}' and timestamp <= '${endTime}'")
    List<Integer> getTimeKey(String startTime, String endTime);

    @Select("select timestamp from time where timelevel_key = 1 and timestamp >= '${startTime}' and timestamp <= '${endTime}'")
    List<String> getTimeKeyString(String startTime, String endTime);

}
