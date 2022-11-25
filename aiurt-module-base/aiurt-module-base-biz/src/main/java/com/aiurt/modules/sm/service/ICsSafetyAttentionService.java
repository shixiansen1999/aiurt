package com.aiurt.modules.sm.service;

import com.aiurt.modules.sm.entity.CsSafetyAttention;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 安全事项
 * @Author: aiurt
 * @Date:   2022-11-17
 * @Version: V1.0
 */
public interface ICsSafetyAttentionService extends IService<CsSafetyAttention> {
    /**
     * 导出
     * @param request
     * @param ids
     * @return
     */
    ModelAndView exportXls(HttpServletRequest request, String ids);

    /**
     * 导入
     * @param file
     * @param params
     * @return
     */
    Result importExcelMaterial(MultipartFile file, ImportParams params) throws Exception;
}
