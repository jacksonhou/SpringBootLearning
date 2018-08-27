package com.jadmin.admin.system.service.impl;

import com.jadmin.admin.system.core.service.AbstractService;
import com.jadmin.admin.system.mapper.UserRoleMapper;
import com.jadmin.admin.system.model.User;
import com.jadmin.admin.system.model.UserRole;
import com.jadmin.admin.system.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserRoleServiceImpl extends AbstractService<UserRole> implements UserRoleService {
    @Resource
    private UserRoleMapper userRoleMapper;

    @Override
    public void updateUserRole(final User user) {
        final Condition condition = new Condition(UserRole.class);
        condition.createCriteria().andCondition("user_id = ", user.getId());
        final UserRole userRole = new UserRole()
                .setUserId(user.getId())
                .setRoleId(user.getRoleId());
        this.userRoleMapper.updateByConditionSelective(userRole, condition);
    }
}
