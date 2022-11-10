package com.aiurt.modules.manufactor.service;

import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: cs_manufactor
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsManufactorService extends IService<CsManufactor> {
    /**
     * 添加
     *
     * @param csManufactor
     * @return
     */
    Result<?> add(CsManufactor csManufactor);
    /**
     * 编辑
     *
     * @param csManufactor
     * @return
     */
    Result<?> update(CsManufactor csManufactor);


    /**
     * 导入数据
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
