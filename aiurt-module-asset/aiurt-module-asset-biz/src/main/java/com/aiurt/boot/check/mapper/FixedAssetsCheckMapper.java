package com.aiurt.boot.check.mapper;

import com.aiurt.boot.check.dto.FixedAssetsCheckDTO;
import com.aiurt.boot.check.entity.FixedAssetsCheck;
import com.aiurt.boot.check.vo.CheckUserVO;
import com.aiurt.boot.check.vo.FixedAssetsCheckVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date: 2023-01-11
 * @Version: V1.0
 */
public interface FixedAssetsCheckMapper extends BaseMapper<FixedAssetsCheck> {
    /**
     * 分页查询
     *
     * @param page
     * @param fixedAssetsCheck
     * @return
     */
    Page<FixedAssetsCheck> selectPageList(Page<FixedAssetsCheck> page, @Param("fixedAssetsCheck") FixedAssetsCheck fixedAssetsCheck);

    /**
     * 翻译
     *
     * @param orgCode
     * @return
     */
    List<String> selectOrgName(@Param("orgCode") List<String> orgCode);

    /**
     * @param categoryCode
     * @return
     */
    List<String> selectCategoryName(@Param("categoryCode") List<String> categoryCode);

    /**
     * 固定资产盘点任务信息表-分页列表查询
     *
     * @param page
     * @param fixedAssetsCheckDTO
     * @return
     */
    IPage<FixedAssetsCheckVO> pageList(@Param("page") Page<FixedAssetsCheckVO> page, @Param("condition") FixedAssetsCheckDTO fixedAssetsCheckDTO);

    /**
     * 固定资产盘点任务信息表-分页列表查询
     * @param id
     * @return
     */
    FixedAssetsCheckVO queryById(String id);

    /**
     * 固定资产盘点任务记录-记录的盘点人下拉接口
     *
     * @return
     */
    List<CheckUserVO> checkUserInfo();

    /**
     * 查询分类下的子级
     * @param categoryCode
     * @return
     */
    List<String> selectCategoryCodeByPid(@Param("categoryCode") String categoryCode);
}
