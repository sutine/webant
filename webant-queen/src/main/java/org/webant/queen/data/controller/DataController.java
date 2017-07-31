package org.webant.queen.data.controller;

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
import org.webant.queen.commons.vo.ErrorCode;
import org.webant.queen.commons.vo.Response;
import org.webant.queen.data.entity.Data;
import org.webant.queen.data.service.DataService;

@Controller
@RequestMapping(value = {"/data"})
public class DataController {
    @Autowired
    DataService service;

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

        Data data = service.get(id);
        if (data == null)
            return Response.failure(ErrorCode.BAD_REQUEST, "请求的数据不存在");
        return Response.success(data);
    }
}
