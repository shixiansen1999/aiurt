package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialBaseResult;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.MaterialBaseParam;
import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface MaterialBaseMapper extends BaseMapper<MaterialBase> {

    /**
     * 物资基础-批量插入
     * @param baseList
     * @return
     */
    Integer insertBatchList(@Param("baseList") List<MaterialBase> baseList);

    /**
     * 分页列表查询
     * @param page
     * @param param
     * @return
     */
    IPage<MaterialBaseResult> queryMaterialBase(IPage<MaterialBaseResult> page, @Param("param")MaterialBaseParam param);
}

