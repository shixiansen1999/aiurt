package com.aiurt.modules.paperless.service.impl;

import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.aiurt.modules.paperless.mapper.PaperlessInspectEntryMapper;
import com.aiurt.modules.paperless.service.IPaperlessInspectEntryService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 安全检查记录从表
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Service
public class PaperlessInspectEntryServiceImpl extends ServiceImpl<PaperlessInspectEntryMapper, PaperlessInspectEntry> implements IPaperlessInspectEntryService {
	
	@Autowired
	private PaperlessInspectEntryMapper paperlessInspectEntryMapper;
	
	@Override
	public List<PaperlessInspectEntry> selectByMainId(String mainId) {
		return paperlessInspectEntryMapper.selectByMainId(mainId);
	}
}
