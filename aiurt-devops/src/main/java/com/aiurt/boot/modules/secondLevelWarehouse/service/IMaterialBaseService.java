package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.aiurt.boot.modules.secondLevelWarehouse.entity.MaterialBase;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.MaterialBaseResult;
import com.aiurt.boot.modules.secondLevelWarehouse.vo.MaterialBaseParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IMaterialBaseService extends IService<MaterialBase> {

    /**
     * 根据物资编号查询物料类型
     * @param code  物资编号
     * @return
     */
    Integer getTypeByMaterialCode(String code);

    /**
     * 分页查询
     * @param page
     * @param param
     * @return
     */
    IPage<MaterialBaseResult> pageList(IPage<MaterialBaseResult> page, MaterialBaseParam param);

    /**
     * excel导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);
}
