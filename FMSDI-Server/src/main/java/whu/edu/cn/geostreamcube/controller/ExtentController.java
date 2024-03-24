package whu.edu.cn.geostreamcube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whu.edu.cn.geostreamcube.mapper.ExtentMapper;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExtentController {

    @Autowired
    ExtentMapper extentMapper;

    @GetMapping("/getExtentKeyString")
    public List<String> getExtentKeyString(double startX, double startY, double endX, double endY, int extentLevelKey) {
        String geom = "POLYGON((" + startX + " " + startY + "," + startX + " " + endY + "," + endX + " " + endY + "," + endX + " " + startY + "," + startX + " " + startY + "))";
        return extentMapper.getExtentKeyString(geom, extentLevelKey);
    }

}
