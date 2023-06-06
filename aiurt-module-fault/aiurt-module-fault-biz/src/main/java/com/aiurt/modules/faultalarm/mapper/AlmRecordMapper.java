package com.aiurt.modules.faultalarm.mapper;

import com.aiurt.modules.faultalarm.dto.req.AlmRecordReqDTO;
import com.aiurt.modules.faultalarm.dto.resp.AlmRecordRespDTO;
import com.aiurt.modules.faultalarm.entity.AlmRecord;
import com.aiurt.modules.faultalarm.entity.OnAlm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 告警记录mapper
 * @Author: aiurt
 * @Date: 2023-06-05
 * @Version: V1.0
 */
public interface AlmRecordMapper extends BaseMapper<AlmRecord> {
    /**
     * 查询sqlserver中的当前告警数据
     *
     * @return
     */
    List<AlmRecord> querySqlServerOnAlm();

    /**
     * 查询实时告警记录的分页列表
     *
     * @param almRecordReqDto 请求DTO，包含查询条件
     * @param page            分页参数
     * @return 响应结果，包含分页后的实时告警记录列表
     */
    Page<AlmRecordRespDTO> queryAlarmRecordPageList(@Param("page") Page<AlmRecordRespDTO> page, @Param("almRecordReqDto") AlmRecordReqDTO almRecordReqDto);
    /**
     * 查询历史告警记录的分页列表
     *
     * @param almRecordReqDto 请求DTO，包含查询条件
     * @param page            分页参数
     * @return 响应结果，包含分页后的历史告警记录列表
     */
    Page<AlmRecordRespDTO> queryAlarmRecordHistoryPageList(Page<AlmRecordRespDTO> page, AlmRecordReqDTO almRecordReqDto);
}
