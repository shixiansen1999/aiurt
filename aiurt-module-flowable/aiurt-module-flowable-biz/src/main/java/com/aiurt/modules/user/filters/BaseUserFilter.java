package com.aiurt.modules.user.filters;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.pipeline.AbstractUserFilter;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Component
public class BaseUserFilter extends AbstractUserFilter<SelectUserContext> {

    @Autowired
    private ISysBaseAPI sysBaseApi;

    /**
     * @param context
     */
    @Override
    protected void handle(SelectUserContext context) {
        ActCustomUser customUser = context.getCustomUser();
        String userName = customUser.getUserName();
        String post = customUser.getPost();
        String orgId = customUser.getOrgId();
        String roleCode = customUser.getRoleCode();

        List<String> resultList = new ArrayList<>();
        List<String> list = StrUtil.split(userName, ',');
        List<String>  userNameList = sysBaseApi.getUserNameByParams(StrUtil.split(roleCode, ','),
                StrUtil.split(orgId, ','), StrUtil.split(post, ','));
        resultList.addAll(list);
        resultList.addAll(userNameList);
        resultList = resultList.stream().distinct().collect(Collectors.toList());
        context.addUserList(resultList);
    }
}
