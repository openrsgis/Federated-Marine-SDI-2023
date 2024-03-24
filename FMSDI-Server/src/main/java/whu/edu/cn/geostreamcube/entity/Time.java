package whu.edu.cn.geostreamcube.entity;

/**
 * @author Ruixiang Liu
 */
public class Time {
    private int timeKey;
    private int timeLevelKey;
    private String timeStamp;

    public int getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(int timeKey) {
        this.timeKey = timeKey;
    }

    public int getTimeLevelKey() {
        return timeLevelKey;
    }

    public void setTimeLevelKey(int timeLevelKey) {
        this.timeLevelKey = timeLevelKey;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Time{" +
                "timeKey=" + timeKey +
                ", timeLevelKey=" + timeLevelKey +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }
}
