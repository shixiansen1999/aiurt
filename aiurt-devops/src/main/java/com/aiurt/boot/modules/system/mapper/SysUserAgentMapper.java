package com.aiurt.boot.modules.system.mapper;

import com.swsc.copsms.modules.system.entity.SysUserAgent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: 用户代理人设置
 * @Author: swsc
 * @Date:  2019-04-17
 * @Version: V1.0
 */
@Mapper
@Repository
public interface SysUserAgentMapper extends BaseMapper<SysUserAgent> {

}
