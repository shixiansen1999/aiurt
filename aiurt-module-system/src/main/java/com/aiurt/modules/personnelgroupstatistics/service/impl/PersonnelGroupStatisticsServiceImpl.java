package com.aiurt.modules.personnelgroupstatistics.service.impl;

import com.aiurt.modules.personnelgroupstatistics.mapper.PersonnelGroupStatisticsMapper;
import com.aiurt.modules.personnelgroupstatistics.service.PersonnelGroupStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lkj
 */
@Service
public class PersonnelGroupStatisticsServiceImpl implements PersonnelGroupStatisticsService {

    @Autowired
    private PersonnelGroupStatisticsMapper personnelGroupStatisticsMapper;

}
