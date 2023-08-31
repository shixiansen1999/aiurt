package com.aiurt.modules.user.filters;

import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.pipeline.AbstractUserFilter;
import com.aiurt.modules.user.pipeline.UserFilterChain;
import org.springframework.stereotype.Service;

/**
 * @author fgw
 */
@Service
public class DefaultNullUserFilter extends AbstractUserFilter<SelectUserContext> {


    /**
     * 实际任务处理
     *
     * @param context
     * @param chain
     */
    @Override
    public void doFilter(SelectUserContext context, UserFilterChain chain) {
        super.doFilter(context, chain);
    }

    /**
     * @param context
     */
    @Override
    protected void handle(SelectUserContext context) {

    }
}
