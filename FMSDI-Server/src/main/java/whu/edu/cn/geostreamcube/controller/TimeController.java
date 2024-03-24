package whu.edu.cn.geostreamcube.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whu.edu.cn.geostreamcube.mapper.TimeMapper;

import java.util.List;

/**
 * @author Ruixiang Liu
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class TimeController {
    @Autowired
    TimeMapper timeMapper;

    @GetMapping("/getTimeKeyString")
    public List<String> getTimeKeyString(String startTime, String endTime) {
        return timeMapper.getTimeKeyString(startTime, endTime);
    }

}
