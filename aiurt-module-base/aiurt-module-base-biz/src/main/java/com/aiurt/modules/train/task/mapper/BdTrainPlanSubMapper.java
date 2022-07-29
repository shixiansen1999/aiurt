package com.aiurt.modules.train.task.mapper;

import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description: 年子计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
public interface BdTrainPlanSubMapper extends BaseMapper<BdTrainPlanSub> {

    //根据年计划删除子计划
    void deleteByPlanId(String id);

    //查询子计划
    List<BdTrainPlanSub> getByPlanId(String id);

    /**
     * 查询已使用的子计划
     * @param bdTrainPlanSub
     * @param pageList
     * @param orgCode
     * @return
     */
    List<BdTrainPlanSub> getList(@Param("pageList") Page<BdTrainPlanSub> pageList, @Param("bdTrainPlanSub") BdTrainPlanSub bdTrainPlanSub,@Param("orgCode")String orgCode );
}
