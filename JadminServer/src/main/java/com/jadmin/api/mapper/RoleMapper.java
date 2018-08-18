package com.jadmin.api.mapper;

import com.jadmin.api.core.mapper.MyMapper;
import com.jadmin.api.model.Resource;
import com.jadmin.api.model.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
public interface RoleMapper extends MyMapper<Role>
{
    /**
     * 获取所有角色以及对应的权限
     *
     * @return 角色可控资源列表
     */
    List<Resource> findAllRoleWithPermission(@Param("notes") String notes);
}