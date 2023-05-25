package com.aiurt.modules.personnelportrait.service;


import com.aiurt.modules.personnelportrait.dto.*;

import java.util.List;

/**
 * @author
 * @description
 */
public interface PersonnelPortraitService {

    /**
     * 人员画像
     *
     * @param orgCode
     * @return
     */
    PersonnelPortraitResDTO portrait(String orgCode);

    /**
     * 用户详细信息
     *
     * @param userId
     * @return
     */
    UserDetailResDTO userDetail(String userId);

    /**
     * 人员综合表现
     *
     * @param userId
     * @return
     */
    RadarResDTO radarMap(String userId);

    /**
     * 综合表现评分
     *
     * @param userId
     * @return
     */
    DashboardResDTO dashboard(String userId);

    /**
     * 培训经历
     *
     * @param userId
     * @return
     */
    List<ExperienceResDTO> experience(String userId);

    /**
     * 任务次数
     *
     * @param userId
     * @return
     */
    List<WaveResDTO> waveRose(String userId);

    /**
     * 历史维修记录
     *
     * @param userId
     * @return
     */
    HistoryResDTO history(String userId);
}
