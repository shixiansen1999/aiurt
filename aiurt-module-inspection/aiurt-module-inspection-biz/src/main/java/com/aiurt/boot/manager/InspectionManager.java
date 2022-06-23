package com.aiurt.boot.manager;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.mapper.InspectionManagerMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description: 检修模块通用业务层
 * @date 2022/6/2216:56
 */
@Service
public class InspectionManager {


    @Resource
    private InspectionManagerMapper inspectionManagerMapper;
    /**
     * 翻译专业、专业子系统信息
     * @param codeList code值
     * @param type 类型：major代表专业、subsystem代表子系统
     * @return
     */
    public String translateMajor(List<String> codeList,String type) {
        List<String> nameList = new ArrayList<>();
        if(InspectionConstant.MAJOR.equals(type)){
            nameList = inspectionManagerMapper.translateMajor(codeList);
        }
        if(InspectionConstant.SUBSYSTEM.equals(type)){
            nameList = inspectionManagerMapper.translateSubsystem(codeList);
        }
        String result = StrUtil.join(",", nameList);
        return result;
    }


}
