package com.aiurt.modules.worklog.service;

import com.aiurt.common.result.*;
import com.aiurt.modules.worklog.dto.WorkLogDTO;
import com.aiurt.modules.worklog.dto.WorkLogIndexDTO;
import com.aiurt.modules.worklog.dto.WorkLogUserTaskDTO;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
public interface IWorkLogService extends IService<WorkLog> {

    /**
     * 添加工作日志
     * @param dto
     * @param req
     * @return
     */
    Result<?> add (WorkLogDTO dto, HttpServletRequest req);

    /**
     * 工作日志上报-分页列表查询
     * @param page
     * @param param
     * @param req
     * @return
     */
    IPage<WorkLogResult> pageList(IPage<WorkLogResult> page, WorkLogParam param, HttpServletRequest req);

    /**
     * 工作日志导出
     * @param param
     * @param req
     * @return
     */
    List<WorkLogResult> exportXls(WorkLogParam param, HttpServletRequest req);

    /**
     * 工作日志确认-分页列表查询
     * @param page
     * @param param
     * @param req
     * @return
     */
    IPage<WorkLogResult> queryConfirmList(IPage<WorkLogResult> page,WorkLogParam param, HttpServletRequest req);

    /**
     * 根据id假删除
     * @param id
     * @return
     */
    Result<?> deleteById (String id);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    WorkLogDTO getDetailById(String id);

    /**
     * 工作日志确认
     * @param id
     * @return
     */
    Result<?> confirm (Integer id);

    /**
     * 批量确认
     * @param ids
     * @return
     */
    Result<?> checkByIds(String ids);


    /**
     * 根据当前登录人id获取巡检检修故障待办消息
     * @param nowday
     * @param req
     * @return
     */
    Result<LogResult>  getWaitMessage(String nowday, HttpServletRequest req);

    /**
     * 编辑工作日志
     * @param dto
     * @return
     */
    void editWorkLog(WorkLogDTO dto);

    /**
     * 日志统计
     * @param param
     * @return
     */
    List<LogCountResult> getLogCount(LogCountParam param);

    /**
     * 首页工作日志提交数量
     * @param startTime
     * @param endTime
     * @return
     */
    Result<LogSubmitCount> getLogSubmitNum(String startTime, String endTime);

    /**
     * 查看当前用户，当天的检修、巡检、故障的工单
     * @return
     */
     WorkLogUserTaskDTO getUseTask();

    /**
     * 工作日志通过id查看
     * @param id
     * @return
     */
    WorkLogDetailResult queryWorkLogDetail(String id);

    /**
     * 工作日志通过id查看，但是返回的是早班、晚班的多条数据
     * @param id
     * @return
     */
    List<WorkLogDetailResult> queryWorkLogDetailList(String id);

    /**
     * 今日工作内容
     * @param nowday
     * @return
     */
    Map getTodayJobContent(String nowday);

    void archWorkLog(WorkLogResult workLogResult, String token, String archiveUserId, String refileFolderId, String realname, String sectId);

    /**
     * 最新的未完成事项
     * @return
     */
    Result<String> getUnfinishedMatters();
    /**
     * 判断是否能编辑
     * @param createTime
     * @param confirmStatus
     * @param checkStatus
     * @return
     * */
    Boolean editFlag(Date createTime, Integer  confirmStatus, Integer  checkStatus);

    /**
     * 获取首页-工作日志
     * 获取首页工作日志的信息
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param request   request
     * @return
     */
    WorkLogIndexDTO getOverviewInfo(Date startDate, Date endDate, HttpServletRequest request);

    /**
     * 批量打印获取数据
     * @param page
     * @param param
     * @param req
     * @return
     */
    List<List<WorkLogDetailResult>> batchPrint(Page<WorkLogResult> page, WorkLogParam param, HttpServletRequest req);
}

