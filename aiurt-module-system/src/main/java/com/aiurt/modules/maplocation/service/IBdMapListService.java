package com.aiurt.modules.maplocation.service;


import com.aiurt.modules.maplocation.dto.AssignUserDTO;
import com.aiurt.modules.maplocation.dto.CurrentTeamPosition;
import com.aiurt.modules.maplocation.dto.EquipmentHistoryDTO;

import com.aiurt.modules.maplocation.dto.UserInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;


import java.util.List;

/**
 * @Description: bd_info_list
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
public interface IBdMapListService extends IService<CurrentTeamPosition> {

    //  查询人员的位置信息
    List<CurrentTeamPosition> queryPositionById(String teamId,String userInfoList, String stationId, String stateId);
    // 根据机构查询人员
    List<UserInfo> getUserByTeamIdList(String teamId);
    // 根据人员id查询人员信息
    List<UserInfo> getUserById(String id);
    // 根据人员id查询附近设备
    Page<EquipmentHistoryDTO> getEquipmentByUserId(String id, String stationId, Page<EquipmentHistoryDTO> pageList);

    /**
     * 根据机构获取机构下的人员状态
     * @param teamId
     * @return
     */
    List<AssignUserDTO> getUserStateByTeamId(String teamId);
    /**
     * 发送消息给对应的用户
     *
     * @param username 用户账号
     * @param msg
     * @return
     */
    void sendSysAnnouncement(String username, String msg);
}
