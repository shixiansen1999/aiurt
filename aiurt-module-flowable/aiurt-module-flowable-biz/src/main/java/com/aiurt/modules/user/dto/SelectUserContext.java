package com.aiurt.modules.user.dto;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.pipeline.context.AbstractUserContext;
import com.aiurt.modules.common.pipeline.selector.FilterSelector;
import lombok.Getter;
import lombok.Setter;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @author fgw
 */
@Setter
@Getter
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

    private Boolean continueChain;


    public SelectUserContext(FilterSelector selector) {
        super(selector);
    }


    @Override
    public boolean continueChain() {
        if (Objects.nonNull(this.continueChain)) {
            return this.continueChain;
        }
        return true;
    }

    public void  addUserList(List<String> userList) {
        if (Objects.isNull(this.userList)) {
            this.userList = new ArrayList<>();
        }
        this.userList.addAll(userList);
    }
}
