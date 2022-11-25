package com.aiurt.boot.standard.service;


import com.aiurt.boot.manager.dto.InspectionCodeDTO;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: inspection_code
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IInspectionCodeService extends IService<InspectionCode> {
    /**
     * 分页查询
     * @param page
     * @param inspectionCodeDTO
     * @return
     */
    IPage<InspectionCodeDTO> pageList(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO);

    /**
     * 逻辑删除
     * @param id
     */
    void updateDelFlag(String id);
    /**
     * 分页列表查询是否配置巡检项
     * @param page
     * @param inspectionCodeDTO
     * @return
     */
    IPage<InspectionCodeDTO> pageLists(Page<InspectionCodeDTO> page, InspectionCodeDTO inspectionCodeDTO);

    /**
     * 导入数据
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
