package com.aiurt.modules.sysFile.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.sysFile.entity.DefaultUser;
import com.aiurt.modules.sysFile.mapper.DefaultUserMapper;
import com.aiurt.modules.sysFile.service.DefaultUserService;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @description: DefaultUserServiceImpl
 * @author: Mr.zhao
 * @date: 2021/11/22 14:50
 */

@Service
public class DefaultUserServiceImpl extends ServiceImpl<DefaultUserMapper, DefaultUser> implements DefaultUserService{

	@Override
	public List<DefaultUser> listDefault(String userId) {

		List<DefaultUser> list = this.baseMapper.listDefaultUser(userId);

		return list;
	}
}
