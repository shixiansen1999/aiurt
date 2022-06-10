package com.aiurt.boot.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.system.entity.SysRole;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @Author swsc
 * @since 2018-12-19
 */
public interface ISysRoleService extends IService<SysRole> {

	/**
	 * 导入 excel ，检查 roleCode 的唯一性
	 *
	 * @param file
	 * @param params
	 * @return
	 * @throws Exception
	 */
	Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception;


	/**
	 * 获取角色鉴权code 可供RoleConstant中字段判断
	 *
	 * @return {@link String}
	 * @Author: Mr.zhao
	 */
	String getRoleCode();

}
