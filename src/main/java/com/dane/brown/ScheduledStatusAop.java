package com.dane.brown;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 * 获取所有批处理的状态
 **/
@Service
@Aspect
@EnableAspectJAutoProxy
@Log4j2
public class ScheduledStatusAop {

    @Pointcut("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void schedulePointCut() {
    }

    @Before("schedulePointCut()")
    public void allScheduledMethodBefore(JoinPoint joinPoint) {
        String key = RunnableHolder.getInstance().makeKey(joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName() );
        RunnableHolder.ScheduledStatus status =RunnableHolder.getInstance().getMethodRunnable().get(key);
        if(status == null){
            log.error("{}找不到对应的缓存", key);
        }
        status.setCurrentStatus(RunnableHolder.ScheduledStatusEnum.Running);
        status.setLastBeginTime(new Date());
        log.info("before:"+key);

    }
    @AfterReturning("schedulePointCut()")
    public void allScheduledMethodAfter(JoinPoint joinPoint) {

        String key = RunnableHolder.getInstance().makeKey(joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName() );
        RunnableHolder.ScheduledStatus status =RunnableHolder.getInstance().getMethodRunnable().get(key);
        if(status == null){
            log.error("{}找不到对应的缓存", key);
        }
        status.setCurrentStatus(RunnableHolder.ScheduledStatusEnum.Finish);
        status.setLastFinishTime(new Date());
        log.info("end:"+key);
    }
    @AfterThrowing("schedulePointCut()")
    public void allScheduledMethodError(JoinPoint joinPoint) {

        String key = RunnableHolder.getInstance().makeKey(joinPoint.getSignature().getDeclaringTypeName(),joinPoint.getSignature().getName() );
        RunnableHolder.ScheduledStatus status =RunnableHolder.getInstance().getMethodRunnable().get(key);
        if(status == null){
            log.error("{}找不到对应的缓存", key);
        }
        status.setCurrentStatus(RunnableHolder.ScheduledStatusEnum.Error);
        status.setLastFinishTime(new Date());
        log.info("error:"+key);

    }

}
