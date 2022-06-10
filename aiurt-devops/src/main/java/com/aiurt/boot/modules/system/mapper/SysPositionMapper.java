package com.aiurt.boot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.system.entity.SysPosition;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: 职务表
 * @Author: swsc
 * @Date: 2019-09-19
 * @Version: V1.0
 */
@Mapper
@Repository
public interface SysPositionMapper extends BaseMapper<SysPosition> {

}
