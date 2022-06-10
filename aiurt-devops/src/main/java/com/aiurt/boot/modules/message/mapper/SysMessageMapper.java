package com.aiurt.boot.modules.message.mapper;

import com.swsc.copsms.modules.message.entity.SysMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Mapper
@Repository
public interface SysMessageMapper extends BaseMapper<SysMessage> {

}
