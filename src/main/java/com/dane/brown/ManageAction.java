package com.dane.brown;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.ScheduledMethodRunnable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaneBrown
 * email:tain198127@163.com
 **/
@Controller
@RequestMapping(value = "/scheduledmanage")
@Log4j2
public class ManageAction {

    @Resource
    private GeneralSchedulingInvokeAction generalSchedulingInvokeAction;

    @RequestMapping(value = "/index")
    public ModelAndView index(Model model) {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("result", "后台返回index3");
        mv.getModel().put("home.welcome", "hahaha");
        mv.getModel().put("result", "后台返回index3");
        mv.getModelMap().addAttribute("result", "后台返回index3");
        return mv;
    }

//    @GetMapping(value = "/home")
//    public void home(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/static/realhome.html");
//    }

    /**
     * 列表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/scheduledlistview")
    public ModelAndView scheduledlistview(Model model) {
        ModelAndView mv = new ModelAndView("ScheduledList");
        Map<String, RunnableHolder.ScheduledStatus> scheduledStatusMap = RunnableHolder.getInstance().getMethodRunnable();
        List<RunnableHolder.ScheduledStatus> ScheduledList = scheduledStatusMap.values().stream().collect(Collectors.toList());
        mv.getModelMap().addAttribute("ScheduledList", ScheduledList);
        return mv;
    }
    /**
     * 列表
     *
     * @return
     */
    @RequestMapping(value = "/scheduledlist")
    @ResponseBody
    public List<RunnableHolder.ScheduledStatus>  scheduledlist() {

        Map<String, RunnableHolder.ScheduledStatus> scheduledStatusMap = RunnableHolder.getInstance().getMethodRunnable();
        List<RunnableHolder.ScheduledStatus> ScheduledList = scheduledStatusMap.values().stream().collect(Collectors.toList());
        return ScheduledList;
    }
    @RequestMapping(value = "/scheduleddetail")
    @ResponseBody
    public InvokeActionResult getScheduleDetail(@RequestParam("key") String key){
        InvokeActionResult result = new InvokeActionResult();

        try {
            RunnableHolder.ScheduledStatus status = generalSchedulingInvokeAction.queryStatus(key, result);
            ScheduledMethodRunnable scheduledMethodRunnable = status.getScheduledMethodRunnable();
            Object object = scheduledMethodRunnable.getTarget();
            Method method = scheduledMethodRunnable.getMethod();
            Scheduled annotation =method.getAnnotation(Scheduled.class);
            log.info(annotation.cron());
            SchedulePresention schedulePresention = new SchedulePresention(annotation);
            status.setAnnotation(schedulePresention);
            result.setData(status);
        }
        catch (InovkeActionException ex) {
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

    /**
     * 详情
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/scheduleddetailView")
    public ModelAndView scheduleddetail(Model model) {
        ModelAndView mv = new ModelAndView("ScheduledDetail");
        return mv;
    }

}
