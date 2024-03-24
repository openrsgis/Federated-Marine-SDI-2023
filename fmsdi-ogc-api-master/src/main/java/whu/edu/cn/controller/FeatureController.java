package whu.edu.cn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import whu.edu.cn.entity.collection.CollectionsInfo;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.feature.Feature;
import whu.edu.cn.entity.process.Link;
import whu.edu.cn.service.FeatureService;

import javax.annotation.Resource;
import java.util.*;

/**
 * OGC API - Coverages
 */
@Api(tags = "OGE - OGC API - Features")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/features_api")
public class FeatureController {
    @Resource
    private FeatureService featureService;


    /**
     * Landing page of geocube coverage API.
     */
    @ApiOperation(value = "Landing page", notes = "Landing page")
    @GetMapping(value = "/")
    public Map<String, Object> getLandingPage() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", "OGE feature server");
        map.put("description", "OGE server implementing the OGC API - Features 1.0");

        List<Link> linkList = new ArrayList<>();
        Link self = new Link();
        self.setHref("http://oge.whu.edu.cn/ogcapi/features_api/");
        self.setRel("self");
        self.setType("application/json");
        self.setTitle("landing page");
        linkList.add(self);

        Link conformance = new Link();
        conformance.setHref("http://oge.whu.edu.cn/ogcapi/features_api/conformance");
        conformance.setRel("conformance");
        conformance.setType("application/json");
        conformance.setTitle("OGC API - Features conformance classes implemented by this server");
        linkList.add(conformance);

        Link processes = new Link();
        processes.setHref("http://oge.whu.edu.cn/ogcapi/features_api/collections");
        processes.setRel("data");
        processes.setType("application/json");
        processes.setTitle("The list of available collections");
        linkList.add(processes);

        map.put("links", linkList);
        return map;
    }

    /**
     * Information about standards that this API conforms to.
     */
    @ApiOperation(value = "Conformance class", notes = "Conformance class")
    @GetMapping(value = "/conformance")
    public Map<String, Object> getConformanceClasses() {
        Map<String, Object> map = new HashMap<>();
        List<String> comformList = new ArrayList<>();
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/core");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/json");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-1/1.0/conf/oas3");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/geodata");
        comformList.add("http://www.opengis.net/spec/ogcapi-common-2/1.0/conf/collections");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson");
        comformList.add("http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core");
        map.put("conformsTo", comformList);
        return map;
    }

    /**
     * Describe the collections in the dataset.
     *
     * @return
     */
    @ApiOperation(value = "Collections provided", notes = "Collections list")
    @GetMapping(value = "/collections")
    public CollectionsInfo getCollections() {
        CollectionsInfo collectionsInfo = new CollectionsInfo();
        List<CollectionInfo> collections = featureService.getCollection();
        collectionsInfo.setCollections(collections);

        List<Link> links = new ArrayList<>();
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections", "self", "application/json", "This document as JSON"));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections/items", "items", "application/geo+json", null));
        collectionsInfo.setLinks(links);
        return collectionsInfo;
    }

    /**
     * Describe the collections in the dataset.
     *
     * @return
     */
    @ApiOperation(value = "Collections provided", notes = "Collections list")
    @GetMapping(value = "/collections/{collectionId}")
    public CollectionInfo getCollections(@PathVariable("collectionId") String collectionId) {
        CollectionInfo collectionInfo = featureService.getCollectionById(collectionId);
        List<Link> links = new ArrayList<>();
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections", "self", "application/json", "This document as JSON"));
        collectionInfo.setLinks(links);
        return collectionInfo;
    }

    /**
     * Describe the collections in the dataset.
     *
     * @return
     */
    @ApiOperation(value = "Collection provided", notes = "Collections list")
    @GetMapping(value = "/collections/{collectionId}/items")
    public JSONObject getFeatureCollection(@PathVariable("collectionId") String collectionId) {
        JSONObject features = new JSONObject();
        JSONObject collectionItems = featureService.getCollectionItemsById(collectionId);
        features.put("features", collectionItems.getJSONArray("features"));

        List<Link> links = new ArrayList<>();
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections/" + collectionId + "/items", "self", "application/geo+json", null));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections/" + collectionId, "collection", "application/json", null));
        features.put("links", links);
        features.put("type", "FeatureCollection");
        return features;
    }

    /**
     * Describe the collections in the dataset.
     *
     * @return
     */
    @ApiOperation(value = "The item of collection provided", notes = "Item of the collection")
    @GetMapping(value = "/collections/{collectionId}/items/{itemId}")
    public JSONObject getCollectionInfo(@PathVariable("collectionId") String collectionId, @PathVariable("itemId") String itemId) {
        // 返回特定的collectionId的collection,

        JSONObject collectionItems = featureService.getCollectionItemsById(collectionId);
        JSONArray features = collectionItems.getJSONArray("features");
        JSONObject feature = null;

        // 遍历features数组以查找匹配的ID
        for (int i = 0; i < features.size(); i++) {
            JSONObject featureJSONObject = features.getJSONObject(i);
            Integer id = featureJSONObject.getInteger("id");

            if (id.equals(Integer.valueOf(itemId))) {
                // 创建新的JSONObject并填充所需的信息
                feature = new JSONObject();
                feature.put("type", featureJSONObject.getString("type"));
                feature.put("id", id);

                // 处理geometry字段
                feature.put("geometry", featureJSONObject.getJSONObject("geometry"));

                // 复制properties字段
                feature.put("properties", featureJSONObject.getJSONObject("properties"));
                break;
            }
        }


        List<Link> links = new ArrayList<>();
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections/" + collectionId + "/items/" + itemId, "self", "application/geo+json", null));
        links.add(new Link("http://oge.whu.edu.cn/ogcapi/features_api/collections/" + collectionId, "collection", "application/json", null));
        feature.put("links", links);
        return feature;
    }

}
