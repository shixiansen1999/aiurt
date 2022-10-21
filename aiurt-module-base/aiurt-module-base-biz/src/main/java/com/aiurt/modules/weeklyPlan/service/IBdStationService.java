package com.aiurt.modules.weeklyplan.service;

import com.aiurt.modules.weeklyplan.entity.BdStation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: 工作场所表，存储用户工作场所信息
 * @Author: wgp
 * @Date: 2021-03-29
 * @Version: V1.0
 */
public interface IBdStationService extends IService<BdStation> {

    /**
     * 工作场所树查询
     *
     * @return
     */
//    List<StationTreeModel> queryStationTree();

    /**
     * 工作场所添加
     *
     * @param bdStation
     * @return
     */
//    boolean addStation(BdStation bdStation);

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
//    StationByIdDTO getStationById(String id);

    /**
     * 通过lineId查询所有的站点
     *
     * @param lineId
     * @return
     */
//    List<StationByLineVo> getStationByLineId(Integer lineId);

    /**
     * 通过多个lineId查询所有的站点
     *
     * @param list
     * @return
     */
//    List<StationByLineVo> getByLineIds(List<String> list);

    /**
     * 根据X，Y坐标查询站点名
     */
//    BdStation getStationByXY(String positionX, String positionY);

    /*
    //根据站点id查询设备
    List<DeviceTypeByStationDTO> queryDeviceTypeByid(String stationId);

    //查询当前用户管辖的站点信息
    List<BdStation> queryStationByUser(String userId);
    //查询当前用户管辖的站点信息(不带 children)
    List<BdStation> queryAllStationByUser(String userId);

    //只查询线路的站点
    List<BdStation> queryStation(Integer lineId);
    // 删除站点信息
    Result<?> removeBystationId(String id);
    //修改站点巡视周期
    void editPatrolCycle(String id, Integer patrolCycle);
    //修改站点巡视周期
    void editSpecialPatrolCycle(String id, Integer specialPatrolCycle);
    */
}
