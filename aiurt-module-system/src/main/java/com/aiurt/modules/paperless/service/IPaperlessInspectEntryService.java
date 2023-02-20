package com.aiurt.modules.paperless.service;

import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 安全检查记录从表
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface IPaperlessInspectEntryService extends IService<PaperlessInspectEntry> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<PaperlessInspectEntry>
	 */
	public List<PaperlessInspectEntry> selectByMainId(String mainId);
}
