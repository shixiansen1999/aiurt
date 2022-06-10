package com.aiurt.boot.modules.apphome.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.apphome.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: UserTaskMapper
 * @author: Mr.zhao
 * @date: 2021/11/25 15:45
 */

@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {
}
