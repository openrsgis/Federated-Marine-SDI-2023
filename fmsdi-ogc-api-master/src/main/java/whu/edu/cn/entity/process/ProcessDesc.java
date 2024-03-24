package whu.edu.cn.entity.process;

import java.io.Serializable;
import java.util.List;

public class ProcessDesc implements Serializable {
    private String id; //name
    private String title;
    private String version;
    private List<String> jobControlOptions;
    private List<String> outputTransmission;
    List<Link> links;

    public ProcessDesc(String id, String title, String version,
                       List<String> jobControlOptions, List<String> outputTransmission, List<Link> links){
        this.id = id;
        this.title = title;
        this.version = version;
        this.jobControlOptions = jobControlOptions;
        this.outputTransmission = outputTransmission;
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getJobControlOptions() {
        return jobControlOptions;
    }

    public void setJobControlOptions(List<String> jobControlOptions) {
        this.jobControlOptions = jobControlOptions;
    }

    public List<String> getOutputTransmission() {
        return outputTransmission;
    }

    public void setOutputTransmission(List<String> outputTransmission) {
        this.outputTransmission = outputTransmission;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
