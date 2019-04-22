package com.dane.brown;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.annotations.VisibleForTesting;
import lombok.Data;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 * 包含了所有的批处理
 **/
public class RunnableHolder {
    private static RunnableHolder _instance;
    private Map<String, ScheduledStatus> methodRunnable;
    private RunnableHolder() {
        methodRunnable = new ConcurrentHashMap<>();
    }

    public static RunnableHolder getInstance() {
        if (_instance == null) {
            _instance = new RunnableHolder();
        }
        return _instance;
    }

    /**
     * 获得拼接名称
     * @param clzName
     * @param methodName
     * @return
     */
    public  String makeKey(String clzName, String methodName){
        return String.format("%s.%s", clzName,methodName);
    }

    /**
     * 获取key名称
     * @param scheduledMethodRunnable
     * @return
     */
    public String makeKey(ScheduledMethodRunnable scheduledMethodRunnable){
        return makeKey(scheduledMethodRunnable.getMethod().getDeclaringClass().getName(),scheduledMethodRunnable.getMethod().getName());
    }

    /**
     * 包含了初始化的put
     * @param taskName
     * @param scheduledMethodRunnable
     */
    public void put(String taskName, ScheduledMethodRunnable scheduledMethodRunnable){
        ScheduledStatus status = new ScheduledStatus();
        status.setCurrentStatus(ScheduledStatusEnum.NotRun);
        if(methodRunnable.containsKey(taskName)){
            status = methodRunnable.get(taskName);
        }
        status.setScheduledMethodRunnable(scheduledMethodRunnable);
        status.setTaskName(taskName);
        methodRunnable.put(taskName, status);
    }
    public void put(String taskName,ScheduledStatus status){
        methodRunnable.put(taskName, status);
    }
    public Map<String, ScheduledStatus> getMethodRunnable() {
        return methodRunnable;
    }
    @Data
    public static class ScheduledStatus{
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
        private ScheduledStatusEnum currentStatus;
        /**
         * 任务名称 className.methodName
         */
        private String taskName;
        /**
         * 任务名称 包含了一些容器
         */
        @JsonIgnore
        @JsonIgnoreProperties
        private ScheduledMethodRunnable scheduledMethodRunnable;
        /**
         * 枚举定义
         */
        private SchedulePresention annotation;
    }
    public static enum ScheduledStatusEnum {
        /*正在运行*/
        Running,
        /*找到了对应的批处理，但是在内存状态中没有对应的执行记录*/
        NotRun,
        /*执行完毕*/
        Finish,
        /*状态错误*/
        Error,
    }

}
