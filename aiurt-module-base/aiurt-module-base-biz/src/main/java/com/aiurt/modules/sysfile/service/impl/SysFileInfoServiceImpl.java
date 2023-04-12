package com.aiurt.modules.sysfile.service.impl;

import com.aiurt.modules.sysfile.entity.SysFileInfo;
import com.aiurt.modules.sysfile.mapper.SysFileInfoMapper;
import com.aiurt.modules.sysfile.service.ISysFileInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zwl
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysFileInfoServiceImpl extends ServiceImpl<SysFileInfoMapper, SysFileInfo> implements ISysFileInfoService {

}
