package com.aiurt.modules.floodpreventioninformation.service;

import com.aiurt.modules.floodpreventioninformation.entity.FloodPreventionInformation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-24
 * @Version: V1.0
 */
public interface IFloodPreventionInformationService extends IService<FloodPreventionInformation> {

    /**
     * 通过excel导入数据
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;


    /**
     * 下载模板
     * @param response
     * @throws IOException
     */
    void exportTemplateXl(HttpServletResponse response) throws IOException;

}
