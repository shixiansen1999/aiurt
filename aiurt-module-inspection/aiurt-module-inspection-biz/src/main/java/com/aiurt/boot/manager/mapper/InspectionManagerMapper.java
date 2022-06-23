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
    /**
     * 翻译组织机构信息
     * @param codeList
     * @return
     */
    List<String> translateOrg(List<String> codeList);
    /**
     * 翻译站点信息
     * @param
     * @return
     */
    String translateStation(String code);

    /**
     * 翻译线路信息
     * @param lineCode
     * @return
     */
    String translateLine(String lineCode);

    /**
     * 翻译位置信息
     * @param positionCode
     * @return
     */
    String translatePosition(String positionCode);
}
