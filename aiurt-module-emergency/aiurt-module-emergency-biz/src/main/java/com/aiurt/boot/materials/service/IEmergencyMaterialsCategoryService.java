package com.aiurt.boot.materials.service;


import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsCategoryService extends IService<EmergencyMaterialsCategory> {

    List<EmergencyMaterialsCategory> selectTreeList();

    /**
     * 应急物资分类模板下载
     * @param response
     * @param request
     */
    void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException;

    /**
     * 应急物资分类导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
