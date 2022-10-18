package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.entity.IpMapping;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: ip_mapping
 * @Author: jeecg-boot
 * @Date: 2022-10-10
 * @Version: V1.0
 */
public interface IIpMappingService extends IService<IpMapping> {


    /**
     * 编辑页面重复性校验
     *
     * @param ipMapping
     * @return
     */
    Long duplicateCheckCount(IpMapping ipMapping);

    /**
     * 添加页面校验
     *
     * @param ipMapping
     * @return
     */
    Long duplicateCheckCountNoId(IpMapping ipMapping);

    /**
     * 重复数据校验
     * @param ipMapping
     * @return
     */
    Result<Object> doDuplicateCheck(IpMapping ipMapping);

    /**
     * 保存ip地址映射
     * @param ipMapping
     */
    void saveIpMapping(IpMapping ipMapping);

    /**
     * 更新ip地址映射
     * @param ipMapping
     */
    void updateIpMappingById(IpMapping ipMapping);
}
