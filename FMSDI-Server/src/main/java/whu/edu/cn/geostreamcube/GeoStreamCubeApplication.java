package whu.edu.cn.geostreamcube;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Ruixiang Liu
 */
@SpringBootApplication
@EnableScheduling
public class GeoStreamCubeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeoStreamCubeApplication.class, args);
    }

}
