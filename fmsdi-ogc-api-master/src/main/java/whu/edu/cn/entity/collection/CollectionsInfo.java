package whu.edu.cn.entity.collection;


import com.fasterxml.jackson.annotation.JsonInclude;
import whu.edu.cn.entity.coverage.CollectionInfo;
import whu.edu.cn.entity.process.Link;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionsInfo {

    private List<CollectionInfo> collections;

    private List<Link> links;

    public List<CollectionInfo> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionInfo> collections) {
        this.collections = collections;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
