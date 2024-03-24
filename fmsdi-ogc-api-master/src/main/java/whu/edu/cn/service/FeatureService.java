package whu.edu.cn.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.coverage.DomainSet;
import whu.edu.cn.entity.coverage.RangeType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public interface FeatureService {
    /**
     * 根据CoverageId获取一个collections的元信息
     */
    CollectionInfo getCollectionById(String name);

    /**
     * 返回全部collections
     */
    List<CollectionInfo> getCollection();

    /**
     * 根据CoverageId下载一个collections
     *
     * @return
     */

    void downloadCollectionByName(String name, HttpServletResponse response) throws IOException;

    JSONObject getCollectionItemsById(String collectionId);
}
