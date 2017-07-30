package org.webant.queen.link.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.ListResult;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.link.entity.Link;
import org.webant.queen.link.service.LinkService;

import java.util.List;

@Controller
@RequestMapping(value = {"/link"})
public class LInkController {
    @Autowired
    LinkService service;

    @RequestMapping(value = "/fetch", method = RequestMethod.GET)
    @ResponseBody
    public Response fetch(@PageableDefault(value = 20, sort = { "dataCreateTime" }, direction = Sort.Direction.ASC) Pageable pageable) {
        List<Link> list = service.getInitLinks(Link.LINK_STATUS_INIT, pageable);
        return Response.success(list);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Response list(@RequestParam(value = "status", required = false, defaultValue = Link.LINK_STATUS_INIT) String status,
                         @PageableDefault(value = 20, sort = { "dataCreateTime" }, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<Link> page = service.list(status, pageable);
        return Response.success(new ListResult<>(page.getTotalElements(), page.getContent()));
    }

    @RequestMapping(value = {"/get"}, method = RequestMethod.GET)
    @ResponseBody
    public Response<?> get(@RequestParam(value = "id", required = false, defaultValue = "") String id) {
        if (StringUtils.isEmpty(id)) {
            return Response.failure(ErrorCode.BAD_REQUEST, "参数 id 不能为空");
        }

        Link link = service.get(id);
        if (link == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(link);
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Response save(@RequestBody Link entity) {
        if (entity == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "数据不能为空");
        return Response.success(service.upsert(entity));
    }

    @RequestMapping(value = "/save/list", method = RequestMethod.POST)
    @ResponseBody
    public Response save(@RequestBody List<Link> list) {
        if (list.isEmpty())
            return Response.success(null);
        return Response.success(service.upsert(list));
    }
}
