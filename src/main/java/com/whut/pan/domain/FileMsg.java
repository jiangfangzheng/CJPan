package com.whut.pan.domain;

/**
 * @author Sandeepin
 * 2018/2/11 0011
 */
public class FileMsg {
    private String name;
    private String link;
    private String size;
    private String time;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
