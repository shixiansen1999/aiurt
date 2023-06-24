package com.aiurt.modules.personnelportrait.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description 人员排名和得分DTO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户账号
     */
    private String username;
    /**
     * 用户分数
     */
    private Double score;
    /**
     * 用户排名
     */
    private Integer rank;
}
