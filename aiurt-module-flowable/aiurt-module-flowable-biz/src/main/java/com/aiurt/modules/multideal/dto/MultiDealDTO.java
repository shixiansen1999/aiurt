package com.aiurt.modules.multideal.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class MultiDealDTO implements Serializable {

    /**
     * 活动ID
     */
    private String taskId;
    /**
     * 中间业务数据
     */
    private JSONObject businessData;
    /**
     * 下一步节点及办理人
     */
    private List<NextNodeUserVo> nextNodeUser;


    @Data
    public class NextNodeUserVo {
        /**
         * 下一步节点
         */
        private String nextNodeId;

        /**
         * 下一步节点用户
         */
        private List<String> user;
    }
}
