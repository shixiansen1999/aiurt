package com.aiurt.boot.modules.worklog.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.result.WorkLogResult;
import com.swsc.copsms.common.system.api.ISysBaseAPI;
import com.swsc.copsms.modules.worklog.dto.WorkLogDTO;
import com.swsc.copsms.modules.worklog.entity.WorkLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.worklog.param.WorkLogParam;

import javax.servlet.http.HttpServletRequest;

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
     * @return
     */
    public Result add(WorkLogDTO dto, HttpServletRequest req);

    /**
     * 日志列表
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<WorkLogResult> pageList(IPage<WorkLogResult> page, Wrapper<WorkLogResult> queryWrapper, WorkLogParam param);

    /**
     * 根据id假删除
     * @param id
     */
    public Result deleteById(Integer id);

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
    Result confirm(Integer id);

    /**
     * 批量确认
     */
    Result<?>  checkByIds(String ids);
}
