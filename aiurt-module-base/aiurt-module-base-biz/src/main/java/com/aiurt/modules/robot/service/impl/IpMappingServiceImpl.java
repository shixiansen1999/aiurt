package com.aiurt.modules.robot.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.robot.entity.IpMapping;
import com.aiurt.modules.robot.manager.AreaPointTreeUtils;
import com.aiurt.modules.robot.mapper.IpMappingMapper;
import com.aiurt.modules.robot.service.IIpMappingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Service;

/**
 * @Description: ip_mapping
 * @Author: jeecg-boot
 * @Date: 2022-10-10
 * @Version: V1.0
 */
@Service
public class IpMappingServiceImpl extends ServiceImpl<IpMappingMapper, IpMapping> implements IIpMappingService {

    /**
     * 编辑页面重复性校验
     *
     * @param ipMapping
     * @return
     */
    @Override
    public Long duplicateCheckCount(IpMapping ipMapping) {
        return baseMapper.duplicateCheckCount(ipMapping);
    }

    /**
     * 添加页面校验
     *
     * @param ipMapping
     * @return
     */
    @Override
    public Long duplicateCheckCountNoId(IpMapping ipMapping) {
        return baseMapper.duplicateCheckCountNoId(ipMapping);
    }

    /**
     * 重复数据校验
     *
     * @param ipMapping
     * @return
     */
    @Override
    public Result<Object> doDuplicateCheck(IpMapping ipMapping) {
        Long num = null;

        if (StringUtils.isNotBlank(ipMapping.getId())) {
            // [2].编辑页面校验
            num = this.duplicateCheckCount(ipMapping);
        } else {
            // [1].添加页面校验
            num = this.duplicateCheckCountNoId(ipMapping);
        }

        if (num == null || num == 0) {
            // 该值可用
            return Result.OK("该值可用！");
        } else {
            return Result.error("该值不可用，系统中已存在！");
        }
    }

    /**
     * 保存ip地址映射
     *
     * @param ipMapping
     */
    @Override
    public void saveIpMapping(IpMapping ipMapping) {
        checkCreateOrUpdate(ipMapping);
        baseMapper.insert(ipMapping);
    }

    /**
     * 更新ip地址映射
     *
     * @param ipMapping
     */
    @Override
    public void updateIpMappingById(IpMapping ipMapping) {
        checkCreateOrUpdate(ipMapping);
        baseMapper.updateById(ipMapping);
    }

    /**
     * 校验ip地址的正确性
     *
     * @param ipMapping
     */
    private void checkCreateOrUpdate(IpMapping ipMapping) {
        if (ObjectUtil.isEmpty(ipMapping)) {
            throw new AiurtBootException("参数为空");
        }
        checkIpLegal(ipMapping.getOutsideIp());
        checkIpLegal(ipMapping.getInsideIp());
    }

    /**
     * 校验ip是否合法
     *
     * @param robotIp 机器人ip
     */
    private void checkIpLegal(String robotIp) {
        if (StrUtil.isEmpty(robotIp)) {
            return;
        }
        if (!AreaPointTreeUtils.ipCheck(robotIp)) {
            throw new AiurtBootException("非法ip地址");
        }
    }
}
