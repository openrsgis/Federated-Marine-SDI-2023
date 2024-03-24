package whu.edu.cn.geostreamcube.service;

import org.springframework.stereotype.Service;
import whu.edu.cn.geostreamcube.mapper.ExtentMapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Ruixiang Liu
 */
@Service
public class ExtentService {
    @Resource
    ExtentMapper extentMapper;
}
