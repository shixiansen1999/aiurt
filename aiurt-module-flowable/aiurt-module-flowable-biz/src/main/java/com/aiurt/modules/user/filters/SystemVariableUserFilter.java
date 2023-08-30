package com.aiurt.modules.user.filters;

import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.pipeline.AbstractUserFilter;
import org.springframework.stereotype.Service;

/**
 *  系统变量
 * @author fgw
 */
@Service
public class SystemVariableUserFilter extends AbstractUserFilter<SelectUserContext> {
    /**
     * @param context
     */
    @Override
    protected void handle(SelectUserContext context) {
        // 工厂 + 策略模型
    }
}
