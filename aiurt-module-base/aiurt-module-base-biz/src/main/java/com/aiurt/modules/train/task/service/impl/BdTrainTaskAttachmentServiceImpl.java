package com.aiurt.modules.train.task.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.train.task.entity.BdTrainTaskAttachment;
import com.aiurt.modules.train.task.mapper.BdTrainTaskAttachmentMapper;
import com.aiurt.modules.train.task.service.IBdTrainTaskAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 培训任务附件
 * @Author: jeecg-boot
 * @Date:   2022-04-24
 * @Version: V1.0
 */
@Service
public class BdTrainTaskAttachmentServiceImpl extends ServiceImpl<BdTrainTaskAttachmentMapper, BdTrainTaskAttachment> implements IBdTrainTaskAttachmentService {

    @Autowired
    private  BdTrainTaskAttachmentMapper bdTrainTaskAttachmentMapper;
    @Override
    public List<BdTrainTaskAttachment> getUploadTaskList(String taskId) {
        //查询上传的集合
        List<BdTrainTaskAttachment> bdTrainTaskAttachmentList = bdTrainTaskAttachmentMapper.selectList(new LambdaQueryWrapper<BdTrainTaskAttachment>().eq(BdTrainTaskAttachment::getTrainTaskId,taskId));
        return bdTrainTaskAttachmentList;
    }
}
