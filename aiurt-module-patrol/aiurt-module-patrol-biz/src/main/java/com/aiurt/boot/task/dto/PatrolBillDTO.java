package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "巡检单联动对象", description = "巡检单联动对象")
public class PatrolBillDTO {
    /**
     * 巡检单ID
     */
    @ApiModelProperty(value = "巡检单ID")
    private String billId;
    /**
     * 巡检单编号
     */
    @ApiModelProperty(value = "巡检单编号")
    private String billCode;
    /**
     * 工单表名
     */
    @ApiModelProperty(value = "工单表名")
    private String tableName;
    /**
     * 站点编码
     */
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String stationName;
}
//SELECT
//	*
//FROM
//	(
//	SELECT
//		ptd.id billId,
//		ptd.patrol_number billCode,
//		CONCAT( ps.`name`, IF ( d.`name` IS NOT NULL, CONCAT( '(', d.`name`, ')' ), '' ) ) tableName,
//	IF
//		( ptd.device_code IS NOT NULL, d.station_code, ptd.station_code ) stationCode,
//		cs.station_name
//	FROM
//		patrol_task_device ptd
//		LEFT JOIN device d ON ptd.device_code = d.`code`
//		AND d.del_flag = 0
//		LEFT JOIN cs_station cs ON
//	IF
//		( ptd.device_code IS NOT NULL, d.station_code, ptd.station_code ) = cs.station_code
//		AND cs.del_flag = 0
//		JOIN patrol_task_standard pts ON ptd.task_standard_id = pts.id
//		AND pts.del_flag = 0
//		JOIN patrol_standard ps ON pts.standard_id = ps.id
//		AND ps.del_flag = 0
//	WHERE
//		ptd.del_flag = 0
//	) bill
//WHERE
//	1 = 1
//-- 	AND stationCode = '3line-01'
