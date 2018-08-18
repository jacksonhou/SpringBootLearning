package com.jadmin.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jadmin.api.core.service.AbstractService;
import com.jadmin.api.mapper.PermissionMapper;
import com.jadmin.api.model.Permission;
import com.jadmin.api.service.PermissionService;
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
