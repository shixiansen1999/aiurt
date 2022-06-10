package com.aiurt.boot.modules.system.mapper;

import java.util.List;

import com.swsc.copsms.modules.system.entity.SysUserDepart;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface SysUserDepartMapper extends BaseMapper<SysUserDepart>{

    List<SysUserDepart> getUserDepartByUid(@Param("userId") String userId);
}
