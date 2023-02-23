package com.aiurt.boot.weeklyplan.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LKJ
 */
@Data
public class BdOperatePlanDeclarationFormMessageDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    private Integer afterStatus;

    /**是否是总线路负责人，true是false否*/
    private  Boolean lineAllPeople;

    /**
     * 业务类型
     */
    private String busType;

}
