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
import org.webant.queen.site.entity.SiteConfig;
import org.webant.queen.site.entity.SiteTemplate;
import org.webant.queen.site.service.SiteTemplateService;
import org.webant.queen.utils.JsonUtils;

@Controller
@RequestMapping(value = {"/template"})
public class SiteTemplateController {
    @Autowired
    SiteTemplateService service;

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
    public Response<?> get(@RequestParam(value = "id", required = false, defaultValue = "") Integer id) {
        if (StringUtils.isEmpty(id))
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");

        SiteTemplate link = service.get(id);
        if (link == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(link);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response add(@RequestBody SiteConfig entity) {
        if (entity == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "数据不能为空");

        String json = JsonUtils.toJson(entity);
        if (StringUtils.isEmpty(json))
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");

        SiteTemplate template = new SiteTemplate(json);
        Integer id = service.save(template);
        return Response.success(id);
    }
}
