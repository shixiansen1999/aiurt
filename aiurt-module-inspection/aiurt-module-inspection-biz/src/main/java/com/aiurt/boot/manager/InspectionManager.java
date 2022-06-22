package com.aiurt.boot.manager;

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
     * 翻译专业信息
     *
     * @param codeList
     * @return
     */
    public String translateMajor(List<String> codeList) {
        List<String> result = new ArrayList<>();
        return "";
    }


}
