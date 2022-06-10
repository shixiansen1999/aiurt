package com.aiurt.boot.modules.repairManage.vo;

import lombok.Data;

import java.util.List;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/28 20:43
 */
@Data
public class RepairRecordVO {
    private String repairTaskCode;
    private List<String> repairPoolContent;
}
