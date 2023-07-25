package com.aiurt.modules.outsourcingpersonnel.mapper;

import com.aiurt.common.result.OutsourcingPersonnelResult;
import com.aiurt.modules.outsourcingpersonnel.entity.OutsourcingPersonnel;
import com.aiurt.modules.outsourcingpersonnel.param.OutsourcingPersonnelParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : sbx
 * @Classname : OutsourcingPersonnelMapper
 * @Description : 委外人员
 * @Date : 2023/7/24 8:54
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