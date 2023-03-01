package com.aiurt.modules.worklog.mapper;

import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.common.result.AssortNumResult;
import com.aiurt.common.result.LogCountResult;
import com.aiurt.common.result.WorkLogDetailResult;
import com.aiurt.common.result.WorkLogResult;
import com.aiurt.modules.worklog.entity.WorkLog;
import com.aiurt.modules.worklog.param.LogCountParam;
import com.aiurt.modules.worklog.param.PatrolAppHomeParam;
import com.aiurt.modules.worklog.param.WorkLogParam;
import com.aiurt.modules.worklog.vo.WorkLogVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 工作日志
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@EnableDataPerm(excluseMethodName = {"deleteOne"})
public interface WorkLogMapper extends BaseMapper<WorkLog> {


    /**
     * 查询日志
     * @param page
     * @param param
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "wl.org_id")
    })
    IPage<WorkLogResult> queryWorkLog(IPage<WorkLogResult> page, @Param("param") WorkLogParam param);

    /**
     * 工作日志导出
     * @param param
     * @return
     */
    List<WorkLogResult> exportXls(@Param("param") WorkLogParam param);


    /**
     * 删除故障知识库类型
     * @param id
     * @return
     */
    int deleteOne(@Param("id") String id);

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    WorkLogResult queryById(String id);

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

    /**
     * 查询工作日志待办消息
     * @param homeParam
     * @return
     */
    List<WorkLogVO> selectAppHome(@Param("param") PatrolAppHomeParam homeParam);

    /**
     * 日志统计
     * @param param
     * @return
     */
    List<LogCountResult> selectLogCount(@Param("param") LogCountParam param);

    /**
     * 配合施工人次
     * @param startTime
     * @param endTime
     * @param userName
     * @return
     */
    List<AssortNumResult> getAssortNum(@Param("startTime") String startTime, @Param("entTime") String endTime,
                                       @Param("userName") String userName);

    /**
     * 日志数量
     * @return
     */
    Integer selectWorkLogCount();

    /**
     * 根据id查询详情
     * @param id
     * @return
     */
    WorkLogDetailResult queryWorkLogById(String id);

    List<LogCountResult> selectOrgLogCount(@Param("param") LogCountParam param);
}

