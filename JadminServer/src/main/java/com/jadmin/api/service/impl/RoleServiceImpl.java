package com.jadmin.api.service.impl;

import com.jadmin.api.core.service.AbstractService;
import com.jadmin.api.mapper.RoleMapper;
import com.jadmin.api.mapper.RolePermissionMapper;
import com.jadmin.api.model.Role;
import com.jadmin.api.model.RolePermission;
import com.jadmin.api.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleServiceImpl extends AbstractService<Role> implements RoleService {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<com.jadmin.api.model.Resource> findAllRoleWithPermission(String notes) {
        return this.roleMapper.findAllRoleWithPermission(notes);
    }

    @Override
    public void save(final Role role) {
        this.roleMapper.insert(role);
        this.rolePermissionMapper.saveRolePermission(role.getId(), role.getPermissionIdList());
    }

    @Override
    public void update(final Role role) {
        roleMapper.updateByPrimaryKey(role);
        // 删掉所有权限，再添加回去
        final Condition condition = new Condition(RolePermission.class);
        condition.createCriteria().andCondition("role_id = ", role.getId());
        this.rolePermissionMapper.deleteByCondition(condition);
        this.rolePermissionMapper.saveRolePermission(role.getId(), role.getPermissionIdList());
    }
}
