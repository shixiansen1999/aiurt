package com.aiurt.modules.user.handler;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Service
public class BaseUserHandler extends AbstractFlowHandler<SelectUserContext> {

    @Autowired
    private ISysBaseAPI sysBaseApi;

    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {
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
