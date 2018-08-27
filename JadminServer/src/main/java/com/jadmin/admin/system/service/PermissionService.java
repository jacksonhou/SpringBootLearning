package com.jadmin.admin.system.service;

import com.alibaba.fastjson.JSONObject;
import com.jadmin.admin.system.core.service.Service;
import com.jadmin.admin.system.model.Permission;

import java.util.List;

/**
 * @author Jadmin
 * @date 2018/05/17
 */
public interface PermissionService extends Service<Permission> {
    /**
     * 找到所有权限可控资源
     *
     * @return Json对象列表
     */
    List<JSONObject> findAllResourcePermission();
}
