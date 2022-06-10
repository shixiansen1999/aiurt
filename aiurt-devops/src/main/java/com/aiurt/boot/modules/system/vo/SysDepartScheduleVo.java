package com.aiurt.boot.modules.system.vo;

import com.aiurt.boot.modules.system.model.DepartScheduleModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: niuzeyu
 * @date: 2022年01月18日 16:41
 */
@Data
public class SysDepartScheduleVo implements Serializable {
    /**
     * 总班组数
     */
    private Integer departNum;
    /**
     * 今日当班人数
     */
    private Integer dutyUserNum;
    /**
     * 总人员数
     */
    private Integer userNum;

    List<DepartScheduleModel> departScheduleModelList;

}
