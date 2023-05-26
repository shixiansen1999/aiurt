package com.aiurt.modules.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.system.entity.SysUserPositionCurrent;
import com.aiurt.modules.system.mapper.SysUserPositionCurrentMapper;
import com.aiurt.modules.system.service.ISysUserPositionCurrentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户连接站点wifi的当前位置
 * @author
 */
@Service
@Slf4j
public class SysUserPositionCurrentServiceImpl extends ServiceImpl<SysUserPositionCurrentMapper, SysUserPositionCurrent> implements ISysUserPositionCurrentService{

    @Override
    public void saveOrUpdateOne(SysUserPositionCurrent sysUserPositionCurrent) {
        // 根据create_by查询是否已存在
        LambdaQueryWrapper<SysUserPositionCurrent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPositionCurrent::getCreateBy, sysUserPositionCurrent.getCreateBy());
        SysUserPositionCurrent one;
        try{
            one = this.getOne(queryWrapper);
        }catch (Exception e){
            throw new AiurtBootException("用户连接wifi当前位置表中用户:" + sysUserPositionCurrent.getCreateBy() +" 不唯一");
        }
        if (sysUserPositionCurrent.getStationCode() == null) {
            // 站点为空时，设置为异常
            sysUserPositionCurrent.setIsPositionError("1");
        }
        if (ObjectUtil.isNull(one)) {
            // 不存在，添加
            this.save(sysUserPositionCurrent);
        }else {
            // 已存在，更新
            if (StrUtil.equals(one.getStationCode(), sysUserPositionCurrent.getStationCode())) {
                // 同站点，或者都是null，不更新upload_time
                sysUserPositionCurrent.setUploadTime(null);
            }
            sysUserPositionCurrent.setId(one.getId());
            this.updateById(sysUserPositionCurrent);
        }

    }
}
