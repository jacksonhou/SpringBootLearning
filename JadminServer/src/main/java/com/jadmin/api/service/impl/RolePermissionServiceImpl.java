package com.jadmin.api.service.impl;

import com.jadmin.api.core.service.AbstractService;
import com.jadmin.api.mapper.RolePermissionMapper;
import com.jadmin.api.model.RolePermission;
import com.jadmin.api.service.RolePermissionService;
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
