package com.one.framework.app.web.plugin.model;

import java.util.ArrayList;
import java.util.List;

public class Plugin {
    private String topic;
    private List<String> clss;

    public Plugin(String topic, List<String> clss) {
        this.topic = topic;
        this.clss = clss;
    }

    public Plugin(String topic, Class cls) {
        this.clss = new ArrayList<String>();
        this.topic = topic;
        clss.add(cls.getName());
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getClss() {
        return clss;
    }

    public void setClss(List<String> clss) {
        this.clss = clss;
    }
}
