package com.jadmin.api.service;

import com.jadmin.api.core.service.Service;
import com.jadmin.api.model.User;
import com.jadmin.api.model.UserRole;

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
