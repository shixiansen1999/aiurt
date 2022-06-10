package com.aiurt.boot.modules.system.mapper;

import com.swsc.copsms.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @Author swsc
 * @since 2018-12-19
 */
@Mapper
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

}
