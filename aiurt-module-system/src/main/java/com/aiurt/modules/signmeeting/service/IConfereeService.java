package com.aiurt.modules.signmeeting.service;

import com.aiurt.modules.signmeeting.entity.Conferee;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 参会人员
 * @Author: jeecg-boot
 * @Date:   2023-02-13
 * @Version: V1.0
 */
public interface IConfereeService extends IService<Conferee> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<Conferee>
	 */
	public List<Conferee> selectByMainId(String mainId);
}
