package com.aiurt.boot.modules.worklog.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.result.WorkLogResult;
import com.swsc.copsms.modules.worklog.entity.WorkLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.worklog.param.WorkLogParam;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface WorkLogMapper extends BaseMapper<WorkLog> {


    /**
     * 查询日志
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<WorkLogResult> queryWorkLog(IPage<WorkLogResult> page, Wrapper<WorkLogResult> queryWrapper,
                                      @Param("param") WorkLogParam param);


    /**
     * 删除故障知识库类型
     * @param id
     * @return
     */
    int deleteOne(@Param("id") Integer id);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    WorkLogResult selectById(Integer id);

    /**
     * 通过id确认
     * @param id
     * @return
     */
    int confirm(Integer id);

    /**
     * 通过id审核
     * @param id
     * @return
     */
    int check(String id);

}
