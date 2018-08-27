package com.jadmin.admin.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jadmin.admin.system.core.service.AbstractService;
import com.jadmin.admin.system.mapper.PermissionMapper;
import com.jadmin.admin.system.model.Permission;
import com.jadmin.admin.system.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PermissionServiceImpl extends AbstractService<Permission> implements PermissionService {
    @Resource
    private PermissionMapper permissionMapper;

    @Override
    public List<JSONObject> findAllResourcePermission() {
        return this.permissionMapper.findAllResourcePermission();
    }
}
