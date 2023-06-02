package com.aiurt.modules.personnelportrait.service;


import com.aiurt.modules.fault.dto.FaultDeviceDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.personnelportrait.dto.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.servlet.http.HttpServletRequest;
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
    List<HistoryResDTO> history(String userId);

    /**
     * 历史维修记录列表(更多)
     *
     * @param pageNo
     * @param pageSize
     * @param userId
     * @param request
     * @return
     */
    IPage<Fault> historyRecord(Integer pageNo, Integer pageSize, String userId, HttpServletRequest request);

    /**
     * 历史维修记录-设备故障信息列表
     *
     * @param userId
     * @return
     */
    List<FaultDeviceDTO> deviceInfo(String userId);
}
