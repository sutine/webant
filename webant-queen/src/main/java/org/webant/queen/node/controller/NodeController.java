package org.webant.queen.node.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.node.entity.Node;
import org.webant.queen.node.service.NodeService;

import java.util.List;

@Controller
@RequestMapping(value = {"/node"})
public class NodeController {
    @Autowired
    NodeService service;

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

        Node link = service.get(id);
        if (link == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(link);
    }

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    @ResponseBody
    public Response save(@RequestBody Node entity) {
        return Response.success();
    }

    @RequestMapping(value = "/pulse", method = RequestMethod.POST)
    @ResponseBody
    public Response pulse(@RequestBody Node entity) {
        return Response.success();
    }

    @RequestMapping(value = "/signoff", method = RequestMethod.POST)
    @ResponseBody
    public Response signoff(@RequestBody List<Node> list) {
        return Response.success();
    }
}
