package com.aiurt.modules.maplocation.mapper;


import com.aiurt.common.api.vo.TreeNode;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.maplocation.dto.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;


import java.util.List;

/**
 * @Description: bd_info_list
 * @Author: jeecg-boot
 * @Date: 2021-04-19
 * @Version: V1.0
 */
@Component
@EnableDataPerm
public interface BdMapListMapper extends BaseMapper<CurrentTeamPosition> {
    //  查询人员的位置信息
   List<CurrentTeamPosition>  queryPositionById(@Param("userIdList") List<String> userIdList);

    // 2.获取所有人员信息
    List<UserInfo> getUserList(String id);

    // 根据机构查询人员
    List<UserInfo> getUserByTeamIdList(List<String> teamChild);

    // 根据人员id查询人员信息
    List<UserInfo> getUserById(String id);

    // 1.所有设备对应的站点
    List<EquipmentDTO> getAllEquipmentList();

    //  根据人员id查询附近设备
    List<EquipmentHistoryDTO> selectEquipment(@Param("pageList") Page<EquipmentHistoryDTO> pageList, @Param("teamIdList") List<String> teamIdList, @Param("stationIdStr") String stationIdStr);

    // 获得所有机构
    List<TreeNode> getAllTeam();

    //根据id 查询人员
    List<UserInfo> queryUserById(List<String> idList);

    UserStationDTO getStationId(String id);

   /**
    * 根据mac 查询站点位置信息
    * @param mac
    * @return
    */
   UserStationDTO getStationByMac(@Param("mac") String mac);

}
