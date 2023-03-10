package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolSamplePerson;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Classname :  IPatrolSamplePersonService
 * @Description : TODO
 * @Date :2023/3/8 17:30
 * @Created by   : sbx
 */

public interface IPatrolSamplePersonService extends IService<PatrolSamplePerson> {

    void addPatrolSamplePerson(String patrolNumber, String sampleId);

}
