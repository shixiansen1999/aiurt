package com.aiurt.boot.monthlyplan.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description: TODO
 * @author: Sand Sculpture King
 * @date: 2021年05月27日 11:17
 */
@Data
@ApiModel("线路信息DTO")
public class BdLineInfoDTO {
    private String id;
    private String lineName;
}
