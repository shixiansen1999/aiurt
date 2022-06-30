package com.aiurt.modules.manufactor.service;

import com.aiurt.modules.manufactor.entity.CsManufactor;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
}
