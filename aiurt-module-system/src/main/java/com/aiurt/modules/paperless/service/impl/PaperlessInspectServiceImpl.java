package com.aiurt.modules.paperless.service.impl;

import com.aiurt.modules.paperless.entity.PaperlessInspect;
import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.aiurt.modules.paperless.mapper.PaperlessInspectEntryMapper;
import com.aiurt.modules.paperless.mapper.PaperlessInspectMapper;
import com.aiurt.modules.paperless.service.IPaperlessInspectService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 安全检查记录
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
@Service
public class PaperlessInspectServiceImpl extends ServiceImpl<PaperlessInspectMapper, PaperlessInspect> implements IPaperlessInspectService {

	@Autowired
	private PaperlessInspectMapper paperlessInspectMapper;
	@Autowired
	private PaperlessInspectEntryMapper paperlessInspectEntryMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(PaperlessInspect paperlessInspect, List<PaperlessInspectEntry> paperlessInspectEntryList) {
		paperlessInspectMapper.insert(paperlessInspect);
		if(paperlessInspectEntryList!=null && paperlessInspectEntryList.size()>0) {
			for(PaperlessInspectEntry entity:paperlessInspectEntryList) {
				//外键设置
				entity.setPaperlessId(paperlessInspect.getId());
				paperlessInspectEntryMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(PaperlessInspect paperlessInspect,List<PaperlessInspectEntry> paperlessInspectEntryList) {
		paperlessInspectMapper.updateById(paperlessInspect);
		
		//1.先删除子表数据
		paperlessInspectEntryMapper.deleteByMainId(paperlessInspect.getId());
		
		//2.子表数据重新插入
		if(paperlessInspectEntryList!=null && paperlessInspectEntryList.size()>0) {
			for(PaperlessInspectEntry entity:paperlessInspectEntryList) {
				//外键设置
				entity.setPaperlessId(paperlessInspect.getId());
				paperlessInspectEntryMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		paperlessInspectEntryMapper.deleteByMainId(id);
		paperlessInspectMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			paperlessInspectEntryMapper.deleteByMainId(id.toString());
			paperlessInspectMapper.deleteById(id);
		}
	}
	
}
