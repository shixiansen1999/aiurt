package com.aiurt.modules.forecast.dto;

import lombok.Data;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class HistoricTaskInfo {



    /**
     * 历史
     */
    private List<HistoricTaskInstance> list = new ArrayList<>();

    /**
     * 允许
     */
    private Boolean addFlag = true;

    /**
     *
     */
    private List<String> nextNodeList;


    public void addTaskInstance(HistoricTaskInstance historicTaskInstance) {
        this.list.add(historicTaskInstance);
    }

}
