package org.webant.queen.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.task.entity.Task;
import org.webant.queen.task.service.TaskService;
import org.webant.queen.task.vo.TaskVo;

@Controller
@RequestMapping(value = {"/task"})
public class TaskController {
    @Autowired
    TaskService service;

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public Response start(@RequestParam(value = "taskId", required = false, defaultValue = "") String taskId,
                          @RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        return Response.success();
    }

    @RequestMapping(value = "/pause", method = RequestMethod.GET)
    @ResponseBody
    public Response pause(@RequestParam(value = "taskId", required = false, defaultValue = "") String taskId,
                          @RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        return Response.success();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    @ResponseBody
    public Response stop(@RequestParam(value = "taskId", required = false, defaultValue = "") String taskId,
                         @RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        return Response.success();
    }

    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    @ResponseBody
    public Response progress(@RequestParam(value = "taskId", required = false, defaultValue = "") String taskId,
                             @RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        return Response.success();
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Response reset(@RequestParam(value = "taskId", required = false, defaultValue = "") String taskId,
                          @RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        return Response.success();
    }

    @RequestMapping(value = {"/get"}, method = RequestMethod.GET)
    @ResponseBody
    public Response<?> get(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        if (StringUtils.isEmpty(id)) {
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");
        }

        Task link = service.get(id);
        if (link == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(link);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response add(@RequestBody TaskVo entity) {
        Task task = entity.toTask();
        String id = service.save(task);

        return Response.success(id);
    }
}
