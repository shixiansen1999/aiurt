package com.aiurt.modules.user.dto;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.pipeline.context.AbstractUserContext;
import com.aiurt.modules.user.pipeline.selector.FilterSelector;
import lombok.Data;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author fgw
 */
@Data
public class SelectUserContext extends AbstractUserContext {

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

    /**
     * 结果
     */
    private List<String> userList;


    public SelectUserContext(FilterSelector selector) {
        super(selector);
    }


    @Override
    public boolean continueChain() {
        return true;
    }

    public void  addUserList(List<String> userList) {
        if (Objects.isNull(userList)) {
            this.userList = new ArrayList<>();
        }
        this.userList.addAll(userList);
    }
}
