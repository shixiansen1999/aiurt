package com.aiurt.boot.manager.mapper;

import java.util.List;

/**
 * @Description: Common
 * @Author: aiurt
 * @Date:   2022-07-11
 * @Version: V1.0
 */
public interface PatrolManagerMapper {
    /**
     * 翻译组织机构信息
     * @param codeList
     * @return
     */
    List<String> translateOrg(List<String> codeList);

    /**
     * 翻译线路信息
     * @param lineCode
     * @return
     */
    String translateLine(String lineCode);
    /**
     * 翻译站点信息
     * @param stationCode
     * @return
     */
    String translateStation(String stationCode);
    /**
     * 翻译位置信息
     * @param positionCode
     * @return
     */
    String translatePosition(String positionCode);

    /**
     *翻译巡检人名称
     * @param code
     * @return
     */
    List<String> spliceUsername(String code);

    /**
     * 获取巡检人
     * @param taskCode
     * @return
     */
    List<String> getUser(String taskCode);
}
