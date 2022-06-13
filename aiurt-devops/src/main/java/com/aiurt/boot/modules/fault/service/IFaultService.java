package com.aiurt.boot.modules.fault.service;

import com.aiurt.boot.modules.fault.dto.FaultDTO;
import com.aiurt.boot.modules.fault.entity.Fault;
import com.aiurt.boot.modules.fault.param.FaultCountParam;
import com.aiurt.boot.modules.fault.param.FaultDeviceParam;
import com.aiurt.boot.modules.fault.param.FaultParam;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.common.result.*;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 故障表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IFaultService extends IService<Fault> {


    /**
     * 查询故障列表
     * @param page
     * @param param
     * @param req
     * @return
     */
    IPage<FaultResult> pageList(IPage<FaultResult> page, FaultParam param, HttpServletRequest req);

    /**
     * 导出故障列表
     * @param param
     * @return
     */
    List<FaultResult> exportXls(FaultParam param);

    /**
     * 故障登记
     * @param fault
     * @param req
     * @return
     */
     Result<?> add(FaultDTO fault, HttpServletRequest req);

    /**
     * 根据code查询故障信息
     * @param code
     * @return
     */
    FaultResult getFaultDetail(String code);

    /**
     * 挂起
     * @param id
     * @param remark
     * @return
     */
     Result hangById(Integer id,String remark);

    /**
     * 取消挂起
     * @param id
     * @return
     */
     Result cancelHang(Integer id);

    /**
     * 报表统计故障数量
     * @param startTime
     * @param endTime
     * @return
     */
    Result<FaultNumResult> getFaultNum(String startTime, String endTime);

    /**
     * 报表统计超时故障数量
     * @param startTime
     * @param endTime
     * @return
     */
    Result<TimeOutFaultNum> getTimeOutFaultNum(String startTime, String endTime);

    /**
     * pc首页报表统计超时挂起数量
     * @param startTime
     * @param endTime
     * @return
     */
    Result<TimeOutFaultNum> getTimeOutHangNum(String startTime, String endTime);

    /**
     * 查询故障数量
     * @param param
     * @return
     */
    Result<List<FaultCountResult>> getFaultCount(FaultCountParam param);

    /**
     * 各系统检修/自检对比
     * @param param
     * @return
     */
    Result<List<FaultCountResult>> getContrast(FaultCountParam param);

    /**
     * 各系统故障数比较
     * @param param
     * @return
     */
    Result<List<FaultCountResult>> getPercentage(FaultCountParam param);

    /**
     * 单一系统检修/自检各月份故障分析
     * @param param
     * @return
     */
    Result<List<FaultMonthResult>> getFaultNumByMonth(FaultCountParam param);

    /**
     * 设备故障总数同比分析
     * @param param
     * @return
     */
    Result<List<FaultMonthResult>>  getFaultByMonth (FaultCountParam param);

    /**
     * 首页一级故障
     * @param dayStart
     * @param dayEnd
     * @return
     */
    Result<List<FaultLevelResult>> getFirstLevelFault(String dayStart,String dayEnd);

    /**
     * 首页二级故障
     * @param dayStart
     * @param dayEnd
     * @return
     */
    Result<List<FaultLevelResult>> getSecondLevelFault(String dayStart,String dayEnd);

    /**
     * 首页三级故障
     * @param dayStart
     * @param dayEnd
     * @return
     */
    Result<List<FaultLevelResult>> getThirdLevelFault(String dayStart,String dayEnd);


    /**
     * 维修人员故障信息
     * @param vo
     * @return
     */
    List<UserAndAmountVO> getFaultPersonDetail(StatisticsVO vo);

    /**
     * 根据设备编号查询故障信息
     * @param page
     * @param code
     * @param param
     * @return
     */
    IPage<FaultDeviceResult> getFaultDeviceDetail(IPage<FaultDeviceResult> page,String code, FaultDeviceParam param);

    /**
     * 维修时长
     * @param vo
     * @return
     */
    Map<String, Long>  getFaultDuration(StatisticsVO vo);

    /**
     * 配合施工总人次
     * @param vo
     * @return
     */
    Map<String, Integer>  getAssortNum(StatisticsVO vo);

    /**
     * 故障报修方式统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultWayVO> getFaultCountGroupByWay(Date startTime, Date endTime, String lineCode);

    /**
     * 故障完成情况对比统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultStatusVO> getFaultCountGroupByStatus(Date startTime, Date endTime, String lineCode);

    /**
     * 故障一级、二级、三级统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultLevelVO> getFaultGroupByLevel(Date startTime, Date endTime, String lineCode);

    /**
     * 年度维修情况统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultMonthVO> getFaultCountGroupByMonth(Date startTime, Date endTime, String lineCode);

    /**
     * 各子系统故障数据统计
     * @param startTime
     * @param endTime
     * @param lineCode
     * @return
     */
    List<StatisticsFaultSystemVO> getFaultCountGroupBySystem(Date startTime, Date endTime, String lineCode);

    /**
     * 故障数据统计
     * @param lineCode
     * @return
     */
    StatisticsFaultCountVO getFaultCountAndDetails(String lineCode);
}
