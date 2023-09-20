package com.aiurt.modules.device.handler;

import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.IRowDataConvertHandler;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.mapper.CsStationMapper;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lkj
 */
@Component("DataConvertHandler")
public class DataConvertHandler implements IRowDataConvertHandler {

    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private CsStationMapper csStationMapper;

    @Override
    public String convert(Column value, Map<String, Column> row) {
        Column lineCodeColumn = row.get("line_code");
        if (lineCodeColumn != null) {
            Object lineName = lineCodeColumn.getData();
            Object stationName = getStationName(row);
            JSONObject lineByName = sysBaseApi.getLineByName((String) lineName);

            if (lineByName != null) {
                LambdaQueryWrapper<CsStation> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CsStation::getStationName, (String)stationName)
                        .eq(CsStation::getLineCode, lineByName.getString("lineCode"))
                        .eq(CsStation::getDelFlag, CommonConstant.DEL_FLAG_0);
                CsStation csStation = csStationMapper.selectOne(wrapper);

                String stationCode = "";

                if (csStation != null) {
                    stationCode = csStation.getStationCode();
                }

                if ("station_code".equals(value.getColumn())) {
                    value.setData(stationCode);
                }
                if ("position_code".equals(value.getColumn())) {
                    Object positionName = getPositionName(row);
                    JSONObject positionByName = sysBaseApi.getPositionByName((String)positionName, lineByName.getString("lineCode"), stationCode);
                    value.setData(positionByName.getString("positionCode"));
                }
            }
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

    private Object getPositionName(Map<String, Column> row) {
        Column stationCodeColumn = row.get("position_code");
        if (stationCodeColumn != null) {
            return stationCodeColumn.getData();
        }
        return null;
    }
}
