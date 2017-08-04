package org.webant.queen.site.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.webant.queen.commons.entity.Progress;
import org.webant.queen.commons.exception.QueenException;
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.site.entity.Site;
import org.webant.queen.site.service.SiteService;

@Controller
@RequestMapping(value = {"/site"})
public class SiteController {
    @Autowired
    private SiteService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Response list(@PageableDefault(value = 20, sort = { "dataCreateTime" }, direction = Sort.Direction.ASC) Pageable pageable) {
        return Response.success();
    }

    @RequestMapping(value = {"/get"}, method = RequestMethod.GET)
    @ResponseBody
    public Response<?> get(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        if (StringUtils.isEmpty(id)) {
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");
        }

        Site link = service.get(id);
        if (link == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(link);
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET)
    @ResponseBody
    public Response start(@RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        if (StringUtils.isEmpty(siteId))
            return Response.failure(ErrorCode.BAD_REQUEST, "site id can not be empty!");

        try {
            service.action(siteId, Site.SITE_STATUS_START);
        } catch (QueenException e) {
            return Response.failure(ErrorCode.APPLICATION_ERROR, e.getMessage());
        }
        return Response.success();
    }

    @RequestMapping(value = "/pause", method = RequestMethod.GET)
    @ResponseBody
    public Response pause(@RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        if (StringUtils.isEmpty(siteId))
            return Response.failure(ErrorCode.BAD_REQUEST, "site id can not be empty!");

        try {
            service.action(siteId, Site.SITE_STATUS_PAUSE);
        } catch (QueenException e) {
            return Response.failure(ErrorCode.APPLICATION_ERROR, e.getMessage());
        }
        return Response.success();
    }

    @RequestMapping(value = "/stop", method = RequestMethod.GET)
    @ResponseBody
    public Response stop(@RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        if (StringUtils.isEmpty(siteId))
            return Response.failure(ErrorCode.BAD_REQUEST, "site id can not be empty!");

        try {
            service.action(siteId, Site.SITE_STATUS_STOP);
        } catch (QueenException e) {
            return Response.failure(ErrorCode.APPLICATION_ERROR, e.getMessage());
        }
        return Response.success();
    }

    @RequestMapping(value = "/progress", method = RequestMethod.GET)
    @ResponseBody
    public Response progress(@RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        if (StringUtils.isEmpty(siteId))
            return Response.failure(ErrorCode.BAD_REQUEST, "site id can not be empty!");

        Progress progress;
        try {
            progress = service.progress(siteId);
        } catch (QueenException e) {
            return Response.failure(ErrorCode.APPLICATION_ERROR, e.getMessage());
        }

        return Response.success(progress);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Response reset(@RequestParam(value = "siteId", required = false, defaultValue = "") String siteId) {
        if (StringUtils.isEmpty(siteId))
            return Response.failure(ErrorCode.BAD_REQUEST, "site id can not be empty!");

        long affectRowsNum;
        try {
            affectRowsNum = service.reset(siteId);
        } catch (QueenException e) {
            return Response.failure(ErrorCode.APPLICATION_ERROR, e.getMessage());
        }

        return Response.success(affectRowsNum);
    }
}
