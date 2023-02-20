package com.aiurt.modules.signmeeting.mapper;

import java.util.List;
import com.aiurt.modules.signmeeting.entity.Conferee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 参会人员
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface ConfereeMapper extends BaseMapper<Conferee> {

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
   * @return List<Conferee>
   */
	public List<Conferee> selectByMainId(@Param("mainId") String mainId);
}
