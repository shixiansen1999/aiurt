package com.aiurt.boot.modules.system.mapper;

import com.aiurt.boot.modules.system.entity.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

	@Select("select r.role_code from sys_role r left join sys_user_role ur on ur.role_id = r.id  where ur.user_id = #{userId} limit 1")
	String selectRoleCode(@Param("userId") String userId);
}
