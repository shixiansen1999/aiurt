package com.aiurt.modules.sysFile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.sysFile.entity.DefaultUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description: DefaultUserMapper
 * @author: Mr.zhao
 * @date: 2021/11/22 14:50
 */

@Mapper
public interface DefaultUserMapper extends BaseMapper<DefaultUser> {

	List<DefaultUser> listDefaultUser(@Param("userId")String userId);
}
