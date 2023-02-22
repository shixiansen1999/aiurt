package com.aiurt.modules.paperless.mapper;

import java.util.List;
import com.aiurt.modules.paperless.entity.PaperlessInspectEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 安全检查记录从表
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface PaperlessInspectEntryMapper extends BaseMapper<PaperlessInspectEntry> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<PaperlessInspectEntry>
   */
	public List<PaperlessInspectEntry> selectByMainId(@Param("mainId") String mainId);
}
