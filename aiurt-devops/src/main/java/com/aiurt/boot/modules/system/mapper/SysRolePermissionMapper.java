package com.aiurt.boot.modules.system.mapper;

import com.aiurt.boot.modules.system.entity.SysRolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @Author swsc
 * @since 2018-12-21
 */
@Mapper
@Repository
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

}
