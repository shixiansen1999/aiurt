package com.aiurt.modules.user.service.impl;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.mapper.ActCustomUserMapper;
import com.aiurt.modules.user.service.IActCustomUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 流程办理人与抄送人
 * @Author: aiurt
 * @Date:   2023-07-25
 * @Version: V1.0
 */
@Service
public class ActCustomUserServiceImpl extends ServiceImpl<ActCustomUserMapper, ActCustomUser> implements IActCustomUserService {

}
