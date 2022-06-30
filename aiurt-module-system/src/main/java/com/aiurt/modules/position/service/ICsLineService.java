package com.aiurt.modules.position.service;

import com.aiurt.modules.position.entity.CsLine;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: cs_line
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface ICsLineService extends IService<CsLine> {
    /**
     * 添加
     *
     * @param csLine
     * @return
     */
    Result<?> add(CsLine csLine);
    /**
     * 编辑
     *
     * @param csLine
     * @return
     */
    Result<?> update(CsLine csLine);
}
