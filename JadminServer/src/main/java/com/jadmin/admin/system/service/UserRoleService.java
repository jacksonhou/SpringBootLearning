package com.jadmin.admin.system.service;

import com.jadmin.admin.system.core.service.Service;
import com.jadmin.admin.system.model.User;
import com.jadmin.admin.system.model.UserRole;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
public interface UserRoleService extends Service<UserRole> {
    /**
     * 更新用户角色
     *
     * @param user 用户
     */
    void updateUserRole(User user);
}
