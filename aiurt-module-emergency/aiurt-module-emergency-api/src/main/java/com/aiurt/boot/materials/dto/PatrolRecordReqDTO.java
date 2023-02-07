package com.aiurt.boot.materials.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
@ApiModel("")
public class PatrolRecordReqDTO implements Serializable {

    private static final long serialVersionUID = 2199168375892692805L;

    @ApiModelProperty(value = "物资id", required = true)
    private String  id;

    @ApiModelProperty(value = "巡视标准编码", required = false)
    private String  standardCode;

    private String  startTime;

    private String  endTime;

    private Integer pageNo;

    private Integer pageSize;



    private String materialsCode;

    private String lineCode;

    private String stationCode;

    private String  positionCode;
}
