package com.aiurt.modules.copy.mapper;

import com.aiurt.modules.copy.entity.ActCustomProcessCopy;
import com.aiurt.modules.flow.dto.FlowCopyDTO;
import com.aiurt.modules.flow.dto.FlowCopyReqDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 流程抄送mapper
 * @Author: aiurt
 * @Date:   2023-08-17
 * @Version: V1.0
 */
public interface ActCustomProcessCopyMapper extends BaseMapper<ActCustomProcessCopy> {
    /**
     * 查询抄送我的任务
     * @param page
     * @param flowCopyReqDTO
     * @param userName
     * @return
     */
    IPage<FlowCopyDTO> queryPageList(@Param("page") Page<FlowCopyDTO> page,
                                     @Param("condition") FlowCopyReqDTO flowCopyReqDTO,
                                     @Param("userName") String userName);
}
