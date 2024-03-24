package whu.edu.cn.geostreamcube.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import whu.edu.cn.geostreamcube.entity.MeteorologyTile;
import whu.edu.cn.geostreamcube.mapper.*;
import whu.edu.cn.geostreamcube.scala.application.Computation;
import whu.edu.cn.geostreamcube.scala.application.Visualization;


import java.util.List;

/**
 * @author Ruixiang Liu
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class MeteorologyTileController {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    VariableMapper variableMapper;
    @Autowired
    ExtentMapper extentMapper;
    @Autowired
    TimeMapper timeMapper;
    @Autowired
    MeteorologyTileMapper meteorologyTileMapper;

    @GetMapping("/getAllMeteorologyTile")
    public JSONObject getAllMeteorologyTile(int productKey, String variableKey, double startX, double startY, double endX, double endY, String startTime, String endTime, String UUID, int extentLevelKey, int cores, int partitions) {
        SparkConf conf = new SparkConf().setMaster("local[" + cores + "]").setAppName("query");
        SparkContext sc = new SparkContext(conf);
        try {
            Long time1 = System.currentTimeMillis();
            String geom = "POLYGON((" + startX + " " + startY + "," + startX + " " + endY + "," + endX + " " + endY + "," + endX + " " + startY + "," + startX + " " + startY + "))";
            List<Integer> extentKey = extentMapper.getExtentKey(geom, extentLevelKey);
            List<Integer> timeKey = timeMapper.getTimeKey(startTime, endTime);
            String variableKeyString = "(" + variableKey + ")";
            String extentKeyString = extentKey.toString().replace("[", "(").replace("]", ")");
            String timeKeyString = timeKey.toString().replace("[", "(").replace("]", ")");
            System.out.println("extentKeyString = " + extentKeyString);
            System.out.println("timeKeyString = " + timeKeyString);
            List<MeteorologyTile> meteorologyTile = meteorologyTileMapper.getMeteorologyTile(productKey, variableKeyString, extentKeyString, timeKeyString);
            Long time2 = System.currentTimeMillis();
            System.out.println("(time2-time1) = " + (time2 - time1));

            return Visualization.MeteorologyVis(sc, meteorologyTile, UUID, partitions);
        } finally {
            sc.stop();
        }
    }

    @GetMapping("/computeMeteorologyTile")
    public JSONObject computeMeteorologyTile(int productKey, String variableKey, double startX, double startY, double endX, double endY, String startTime, String endTime, String UUID, int extentLevelKey, int cores, int partitions) {
        SparkConf conf = new SparkConf().setMaster("local[" + cores + "]").setAppName("query");
        SparkContext sc = new SparkContext(conf);
        try {
            Long time1 = System.currentTimeMillis();
            String geom = "POLYGON((" + startX + " " + startY + "," + startX + " " + endY + "," + endX + " " + endY + "," + endX + " " + startY + "," + startX + " " + startY + "))";
            List<Integer> extentKey = extentMapper.getExtentKey(geom, extentLevelKey);
            List<Integer> timeKey = timeMapper.getTimeKey(startTime, endTime);
            String variableKeyString = "(" + variableKey + ")";
            String extentKeyString = extentKey.toString().replace("[", "(").replace("]", ")");
            String timeKeyString = timeKey.toString().replace("[", "(").replace("]", ")");
            System.out.println("extentKeyString = " + extentKeyString);
            System.out.println("timeKeyString = " + timeKeyString);
            List<MeteorologyTile> meteorologyTile = meteorologyTileMapper.getMeteorologyTile(productKey, variableKeyString, extentKeyString, timeKeyString);
            Long time2 = System.currentTimeMillis();
            System.out.println("(time2-time1) = " + (time2 - time1));

            return Computation.MeteorologyComputation(sc, meteorologyTile, UUID, partitions);
        } finally {
            sc.stop();
        }
    }

}
