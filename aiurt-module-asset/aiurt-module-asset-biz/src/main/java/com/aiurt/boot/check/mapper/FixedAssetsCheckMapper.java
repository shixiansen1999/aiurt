package com.aiurt.boot.check.mapper;

import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
public interface FixedAssetsCheckMapper extends BaseMapper<FixedAssetsCheck> {
    /**
     * 分页查询
     * @param page
     * @param fixedAssetsCheck
     * @return
     */
    Page<FixedAssetsCheck> selectPageList(Page<FixedAssetsCheck> page,@Param("fixedAssetsCheck") FixedAssetsCheck fixedAssetsCheck);

    /**
     * 翻译
     * @param orgCode
     * @return
     */
    List<String> selectOrgName(@Param("orgCode")List<String> orgCode);

    /**
     *
     * @param categoryCode
     * @return
     */
    List<String> selectCategoryName(@Param("categoryCode") List<String> categoryCode);
}
