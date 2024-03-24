package whu.edu.cn.geostreamcube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import whu.edu.cn.geostreamcube.entity.Product;
import whu.edu.cn.geostreamcube.entity.Variable;
import whu.edu.cn.geostreamcube.mapper.VariableMapper;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class VariableController {
    @Autowired
    VariableMapper variableMapper;

    @GetMapping("/getAllVariable")
    public List<Variable> getAllVariable() {
        return variableMapper.getAllVariable();
    }

    @GetMapping("/getAllVariableByProductKey")
    public List<Variable> getAllVariableByProductKey(@RequestParam int productKey) {
        return variableMapper.getAllVariableByProductKey(productKey);
    }

}
