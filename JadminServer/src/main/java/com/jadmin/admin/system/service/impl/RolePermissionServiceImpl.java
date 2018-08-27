package com.jadmin.admin.system.service.impl;

import com.jadmin.admin.system.core.service.AbstractService;
import com.jadmin.admin.system.mapper.RolePermissionMapper;
import com.jadmin.admin.system.model.RolePermission;
import com.jadmin.admin.system.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RolePermissionServiceImpl extends AbstractService<RolePermission> implements RolePermissionService {
    @Resource
    private RolePermissionMapper rolePermissionMapper;

}
