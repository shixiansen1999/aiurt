package com.aiurt.modules.sysfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.modules.sysfile.entity.SysFileType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: 文档类型表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface SysFileTypeMapper extends BaseMapper<SysFileType> {

	@Select("select parent_id from sys_file_type where del_flag = 0 and id = #{typeId}")
	Long selectParentId(@Param("typeId") Long typeId);
}
