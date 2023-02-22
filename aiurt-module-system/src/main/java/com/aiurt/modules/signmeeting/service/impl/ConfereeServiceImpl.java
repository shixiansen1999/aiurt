package com.aiurt.modules.signmeeting.service.impl;

import com.aiurt.modules.signmeeting.entity.Conferee;
import com.aiurt.modules.signmeeting.mapper.ConfereeMapper;
import com.aiurt.modules.signmeeting.service.IConfereeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 参会人员
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Service
public class ConfereeServiceImpl extends ServiceImpl<ConfereeMapper, Conferee> implements IConfereeService {
	
	@Autowired
	private ConfereeMapper confereeMapper;
	
	@Override
	public List<Conferee> selectByMainId(String mainId) {
		return confereeMapper.selectByMainId(mainId);
	}
}
