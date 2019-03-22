package com.dane.brown;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
@ConfigurationProperties(prefix = "com.dane.brown.scheduled.preventer")
@Component
public class PreventAutoRunScheduledConfig {
    public List<String> getPreventRegex() {
        return preventRegex;
    }

    public void setPreventRegex(List<String> preventRegex) {
        this.preventRegex = preventRegex;
    }

    private List<String> preventRegex = new LinkedList<>();
}
