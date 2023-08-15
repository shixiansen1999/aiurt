package com.aiurt.modules.multideal.dto;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.flow.dto.NextNodeUserDTO;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private Map<String, Object> businessData;
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


    public MultiDealDTO(FlowCompleteReqDTO flowCompleteReqDTO){
        this.taskId = flowCompleteReqDTO.getTaskId();
        this.businessData = flowCompleteReqDTO.getBusData();
        List<NextNodeUserDTO> nextNodeUserParams = flowCompleteReqDTO.getNextNodeUserParam();
        if (CollUtil.isNotEmpty(nextNodeUserParams)){
            List<NextNodeUserVo> userVoList = nextNodeUserParams.stream().map(nextNodeUserDTO -> {
                NextNodeUserVo nextNodeUserVo = new NextNodeUserVo();
                nextNodeUserVo.setNextNodeId(nextNodeUserDTO.getNodeId());
                nextNodeUserVo.setUser(nextNodeUserDTO.getApprover());
                return nextNodeUserVo;
            }).collect(Collectors.toList());
            this.setNextNodeUser(userVoList);
        }
    }
}
