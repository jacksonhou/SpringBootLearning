package com.jadmin.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jadmin.api.core.response.Result;
import com.jadmin.api.core.response.ResultGenerator;
import com.jadmin.api.service.PermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/permission")
public class PermissionController
{
    @Resource
    private PermissionService permissionService;

    @PreAuthorize("hasAuthority('system:role')")
    @GetMapping
    public Result listResourcePermission(@RequestParam(defaultValue = "0") final Integer page, @RequestParam(defaultValue = "0") final Integer size)
    {
        PageHelper.startPage(page, size);
        final List<JSONObject> list = this.permissionService.findAllResourcePermission();
        // noinspection unchecked
        final PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genOkResult(pageInfo);
    }
}
