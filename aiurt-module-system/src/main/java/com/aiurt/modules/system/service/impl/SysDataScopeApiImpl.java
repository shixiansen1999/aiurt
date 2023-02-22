package com.aiurt.modules.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.system.mapper.CsUserStaionMapper;
import com.aiurt.modules.system.mapper.CsUserSubsystemMapper;
import com.aiurt.modules.system.service.ICsUserDepartService;
import com.aiurt.modules.system.service.ICsUserMajorService;
import lombok.RequiredArgsConstructor;
import org.jeecg.common.system.api.ISysDataScopeApi;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.CsUserStationModel;
import org.jeecg.common.system.vo.CsUserSubsystemModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/69:38
 */
@RequiredArgsConstructor
@Service("sdss")
public class SysDataScopeApiImpl implements ISysDataScopeApi {

    private final CsUserStaionMapper csUserStaionMapper;

    private final CsUserSubsystemMapper csUserSubsystemMapper;

    private final ICsUserMajorService iCsUserMajorService;

    private final ICsUserDepartService iCsUserDepartService;

    @Override
    public String getStationByUserIdStr(String id) {
        List<CsUserStationModel> list = csUserStaionMapper.getStationByUserId(id);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().map(st -> "'" + st.getStationCode() + "'").collect(Collectors.joining(","));
        }
        return "";
    }

    @Override
    public String getSubsystemByUserIdStr(String id) {
        List<CsUserSubsystemModel> list = csUserSubsystemMapper.getSubsystemByUserId(id);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().map(st -> "'" + st.getSystemCode() + "'").collect(Collectors.joining(","));
        }
        return "";
    }

    @Override
    public String getMajorByUserIdStr(String id) {
        List<CsUserMajorModel> list = iCsUserMajorService.getMajorByUserId(id);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().map(st -> "'" + st.getMajorCode() + "'").collect(Collectors.joining(","));
        }
        return "";
    }

    @Override
    public String getDepartByUserIdStr(String id) {
        List<CsUserDepartModel> list = iCsUserDepartService.getDepartByUserId(id);
        if (CollUtil.isNotEmpty(list)) {
            return list.stream().map(st -> "'" + st.getOrgCode() + "'").collect(Collectors.joining(","));
        }
        return "";
    }
}
