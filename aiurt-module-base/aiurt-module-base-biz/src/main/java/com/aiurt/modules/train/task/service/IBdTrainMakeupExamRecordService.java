package com.aiurt.modules.train.task.service;

import com.aiurt.modules.train.task.entity.BdTrainMakeupExamRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;
import java.util.List;

/**
 * @Description: 培训补考记录
 * @Author: jeecg-boot
 * @Date:   2022-04-20
 * @Version: V1.0
 */
public interface IBdTrainMakeupExamRecordService extends IService<BdTrainMakeupExamRecord> {

    /**
     * 分页列表查询
     * @param page
     * @param bdTrainMakeupExamRecord
     * @return
     */
    IPage<BdTrainMakeupExamRecord> getList(Page<BdTrainMakeupExamRecord> page, BdTrainMakeupExamRecord bdTrainMakeupExamRecord);


    /**
     * 批准补考
     * @param id
     * @param makeUpTime
     * @throws ParseException
     */
    void makeUp(String id,String makeUpTime) throws ParseException;


    /**
     * 批量批准
     * @param makeUpTime
     * @param idList
     */
    void batchMakeUp(String makeUpTime, List<String> idList);
}
