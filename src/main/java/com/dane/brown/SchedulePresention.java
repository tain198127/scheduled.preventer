package com.dane.brown;

import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
@Data
public class SchedulePresention {
    public SchedulePresention(Scheduled annotation){
        this.cron = annotation.cron();
        this.fixedDelay=annotation.fixedDelay();
        this.fixedDelayString = annotation.fixedDelayString();
        this.fixedRate = annotation.fixedRate();
        this.fixedRateString = annotation.fixedRateString();
        this.initialDelay = annotation.initialDelay();
        this.initialDelayString = annotation.initialDelayString();
        this.zone = annotation.zone();
    }
    private  String cron;
    private  String zone;
    private  long fixedDelay;
    private  String fixedDelayString;
    private  long fixedRate;
    private  String fixedRateString;
    private  long initialDelay;
    private  String initialDelayString;
}
