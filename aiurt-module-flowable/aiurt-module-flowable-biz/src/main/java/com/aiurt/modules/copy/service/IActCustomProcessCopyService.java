package com.aiurt.modules.copy.service;

import com.aiurt.modules.copy.entity.ActCustomProcessCopy;
import com.aiurt.modules.flow.dto.FlowCopyDTO;
import com.aiurt.modules.flow.dto.FlowCopyReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程抄送
 * @Author: aiurt
 * @Date:   2023-08-17
 * @Version: V1.0
 */
public interface IActCustomProcessCopyService extends IService<ActCustomProcessCopy> {
    /**
     * 查询流程抄送信息
     * @param page
     * @param flowCopyReqDTO
     * @param userName
     * @return
     */
    IPage<FlowCopyDTO> queryPageList(Page<FlowCopyDTO> page, FlowCopyReqDTO flowCopyReqDTO,String userName);

}
