package org.webant.queen.site.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.site.entity.Site;
import org.webant.queen.site.service.SiteService;

import java.util.List;

@Controller
@RequestMapping(value = {"/site"})
public class SiteController {
    @Autowired
    SiteService service;

    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    @ResponseBody
    public Response fetch(@PageableDefault(value = 20, sort = { "dataCreateTime" }, direction = Sort.Direction.ASC) Pageable pageable) {
        return Response.success();
    }

    @RequestMapping(value = "/pulse", method = RequestMethod.GET)
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

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Response save(@RequestBody Site entity) {
        return Response.success();
    }

    @RequestMapping(value = "/pulse", method = RequestMethod.POST)
    @ResponseBody
    public Response pulse(@RequestBody Site entity) {
        return Response.success();
    }

    @RequestMapping(value = "/signoff", method = RequestMethod.POST)
    @ResponseBody
    public Response signoff(@RequestBody List<Site> list) {
        return Response.success();
    }
}
