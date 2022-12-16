package com.aiurt.modules.maplocation.dto;

import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description: 离人员位置最近的站点DTO
 * @date 2021/7/2217:18
 */
@Data
public class UserStationDTO {
    // 人员的横坐标
    Double positionX;
    // 人员的纵坐标
    Double positionY;

    String bssid;
}
