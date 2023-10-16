package com.aiurt.modules.copy.service.impl;

import com.aiurt.modules.copy.entity.ActCustomProcessCopy;
import com.aiurt.modules.copy.mapper.ActCustomProcessCopyMapper;
import com.aiurt.modules.copy.service.IActCustomProcessCopyService;
import com.aiurt.modules.flow.dto.FlowCopyDTO;
import com.aiurt.modules.flow.dto.FlowCopyReqDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程抄送
 * @Author: aiurt
 * @Date:   2023-08-17
 * @Version: V1.0
 */
@Service
public class ActCustomProcessCopyServiceImpl extends ServiceImpl<ActCustomProcessCopyMapper, ActCustomProcessCopy> implements IActCustomProcessCopyService {
    @Override
    public IPage<FlowCopyDTO> queryPageList(Page<FlowCopyDTO> page, FlowCopyReqDTO flowCopyReqDTO,String userName) {
        IPage<FlowCopyDTO> pageList = this.baseMapper.queryPageList(page, flowCopyReqDTO,userName);
        return pageList;
    }
}
