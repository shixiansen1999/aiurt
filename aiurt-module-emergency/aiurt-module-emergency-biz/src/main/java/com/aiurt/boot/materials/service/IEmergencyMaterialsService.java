package com.aiurt.boot.materials.service;

import com.aiurt.boot.materials.dto.EmergencyMaterialsInvoicesDTO;
import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.dto.MaterialPatrolDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterials;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: emergency_materials
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsService extends IService<EmergencyMaterials> {


    /**
     * 应急物资台账列表
     * @param pageList
     * @param condition
     * @return
     */
    Page<MaterialAccountDTO> getMaterialAccountList(Page<MaterialAccountDTO> pageList, MaterialAccountDTO condition);

    /**
     * 应急物资检查记录列表
     * @param pageList
     * @param condition
     * @return
     */
    Page<EmergencyMaterialsInvoicesItem> getInspectionRecord(Page<EmergencyMaterialsInvoicesItem> pageList, EmergencyMaterialsInvoicesItem condition);

    /**
     * 应急物资巡检登记
     * @return
     */
    MaterialPatrolDTO getMaterialPatrol();


    /**
     * 应急物资巡检登记
     * @return
     */
    MaterialPatrolDTO getStandingBook(String materialsCode,
                                      String categoryCode,
                                      String lineCode,
                                      String stationCode,
                                      String positionCode);


    /**
     * 应急物资检查记录查看
     * @param id
     * @return
     */
    Page<EmergencyMaterialsInvoicesItem> getMaterialInspection(Page<EmergencyMaterialsInvoicesItem> pageList,@Param("id") String id);


    /**
     * 应急物资台账导出数据
     * @param condition
     * @return
     */
    ModelAndView getMaterialPatrolList(MaterialAccountDTO condition);

    /**
     * 下载应急物资台账导入模板
     * @param response
     * @param request
     */
    void getImportTemplate(HttpServletResponse response, HttpServletRequest request) throws IOException;

    /**
     * 应急物资台账数据导入
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;


    /**
     * excel-应急物资检查记录导出
     * @param condition
     * @param request
     * @param response
     */
    void getInspectionRecordExportExcel(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * 压缩包-应急物资检查记录导出
     * @param condition
     * @param request
     * @param response
     */
    void getInspectionRecordExportZip(EmergencyMaterialsInvoicesDTO condition, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
