package whu.edu.cn.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vector {
    private String id;
    private String productName;
    private String path;
    private String createBy;
    private Timestamp createTime;
    private String updateBy;
    private Timestamp updateTime;
    private String title;
    private String crs;
    private String extent;
    private Double lowerLeftLat;
    private Double lowerLeftLong;
    private Double upperRightLat;
    private Double upperRightLong;
    private String description;
}
