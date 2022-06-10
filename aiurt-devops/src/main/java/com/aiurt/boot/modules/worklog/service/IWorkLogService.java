package com.aiurt.boot.modules.worklog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.aiurt.boot.common.result.LogCountResult;
import com.aiurt.boot.common.result.LogResult;
import com.aiurt.boot.common.result.LogSubmitCount;
import com.aiurt.boot.common.result.WorkLogResult;
import com.aiurt.boot.modules.worklog.dto.WorkLogDTO;
import com.aiurt.boot.modules.worklog.entity.WorkLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.worklog.param.LogCountParam;
import com.aiurt.boot.modules.worklog.param.WorkLogParam;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
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
    IPage<WorkLogResult> pageList(IPage<WorkLogResult> page,WorkLogParam param, HttpServletRequest req);

    /**
     * 工作日志导出
     * @param param
     * @param req
     * @return
     */
    List<WorkLogResult> exportXls(WorkLogParam param, HttpServletRequest req);

    /**
     * 工作日志上报-分页列表查询
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
     Result<?> deleteById (Integer id);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    WorkLogResult getDetailById(Integer id);

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
    Result<LogResult>  getWaitMessage(String nowday,HttpServletRequest req);

    /**
     * 编辑工作日志
     * @param dto
     * @return
     */
    Result editWorkLog(WorkLogDTO dto);

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
    Result<LogSubmitCount> getLogSubmitNum(String startTime,String endTime);
}
