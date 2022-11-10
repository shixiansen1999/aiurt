package com.aiurt.modules.major.service;

import com.aiurt.modules.major.entity.CsMajor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @Description: cs_major
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsMajorService extends IService<CsMajor> {
    /**
     * 添加
     *
     * @param csMajor
     * @return
     */
    Result<?> add(CsMajor csMajor);
    /**
     * 编辑
     *
     * @param csMajor
     * @return
     */
    Result<?> update(CsMajor csMajor);

    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
