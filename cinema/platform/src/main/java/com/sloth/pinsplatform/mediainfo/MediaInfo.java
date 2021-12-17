package com.sloth.pinsplatform.mediainfo;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 15:57
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class MediaInfo {
    private String width;
    private String height;
    private String rotation;
    private long duration;

    public MediaInfo(String width, String height, String rotation, long duration) {
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        this.duration = duration;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        this.rotation = rotation;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
