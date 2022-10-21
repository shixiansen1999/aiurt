package com.aiurt.modules.train.task.mapper;

import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 培训补考记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface BdTrainMakeupExamRecordMapper extends BaseMapper<BdTrainMakeupExamRecord> {

    /**
     * 分页列表查询
     *
     * @param page
     * @param bdTrainMakeupExamRecord
     * @return
     */
    List<BdTrainMakeupExamRecord> getList(Page<BdTrainMakeupExamRecord> page, @Param("bdTrainMakeupExamRecord") BdTrainMakeupExamRecord bdTrainMakeupExamRecord);

    /**
     * 查询最近一次补考时间
     *
     * @param trainTaskId
     * @return
     */
    String getLastMakeUpTime(String trainTaskId);

    /**
     * 查询最近一次定时任务id
     *
     * @param trainTaskId
     * @param userId
     * @return
     */
    String getQuartzJobIdById(String userId,String trainTaskId);

    /**
     * 根据id查定时任务
     *
     * @param id
     * @return
     */
    QuartzJobDTO getQuartzJobDTO(String id);
}
