package com.aiurt.boot.manager.mapper;

import java.util.List;

/**
 * @Description: repair_pool_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface InspectionManagerMapper {

    /**
     * 翻译专业信息
     * @param codeList
     * @return
     */
    List<String> translateMajor(List<String> codeList);
    /**
     * 翻译专业子系统信息
     * @param codeList
     * @return
     */
    List<String> translateSubsystem(List<String> codeList);
}
