package com.aiurt.modules.user.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.common.pipeline.FlowHandlerChain;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.enums.EmptyRuleEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
@Service
public class DefaultNullUserHandler extends AbstractFlowHandler<SelectUserContext> {


    /**
     * 实际任务处理
     *
     * @param context
     * @param chain
     */
    @Override
    public void doHandle(SelectUserContext context, FlowHandlerChain chain) {
        if (context.getHandlerSelector().matchHandler(this.getClass().getSimpleName())) {
            // 审批人为空
            if (CollUtil.isEmpty(context.getUserList())) {
                // 业务处理
                handle(context);
            }
        }

        if (context.continueChain()) {
            // 执行下一个过滤器
            chain.fireNext(context);
        }
    }

    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {
        ActCustomUser customUser = context.getCustomUser();

        if (Objects.isNull(customUser)) {
            return;
        }

        String emptyRule = customUser.getEmptyRule();
        if (StrUtil.isBlank(emptyRule)) {
            emptyRule = EmptyRuleEnum.AUTO_ADMIN.getCode();
        }

        String emptyUserName = customUser.getEmptyUserName();

        EmptyRuleEnum emptyRuleEnum = EmptyRuleEnum.getByCode(emptyRule);
        if (Objects.isNull(emptyRuleEnum)) {
            return;
        }
        List<String> userNameList = new ArrayList<>();
        switch (emptyRuleEnum) {
            case POINT_USER_NAME:
                if (StrUtil.isNotBlank(emptyUserName)) {
                    List<String> list = StrUtil.split(emptyUserName, ',');
                    userNameList.add(list.get(0));
                }
                break;
            default:
                userNameList.add(emptyRuleEnum.getMessage());
                break;
        }
        context.setUserList(userNameList);
    }
}
