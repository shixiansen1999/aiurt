package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartLend;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SparePartLendParam;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.SparePartLendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 备件借出表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface SparePartLendMapper extends BaseMapper<SparePartLend> {

    /**
     * 备件借出分页查询
     * @param page
     * @param param
     * @return
     */
    IPage<SparePartLendVO> queryPageList(Page<SparePartLendVO> page,
                                         @Param("param") SparePartLendParam param);

    /**
     * 备件借出导出excel所需数据
     * @param param
     * @return
     */
    List<SparePartLendVO> queryExportXls(@Param("param") SparePartLendParam param);
}
