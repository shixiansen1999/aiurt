package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.common.result.OutsourcingPersonnelResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.fault.entity.OutsourcingPersonnel;
import com.aiurt.boot.modules.fault.param.OutsourcingPersonnelParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 委外人员
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface OutsourcingPersonnelMapper extends BaseMapper<OutsourcingPersonnel> {

    /**
     * 查询委外人员
     * @param page
     * @param param
     * @return
     */
    IPage<OutsourcingPersonnelResult> queryOutsourcingPersonnel(IPage<OutsourcingPersonnelResult> page, @Param("personnel") OutsourcingPersonnelParam param);

    /**
     * 委外人员导出
     * @param param
     * @return
     */
    List<OutsourcingPersonnelResult> exportXls(@Param("personnel") OutsourcingPersonnelParam param);

}
