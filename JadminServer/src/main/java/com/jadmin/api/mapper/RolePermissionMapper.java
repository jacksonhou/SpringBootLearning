package com.jadmin.api.mapper;

import com.jadmin.api.core.mapper.MyMapper;
import com.jadmin.api.model.RolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
public interface RolePermissionMapper extends MyMapper<RolePermission>
{
    /**
     * 保存角色以及对应的权限ID
     *
     * @param roleId           角色ID
     * @param permissionIdList 权限ID列表
     */
    void saveRolePermission(@Param("roleId") Long roleId, @Param("permissionIdList") List<Integer> permissionIdList);
}