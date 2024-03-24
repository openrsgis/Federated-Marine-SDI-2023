package whu.edu.cn.entity.feature;

import java.util.List;

public class FeaturesGeoJson {
    public static final String TYPE = "Feature";
    private List<FeatureGeoJson> features;


    public static String getTYPE() {
        return TYPE;
    }

    public List<FeatureGeoJson> getFeatures() {
        return features;
    }

    public void setFeatures(List<FeatureGeoJson> features) {
        this.features = features;
    }
}
