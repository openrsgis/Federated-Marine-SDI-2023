package whu.edu.cn.geostreamcube.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import whu.edu.cn.geostreamcube.entity.Variable;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Mapper
public interface VariableMapper {

    @Select("select * from variable")
    List<Variable> getAllVariable();

    @Select("SELECT variable.* FROM variable join product_variable on variable.variable_key = product_variable.variable_key where product_key = ${ProductKey}")
    List<Variable> getAllVariableByProductKey(int ProductKey);

}
