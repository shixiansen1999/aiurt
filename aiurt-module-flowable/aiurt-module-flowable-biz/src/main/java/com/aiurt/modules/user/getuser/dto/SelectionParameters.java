package com.aiurt.modules.user.getuser.dto;

import com.aiurt.modules.user.entity.ActCustomUser;
import lombok.Data;
import org.flowable.engine.runtime.ProcessInstance;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fgw
 */
@Data
public class SelectionParameters implements Serializable {

    /**
     * 自定义用户
     */
    private ActCustomUser customUser;

    /**
     * 流程实例
     */
    private ProcessInstance processInstance;

    /***
     * 系统变量
     */
    private Map<String, Object> variable;
}
