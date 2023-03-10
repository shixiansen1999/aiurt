package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolSamplePerson;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Classname :  PatrolSamplePersonMapper
 * @Description : TODO
 * @Date :2023/3/8 17:33
 * @Created by   : sbx
 */
public interface PatrolSamplePersonMapper extends BaseMapper<PatrolSamplePerson> {
    List<PatrolSamplePerson> getSamplePersonList(String patrolNumber);
}
