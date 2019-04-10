package com.dane.brown;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 * 通用调用接口，用于调用scheduled
 **/
@EnableAsync
@RestController
@Log4j2
public class GeneralSchedulingInvokeAction {
    @Autowired
    private ApplicationContext context;

    private RunnableHolder.ScheduledStatus queryStatus(String clzName, String methodName, InvokeActionResult result) throws InovkeActionException {
        String key = RunnableHolder.getInstance().makeKey(clzName, methodName);
        RunnableHolder.ScheduledStatus status = RunnableHolder.getInstance().getMethodRunnable().get(key);
        if(status == null){
            throw new InovkeActionException(key + "未找到", 404);
        }
        result.setCurrentStatus(status.getCurrentStatus());
        result.setLastBeginTime(status.getLastBeginTime());
        result.setLastFinishTime(status.getLastFinishTime());

        ScheduledMethodRunnable scheduledMethodRunnable = RunnableHolder.getInstance().getMethodRunnable().get(key).getScheduledMethodRunnable();

        Object object = scheduledMethodRunnable.getTarget();
        if (object == null) {
            throw new InovkeActionException(clzName + "未找到", 404);
        }
        Method method = scheduledMethodRunnable.getMethod();
        if (method == null) {
            throw new InovkeActionException(key + "未找到", 410);
        }
        if (status.getCurrentStatus() == RunnableHolder.ScheduledStatusEnum.Running) {
            result.setStatus(301);
            result.setMessage(key + ":正在执行");
        }

        return status;
    }

    /**
     * 查询批处理状态
     *
     * @param clzName
     * @param methodName
     * @return
     */
    @RequestMapping(value = "/generalschedulingquery/{clzName}/{methodName}", method = RequestMethod.GET)
    public Object query(@PathVariable(value = "clzName") String clzName, @PathVariable(value = "methodName") String methodName) {
        InvokeActionResult result = new InvokeActionResult();
        try {
            queryStatus(clzName, methodName, result);
            result.setStatus(200);
            result.setMessage("查询成功");
        } catch (InovkeActionException ex) {
            log.error("请求报错，错误内容是:{}", ex);
            result.setStatus(ex.getResultCode());
            result.setMessage(ex.getMessage());
        } finally {

            return result;
        }
    }

    /**
     * @param clzName 被调函数类名.函数名
     * @return
     */
    @RequestMapping(value = "/generalschedulinginvoke/{clzName}/{methodName}", method = RequestMethod.GET)
    public Object invoke(@PathVariable(value = "clzName") String clzName, @PathVariable(value = "methodName") String methodName) {
        InvokeActionResult result = new InvokeActionResult();

        try {

            RunnableHolder.ScheduledStatus status = queryStatus(clzName, methodName, result);
            if(result.getCurrentStatus() == RunnableHolder.ScheduledStatusEnum.Running){
                return result;
            }
            context.getBean(GeneralSchedulingInvokeAction.class).asyncInvoke(status.getScheduledMethodRunnable().getTarget(), status.getScheduledMethodRunnable().getMethod());
            result.setStatus(200);
            result.setMessage("已经调用");
        } catch (InovkeActionException ex) {
            log.error("请求报错，错误内容是:{}", ex);
            result.setStatus(ex.getResultCode());
            result.setMessage(ex.getMessage());
        } catch (Exception ex) {
            log.error("请求报错，错误内容是:{}", ex);
            result.setStatus(500);
            result.setMessage(ex.getMessage());
        } finally {
            return result;
        }
    }

    @Async
    void asyncInvoke(Object object, Method method) {
        try {
            method.invoke(object);
        } catch (IllegalAccessException e) {
            log.error("error:{}", e);
        } catch (InvocationTargetException e) {
            log.error("error:{}", e);
        }
    }

    @Data
    public static class InvokeActionResult {
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

    }

    /**
     *
     */
    public static class InovkeActionException extends Exception {
        private int resultCode;

        public InovkeActionException(String msg) {
            super(msg);
        }

        public InovkeActionException(String msg, Throwable t) {
            super(msg, t);
        }

        public InovkeActionException(String msg, int resultCode, Throwable t) {
            super(msg, t);
            this.resultCode = resultCode;
        }

        public InovkeActionException(String msg, int resultCode) {
            super(msg);
            this.resultCode = resultCode;
        }

        public int getResultCode() {
            return resultCode;
        }

    }
}
