package com.aiurt.boot.check.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.check.entity.FixedAssetsCheckCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: fixed_assets_check_category
 * @Author: aiurt
 * @Date: 2023-01-17
 * @Version: V1.0
 */
public interface FixedAssetsCheckCategoryMapper extends BaseMapper<FixedAssetsCheckCategory> {
    /**
     * 根据盘点任务记录ID获取物资分类信息
     *
     * @param id
     * @return
     */
    List<FixedAssetsCheckCategory> getCategoryList(@Param("id") String id);
}
