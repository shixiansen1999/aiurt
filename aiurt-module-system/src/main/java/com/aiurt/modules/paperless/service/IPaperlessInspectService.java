package com.aiurt.modules.paperless.service;

import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.aiurt.modules.paperless.entity.PaperlessInspect;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 安全检查记录
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface IPaperlessInspectService extends IService<PaperlessInspect> {

	/**
	 * 添加一对多
	 *
	 * @param paperlessInspect
	 * @param paperlessInspectEntryList
	 */
	public void saveMain(PaperlessInspect paperlessInspect,List<PaperlessInspectEntry> paperlessInspectEntryList) ;
	
	/**
	 * 修改一对多
	 *
   * @param paperlessInspect
   * @param paperlessInspectEntryList
	 */
	public void updateMain(PaperlessInspect paperlessInspect,List<PaperlessInspectEntry> paperlessInspectEntryList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
