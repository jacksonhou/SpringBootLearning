package com.jadmin.admin.system.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jadmin.admin.system.core.response.Result;
import com.jadmin.admin.system.core.response.ResultGenerator;
import com.jadmin.admin.system.model.Role;
import com.jadmin.admin.system.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/role")
public class RoleController
{
    @Resource
    private RoleService roleService;

    @PreAuthorize("hasAuthority('role:add')")
    @PostMapping
    public Result add(@RequestBody final Role role)
    {
        this.roleService.save(role);
        return ResultGenerator.genOkResult();
    }

    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable final Long id)
    {
        this.roleService.deleteById(id);
        return ResultGenerator.genOkResult();
    }

    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping
    public Result update(@RequestBody final Role role)
    {
        this.roleService.update(role);
        return ResultGenerator.genOkResult();
    }

    @PreAuthorize("hasAuthority('system:role')")
    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") final Integer page, @RequestParam(defaultValue = "0") final Integer size,
            @RequestParam(defaultValue = "") final String notes)
    {
        PageHelper.startPage(page, size);
        final List<com.jadmin.admin.system.model.Resource> list = this.roleService.findAllRoleWithPermission(notes);
        //noinspection unchecked
        final PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genOkResult(pageInfo);
    }
}
