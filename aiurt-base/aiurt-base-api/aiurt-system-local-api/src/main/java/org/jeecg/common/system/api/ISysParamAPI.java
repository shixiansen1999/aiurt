package org.jeecg.common.system.api;

import org.jeecg.common.system.vo.SysParamModel;

/**
 * 系统参数配置API
 */
public interface ISysParamAPI {
    /**
     * 根据系统参数编码查询一条系统参数配置记录
     * @return
     */
    SysParamModel selectByCode(String code);
}
