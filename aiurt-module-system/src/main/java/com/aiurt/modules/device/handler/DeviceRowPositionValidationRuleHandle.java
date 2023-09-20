package com.aiurt.modules.device.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-03 11:50
 * @Description:
 */
@Component("DeviceRowPositionValidationRuleHandle")
public class DeviceRowPositionValidationRuleHandle  implements RowValidationRule {

    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private CsStationMapper csStationMapper;
    /**
     * 针对特定的行和列验证是否被指定。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     * <p>
     * 此方法首先检查设备位置数据是否为空。如果为空，它将返回带有错误消息的 ValidationResult。
     * 如果设备位置数据不为空，它将继续使用 `validateMajorAndSubsystem` 方法来验证线路，站点和位置。
     */
    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        if (ObjectUtil.isEmpty(column.getData())) {
            return new ValidationResult(false, String.format("%s该字段值不能为空", column.getName()));
        }
        return validateLineAndStation(row, column);
    }

    /**
     * 验证线路，站点和位置。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult validateLineAndStation(Map<String, Column> row, Column column) {
        Column lineCodeColumn = row.get("line_code");
        if (lineCodeColumn != null) {
            return checkLineAndStation(lineCodeColumn, row, column);
        }
        return new ValidationResult(true, null);
    }

    /**
     * 验证线路，站点和位置的数据是否正确。
     *
     * @param row    正在被验证的数据集中的行。
     * @param column 正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult checkLineAndStation(Column lineCodeColumn, Map<String, Column> row, Column column) {
        Object lineName = lineCodeColumn.getData();
        Object stationName = getStationName(row);
        JSONObject lineByName = sysBaseApi.getLineByName((String) lineName);

        if (lineByName != null) {
            return validateDeviceStation(lineByName, stationName,row, column);
        }
        return new ValidationResult(true, null);
    }

    /**
     * 验证站点是否匹配线路,位置是否匹配站点线路。
     *
     * @param lineByName 线路。
     * @param stationName 站点名称。
     * @param column        正在被验证的数据集中的列。
     * @return ValidationResult，包含验证状态和错误信息（如果有）。
     */
    private ValidationResult validateDeviceStation(JSONObject lineByName, Object stationName,Map<String, Column> row, Column column) {
        LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CsStation::getStationName, stationName)
                .eq(CsStation::getLineCode, lineByName.getString("lineCode"))
                .eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
        CsStation csStation = csStationMapper.selectOne(wrapper);

        String stationCode = "";
        if (csStation != null) {
            stationCode = csStation.getStationCode();
        }else {
            if ("station_code".equals(column.getColumn())) {
                return new ValidationResult(false, String.format("系统不存在该线路下的站点"));
            }
        }

        if ("position_code".equals(column.getColumn())) {
            Object positionName = getPositionName(row);
            JSONObject positionByName = sysBaseApi.getPositionByName((String)positionName, lineByName.getString("lineCode"), stationCode);
            if (ObjectUtil.isEmpty(positionByName)) {
                return new ValidationResult(false, String.format("系统不存在该站点下的位置"));
            }
            return new ValidationResult(true, null);
        }
        return new ValidationResult(true, null);
    }

    private Object getPositionName(Map<String, Column> row) {
        Column stationCodeColumn = row.get("position_code");
        if (stationCodeColumn != null) {
            return stationCodeColumn.getData();
        }
        return null;
    }

    /**
     * 获取站点的名称。
     *
     * @param row 正在被验证的数据集中的行。
     * @return 子系统名称。
     */
    private Object getStationName(Map<String, Column> row) {
        Column stationCodeColumn = row.get("station_code");
        if (stationCodeColumn != null) {
            return stationCodeColumn.getData();
        }
        return null;
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
