package com.jadmin.admin.system.controller;

import com.jadmin.admin.system.core.response.Result;
import com.jadmin.admin.system.core.response.ResultGenerator;
import com.jadmin.admin.system.model.User;
import com.jadmin.admin.system.service.UserRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@RestController
@RequestMapping("/user/role")
public class UserRoleController {
    @Resource
    private UserRoleService userRoleService;

    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping
    public Result updateUserRole(@RequestBody final User user) {
        this.userRoleService.updateUserRole(user);
        return ResultGenerator.genOkResult();
    }
}
