package com.jadmin.admin.system.service.impl;

import com.jadmin.admin.system.core.exception.ServiceException;
import com.jadmin.admin.system.core.service.AbstractService;
import com.jadmin.admin.system.mapper.PermissionMapper;
import com.jadmin.admin.system.mapper.UserMapper;
import com.jadmin.admin.system.mapper.UserRoleMapper;
import com.jadmin.admin.system.model.User;
import com.jadmin.admin.system.model.UserRole;
import com.jadmin.admin.system.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends AbstractService<User> implements UserService
{
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 重写save方法，密码加密后再存
     */
    @Override
    public void save(final User user)
    {
        User u = this.findBy("username", user.getUsername());
        if (u != null)
        {
            throw new ServiceException("username already existed");
        }
        else
        {
            u = this.findBy("email", user.getEmail());
            if (u != null)
            {
                throw new ServiceException("email already existed");
            }
            else
            {
                //log.info("before password : {}", user.getPassword().trim());
                user.setPassword(this.passwordEncoder.encode(user.getPassword().trim()));
                //log.info("after password : {}", user.getPassword());
                this.userMapper.insertSelective(user);
                //log.info("User<{}> id : {}", user.getUsername(), user.getId());
                // 如果没有指定角色Id，以默认普通用户roleId保存
                Long roleId = user.getRoleId();
                if (roleId == null)
                {
                    roleId = 2L;
                }
                this.userRoleMapper.insert(new UserRole().setUserId(user.getId()).setRoleId(roleId));
            }
        }
    }

    /**
     * 重写update方法
     */
    @Override
    public void update(final User user)
    {
        // 如果修改了密码
        if (user.getPassword() != null && user.getPassword().length() >= 6)
        {
            // 密码修改后需要加密
            user.setPassword(this.passwordEncoder.encode(user.getPassword().trim()));
        }
        this.userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public List<User> findAllUserWithRole(String userName)
    {
        return this.userMapper.findAllUserWithRole(userName);
    }

    @Override
    public User findDetailBy(final String column, final Object param)
    {
        final Map<String, Object> map = new HashMap<>(1);
        map.put(column, param);
        return this.userMapper.findDetailBy(map);
    }

    @Override
    public User findDetailByUsername(final String username) throws UsernameNotFoundException
    {
        final User user = this.findDetailBy("username", username);
        if (user == null)
        {
            throw new UsernameNotFoundException("not found this username");
        }
        if ("ROLE_ADMIN".equals(user.getRoleName()))
        {
            // 超级管理员所有权限都有
            user.setPermissionCodeList(this.permissionMapper.findAllCode());
        }
        return user;
    }

    @Override
    public boolean verifyPassword(final String rawPassword, final String encodedPassword)
    {
        return this.passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public void updateLoginTimeByUsername(final String username)
    {
        this.userMapper.updateLoginTimeByUsername(username);
    }
}
