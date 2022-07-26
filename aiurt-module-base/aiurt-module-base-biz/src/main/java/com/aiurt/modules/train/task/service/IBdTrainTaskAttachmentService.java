package com.aiurt.modules.train.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.task.entity.BdTrainTaskAttachment;

import java.util.List;

/**
 * @Description: 培训任务附件
 * @Author: jeecg-boot
 * @Date:   2022-04-24
 * @Version: V1.0
 */
public interface IBdTrainTaskAttachmentService extends IService<BdTrainTaskAttachment> {

    /**
     * 根据任务taskId查询附件
     * @param taskId
     * @return
     */
    List<BdTrainTaskAttachment> getUploadTaskList(String taskId);
}
