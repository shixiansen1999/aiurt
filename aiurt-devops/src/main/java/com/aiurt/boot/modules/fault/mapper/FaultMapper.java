package com.aiurt.boot.modules.fault.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.modules.apphome.vo.FaultHomeVO;
import com.aiurt.boot.modules.fault.entity.Fault;
import com.aiurt.boot.modules.fault.param.FaultCountParam;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.param.FaultParam;
import com.aiurt.boot.modules.patrol.param.PatrolAppHomeParam;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.common.result.*;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultMapper extends BaseMapper<Fault> {

    /**
     * 查询故障
     * @param page
     * @param param
     * @return
     */
    IPage<FaultResult> queryFault(IPage<FaultResult> page,
                                  @Param("param") FaultParam param);

    /**
     * 导出故障列表
     * @param param
     * @return
     */
    List<FaultResult> exportXls(@Param("param") FaultParam param);

    /**
     * 根据code查询故障详情
     * @param code
     * @return
     */
    FaultResult selectDetailByCode(String code);

    /**
     * 根据code查询信息
     * @param code
     * @return
     */
    FaultCodesResult selectCodeDetail(String code);

    /**
     * 故障挂起
     * @param id
     * @param remark
     */
    void hangById (@Param("id")Integer id, String remark);

    /**
     * 取消挂起
     * @param id
     */
    void cancelById (@Param("id")Integer id);

    /**
     * 指派
     * @param code
     * @return
     */
    void assignByCode(@Param("code")String code);

    /**
     * 重新指派
     * @param code
     */
    void assignAgain(@Param("code")String code);

    /**
     * 更改状态为已解决
     * @param code
     */
    void updateStatus(@Param("code")String code);

    /**
     * 修改故障现象
     * @param code
     * @param faultPhenomenon
     */
    void updateByFaultCode(@Param("code")String code, @Param("faultPhenomenon")String faultPhenomenon);

    /**
     * 查询app首页信息
     * @param homeParam
     * @return
     */
    List<FaultHomeVO> selectAppHome(@Param("param") PatrolAppHomeParam homeParam);

    /**
     * 查询故障已完成数量
     * @param homeParam
     * @return
     */
    Integer selectAppHomeCount(@Param("param")PatrolAppHomeParam homeParam);

    /**
     * 计算超时故障数量
     * @param startTime
     * @param endTime
     * @param nowTime
     * @return
     */
    Integer selectTimeOutCount(String startTime, String endTime, Date nowTime);

    /**
     * pc首页报表统计超时挂起数量
     * @param startTime
     * @param endTime
     * @param nowTime
     * @return
     */
    Integer selectTimeOutHangNum(String startTime, String endTime, Date nowTime);

    /**
     * 获取故障数量
     * @param param
     * @return
     */
    List<FaultCountResult> selectFaultCount(@Param("param")FaultCountParam param);

    /**
     * 获取该系统自检数量
     * @param param
     * @return
     */
    Integer selectSelfCheckNum(@Param("param")FaultCountParam param);

    /**
     * 获取该系统上月同期故障数量
     * @param param
     * @return
     */
    Integer selectLastMonthNum(@Param("param")FaultCountParam param);

    /**
     * 各系统检修/自检对比
     * @param param
     * @return
     */
    List<FaultCountResult> selectContrast(@Param("param")FaultCountParam param);

    /**
     * 单一系统检修/自检各月份故障分析
     * @param param
     * @return
     */
    List<FaultMonthResult> selectFaultNumByMonth(@Param("param")FaultCountParam param);

    /**
     * 获取本月自检数量
     * @param param
     * @param dayStart
     * @param dayEnd
     * @return
     */
    Integer selectThisMonthNum(@Param("param") FaultCountParam param, @Param("dayStart") LocalDate dayStart, @Param("dayEnd") LocalDate dayEnd);

    /**
     * 单一系统检修/自检各月份故障分析
     * @param param
     * @return
     */
    List<FaultMonthResult> selectFaultByMonth(@Param("param")FaultCountParam param);

    /**
     * 计算qu
     * @param param
     * @param dayStart
     * @param dayEnd
     * @return
     */
    Integer selectThisMonth(@Param("param") FaultCountParam param, @Param("dayStart") LocalDate dayStart, @Param("dayEnd") LocalDate dayEnd);

    /**
     * 首页一级故障
     * @param startTime
     * @param endTime
     * @param nowTime
     * @return
     */
    List<FaultLevelResult> selectFirstLevelFault(String startTime, String endTime, Date nowTime);

    /**
     * 首页二三级故障
     * @param startTime
     * @param endTime
     * @param nowTime
     * @param powerDay
     * @return
     */
    List<FaultLevelResult> selectLevelFault(String startTime, String endTime, Date nowTime,Date powerDay);

    /**
     * 根据设备编号查询故障信息
     * @param page
     * @param id
     * @param param
     * @return
     */
    IPage<FaultDeviceResult> selectFaultDeviceDetail(IPage<FaultDeviceResult> page,Long id,@Param("param") FaultDeviceParam param);

    IPage<FaultStatisticsModal> selectFaultStatisticsModal(IPage<FaultStatisticsModal> page, @Param("startTime") Date startTime,
                                                           @Param("endTime") Date endTime, @Param("lineCode") String lineCode);

    Integer countUnCompleteNumByLineCode(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("lineCode")String lineCode);
    /**
     * 故障报修方式统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultWayVO> getFaultCountGroupByWay(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 故障报修状态统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultStatusVO> getFaultCountGroupByStatus(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 故障一级、二级、三级查询
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultLevelVO> getFaultGroupByLevel(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 年度维修情况统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultMonthVO> getFaultCountGroupByMonth(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 各子系统故障数据统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultSystemVO> getFaultCountGroupBySystem(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 故障总数
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    int getFaultCount(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 本周故障完成数
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    int getFaultCompleteCount(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * 故障大屏-展示列表详情
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<FaultSystemVO> selectFaultSystemVO(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("lineCode")String lineCode);

    /**
     * @Description: 未解决故障
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<FaultStatisticsModal> getUncompletedFault(@Param("startTime") Date startTime,@Param("endTime") Date endTime, @Param("lineCode") String lineCode);

    List<FaultStatisticsModal> getCompletedFault(String lineCode, DateTime startTime, DateTime endTime);
}
