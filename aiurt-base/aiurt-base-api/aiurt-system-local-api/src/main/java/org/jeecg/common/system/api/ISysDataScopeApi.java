package org.jeecg.common.system.api;

/**
 * @author wgp
 * @Title:
 * @Description: 数据权限通用接口
 * @date 2023/2/69:37
 */
public interface ISysDataScopeApi {
    /**
     * 16 根据用户id获取部门权限
     * @param id
     * @return
     */
    String getDepartByUserIdStr(String id);

    /**
     * 17 根据用户id获取专业权限
     * @param id
     * @return
     */
    String getMajorByUserIdStr(String id);

    /**
     * 18 根据用户id获取站点
     * @param id
     * @return
     */
    String getStationByUserIdStr(String id);

    /**
     * 19 根据用户id获取子系统
     * @param id
     * @return
     */
    String getSubsystemByUserIdStr(String id);
}
