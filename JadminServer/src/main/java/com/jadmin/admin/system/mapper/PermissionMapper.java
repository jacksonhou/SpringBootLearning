package com.jadmin.admin.system.mapper;

import com.alibaba.fastjson.JSONObject;
import com.jadmin.admin.system.core.mapper.MyMapper;
import com.jadmin.admin.system.model.Permission;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Jadmin
 * @date 2018/06/09
 */
public interface PermissionMapper extends MyMapper<Permission>
{
    /**
     * 找到所有权限可控资源
     *
     * @return Json对象列表
     */
    List<JSONObject> findAllResourcePermission();

    /**
     * 获取所有权限代码
     *
     * @return 代码列表
     */
    @Select("SELECT p.code FROM `permission` p")
    List<String> findAllCode();
}