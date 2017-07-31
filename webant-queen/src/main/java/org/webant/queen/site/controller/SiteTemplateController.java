package org.webant.queen.site.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = {"/get"}, method = RequestMethod.GET)
    @ResponseBody
    public Response<?> get(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        if (StringUtils.isEmpty(id))
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");

        SiteTemplate template = service.get(id);
        if (template == null)
            return Response.failure(ErrorCode.APPLICATION_ERROR, "请求的数据不存在");

        if (StringUtils.isEmpty(template.getConfig()))
            return Response.failure(ErrorCode.APPLICATION_ERROR, "config字段为空");

        SiteConfig site = JsonUtils.fromJson(template.getConfig(), SiteConfig.class);
        return Response.success(site);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response add(@RequestBody SiteConfig site) {
        if (site == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "数据不能为空");

        String json = JsonUtils.toJson(site);
        if (StringUtils.isEmpty(json))
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");

        SiteTemplate template = new SiteTemplate(json);
        String id = service.save(template);
        return Response.success(id);
    }
}
