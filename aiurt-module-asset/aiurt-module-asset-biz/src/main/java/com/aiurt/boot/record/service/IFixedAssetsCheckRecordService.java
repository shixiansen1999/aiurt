package com.aiurt.boot.record.service;

import com.aiurt.boot.record.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface IFixedAssetsCheckRecordService extends IService<FixedAssetsCheckRecord> {
    /**
     * 固定资产盘点记录表-分页列表查询
     *
     * @param page
     * @param fixedAssetsCheckRecordDTO
     * @return
     */
    IPage<FixedAssetsCheckRecord> queryPageList(Page<FixedAssetsCheckRecord> page, FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO);

    /**
     * 固定资产盘点记录-盘点结果记录不分页查询
     *
     * @param fixedAssetsCheckRecordDTO
     * @return
     */
    List<FixedAssetsCheckRecord> nonsortList(FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO);
}
