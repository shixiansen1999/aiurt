package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.modules.fault.entity.OutsourcingPersonnel;
import com.swsc.copsms.modules.fault.param.OutsourcingPersonnelParam;
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
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<OutsourcingPersonnel> queryOutsourcingPersonnel(IPage<OutsourcingPersonnel> page, Wrapper<OutsourcingPersonnel> queryWrapper, @Param("personnel") OutsourcingPersonnelParam param);


    /**
     * 根据id删除
     * @param id
     * @return
     */
    int deleteOne(@Param("id") Integer id);

    /**
     * 查询所有委外人员
     * @return
     */
    List<OutsourcingPersonnel> selectAll();
}
