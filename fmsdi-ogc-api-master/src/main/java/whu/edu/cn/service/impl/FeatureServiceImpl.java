package whu.edu.cn.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import whu.edu.cn.bean.Image;
import whu.edu.cn.bean.Vector;
import whu.edu.cn.entity.coverage.*;
import whu.edu.cn.entity.extent.Extent;
import whu.edu.cn.entity.extent.SpatialExtent;
import whu.edu.cn.entity.extent.TemporalExtent;
import whu.edu.cn.mapper.FeatureMapper;
import whu.edu.cn.service.FeatureService;
import whu.edu.cn.util.GeoUtil;
import whu.edu.cn.util.MinIOUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FeatureServiceImpl implements FeatureService {
    @Autowired
    private FeatureMapper featureMapper;

    @Resource
    private GeoUtil geoUtil;

    @Resource
    private MinIOUtil minIOUtil;

    private static final String OGE_PATH = "http://125.220.153.22:9006/oge/";


    @Override
    public CollectionInfo getCollectionById(String name) {
        CollectionInfo collectionInfo = new CollectionInfo();
        Vector vector = null;
        try {
            vector = featureMapper.getCollectionByName(name);

            collectionInfo.setTitle(vector.getTitle());
            List<String> crs = Arrays.asList(geoUtil.getCRSHref(vector.getCrs()));
            collectionInfo.setCrs(crs);

            List<List<Double>> bbox = new ArrayList<>();
            bbox.add(Arrays.asList(vector.getLowerLeftLong(), vector.getLowerLeftLat(), vector.getUpperRightLong(), vector.getUpperRightLat()));
            SpatialExtent spatialExtent = new SpatialExtent(bbox, geoUtil.getCRSHref(vector.getCrs()));
            TemporalExtent temporalExtent = new TemporalExtent(null);
            Extent extent = new Extent(spatialExtent, temporalExtent);
            collectionInfo.setExtent(extent);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return collectionInfo;
    }

    @Override
    public List<CollectionInfo> getCollection() {
        List<Vector> vectors = null;
        List<CollectionInfo> CollectionInfos = new ArrayList<>();

        try {
            vectors = featureMapper.getCollection();
            for (Vector vector : vectors) {
                CollectionInfo collectionInfo = new CollectionInfo();

                collectionInfo.setId(vector.getId());
                collectionInfo.setTitle(vector.getTitle());
                collectionInfo.setDescription(vector.getDescription());
                List<String> crs = Arrays.asList(geoUtil.getCRSHref(vector.getCrs()));
                collectionInfo.setCrs(crs);

                List<List<Double>> bbox = new ArrayList<>();
                bbox.add(Arrays.asList(vector.getLowerLeftLong(), vector.getLowerLeftLat(), vector.getUpperRightLong(), vector.getUpperRightLat()));
                SpatialExtent spatialExtent = new SpatialExtent(bbox, geoUtil.getCRSHref(vector.getCrs()));
                TemporalExtent temporalExtent = new TemporalExtent(null);
                Extent extent = new Extent(spatialExtent, temporalExtent);
                collectionInfo.setExtent(extent);

                CollectionInfos.add(collectionInfo);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CollectionInfos;
    }

    /**
     * 通过CollectionId下载文件
     *
     * @param name collection Id
     * @return 返回文件流
     * @throws IOException IO错误 出错则返回null
     */
    @Override
    public void downloadCollectionByName(String name, HttpServletResponse response) throws IOException {
        String bandNum = null;
        if (name.contains("-")) {
            bandNum = name.split("-")[1];
            name = name.split("-")[0];
        }
        Vector vector = null;
        try {
            vector = featureMapper.downloadCollectionsByName(name);
//            minIOUtil.downloadFile(vector, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject getCollectionItemsById(String collectionId) {
        JSONObject Items = new JSONObject();

        Vector vector = featureMapper.getCollectionByName(collectionId);
        String filePath = vector.getPath() + "/" + vector.getTitle() + ".geojson";

        try {
            Items = minIOUtil.getObjectAsJSONObject(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Items;
    }


}
