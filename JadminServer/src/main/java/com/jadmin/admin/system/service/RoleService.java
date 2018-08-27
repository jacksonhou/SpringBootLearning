package com.jadmin.admin.system.service;

import com.jadmin.admin.system.core.service.Service;
import com.jadmin.admin.system.model.Resource;
import com.jadmin.admin.system.model.Role;

import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
public interface RoleService extends Service<Role> {
    /**
     * 获取所有角色以及对应的权限
     *
     * @return 角色可控资源列表
     */
    List<Resource> findAllRoleWithPermission(String notes);
}
