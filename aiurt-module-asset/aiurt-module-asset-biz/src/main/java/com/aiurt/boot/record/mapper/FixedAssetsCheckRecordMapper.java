package com.aiurt.boot.record.mapper;

import java.util.List;

import com.aiurt.boot.record.dto.FixedAssetsCheckRecordDTO;
import com.aiurt.boot.record.vo.FixedAssetsCheckRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.record.entity.FixedAssetsCheckRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface FixedAssetsCheckRecordMapper extends BaseMapper<FixedAssetsCheckRecord> {
    /**
     * 固定资产管理盘点结果-下发前的物资数据信息
     *
     * @return
     */
    Page<FixedAssetsCheckRecordVO> pageList(@Param("page") Page<FixedAssetsCheckRecordVO> page,
                                          @Param("condition") FixedAssetsCheckRecordDTO fixedAssetsCheckRecordDTO,
                                          @Param("categoryCodes") List<String> categoryCodes,
                                          @Param("orgCodes") List<String> orgCodes);
}
