package com.jadmin.api.controller;

import com.jadmin.api.core.response.Result;
import com.jadmin.api.core.response.ResultGenerator;
import com.jadmin.api.model.User;
import com.jadmin.api.service.UserRoleService;
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
