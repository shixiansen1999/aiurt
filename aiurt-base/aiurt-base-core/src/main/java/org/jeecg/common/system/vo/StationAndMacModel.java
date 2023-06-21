package org.jeecg.common.system.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author LKJ
 */
@Data
public class StationAndMacModel {
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    @ApiModelProperty(value = "站点mac地址")
    private String mac;

    /**
     * 重写hashCode方法
     * @return
     */
    @Override
    public int hashCode(){
        return mac.hashCode();
    }

    /**
     * 重写equals方法
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o){
        if (o instanceof StationAndMacModel ){
            StationAndMacModel stationAndMacModel = (StationAndMacModel) o ;
            return this.getMac().equals(stationAndMacModel.getMac());
        }
        return false;
    }
}
