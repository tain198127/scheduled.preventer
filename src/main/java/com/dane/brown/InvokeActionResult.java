package com.dane.brown;

import lombok.Data;

import java.util.Date;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
@Data
public class InvokeActionResult {
    private int status;
    private String message;
    /**
     * 最后一次开始时间
     */
    private Date lastBeginTime;
    /**
     * 最后一次结束时间
     */
    private Date lastFinishTime;
    /**
     * 当前状态
     */
    private RunnableHolder.ScheduledStatusEnum currentStatus;
    /**
     * 更加细节的数据
     */
    private RunnableHolder.ScheduledStatus data;
}
