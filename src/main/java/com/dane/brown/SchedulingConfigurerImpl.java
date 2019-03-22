package com.dane.brown;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.Task;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
@Component
public class SchedulingConfigurerImpl implements SchedulingConfigurer {

    private Logger log = LogManager.getLogger(getClass());
    @Autowired
    private PreventAutoRunScheduledConfig config;

    /**
     * Callback allowing a {@link TaskScheduler
     * TaskScheduler} and specific {@link Task Task}
     * instances to be registered against the given the {@link ScheduledTaskRegistrar}
     *
     * @param taskRegistrar the registrar to be configured.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        List<TriggerTask> preserverTriggerTaskList = taskRegistrar.getTriggerTaskList().stream().filter(item -> !isNeedPrevent(getTaskRealFuncName(item))).collect(Collectors.toList());
        List<IntervalTask> preserverFixedRateTaskList = taskRegistrar.getFixedRateTaskList().stream().filter(item -> !isNeedPrevent(getTaskRealFuncName(item))).collect(Collectors.toList());
        List<IntervalTask> preserverFixedDelayTaskList = taskRegistrar.getFixedDelayTaskList().stream().filter(item -> !isNeedPrevent(getTaskRealFuncName(item))).collect(Collectors.toList());
        List<CronTask> preserverCronTaskList = taskRegistrar.getCronTaskList().stream().filter(item -> !isNeedPrevent(getTaskRealFuncName(item))).collect(Collectors.toList());

        taskRegistrar.setTriggerTasksList(preserverTriggerTaskList);
        taskRegistrar.setFixedRateTasksList(preserverFixedRateTaskList);
        taskRegistrar.setFixedDelayTasksList(preserverFixedDelayTaskList);
        taskRegistrar.setCronTasksList(preserverCronTaskList);


    }

    private boolean isNeedPrevent(String name) {
        if (config == null || config.getPreventRegex() == null || config.getPreventRegex().size() <= 0) {
            return true;
        }
        List<String> regexlist = config.getPreventRegex();
        boolean isPrevent = regexlist.stream().anyMatch(item -> {
            Pattern pattern = Pattern.compile(item);
            Matcher matcher = pattern.matcher(name);
            return matcher.matches();
        });
        return isPrevent;
    }

    private String getTaskRealFuncName(Task task) {
        ScheduledMethodRunnable realRunnable = (ScheduledMethodRunnable)task.getRunnable();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(realRunnable.getMethod().getDeclaringClass().getName());
        stringBuilder.append(".");
        stringBuilder.append(realRunnable.getMethod().getName());
        String taskName = stringBuilder.toString();
        log.debug("SchedulingConfigurerImpl——>getTaskRealFuncName:{}",taskName);
        return taskName;

    }
}
