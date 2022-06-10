package com.aiurt.boot.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.system.entity.SysDictItem;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItem> {
	public List<SysDictItem> selectItemsByMainId(String mainId);

	/**
	 * 通过dict_code查询所有item项
	 *
	 * @param code 代码
	 * @return {@code List<SysDictItem>}
	 */
	List<SysDictItem> selectByDictCode(String code);
}
