package com.octave.intelligentinsole.entity;

import com.octave.intelligentinsole.R;
import com.octave.intelligentinsole.utils.TimeUtils;

import java.io.Serializable;

/**
 * Created by Paosin Von Scarlet on 2017/2/15.
 */

public class EntityOfHistory  implements Serializable {
    private static final long serialVersionUID = -7640467242182151398L;
    private int steps;
    private long startTime;// yyyy:MM:dd hh:mm:ss
    private long duration;// **/s
    private int distance;// km
    private int type;
    private int imgSrc;

    public EntityOfHistory(int steps, long startTime, long duration, int distance, int type) {
        this.steps = steps;
        this.startTime = startTime;
        this.duration = duration;
        this.distance = distance;
        this.type = type;
    }

    public int getImgSrc() {
        switch (type) {
            case 0:
                return R.drawable.ic_run;
            case 1:
                return R.drawable.ic_walk;
            case 2:
                return R.drawable.ic_check;
            default:
                return R.drawable.ic_close;
        }
    }
    public int getType(){
        return type;
    }
    public String getTypeString() {
        switch (type) {
            case 0:
                return "run";
            case 1:
                return "walk";
            case 2:
                return "sit";
            default:
                return null;
        }
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSteps() {
        return steps;
    }
    public String getStepsString(){
        return steps + "";
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getStartTimeString(){
        return TimeUtils.millisToYMDHMS(startTime);
    }

    public String getDurationString() {
        return TimeUtils.millisToHMS(duration);
    }
    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getDistance() {
        return distance+"";
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
