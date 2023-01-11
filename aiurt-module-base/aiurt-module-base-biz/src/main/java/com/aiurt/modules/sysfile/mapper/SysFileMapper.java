package com.aiurt.modules.sysfile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.modules.sysfile.entity.SysFile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文档表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface SysFileMapper extends BaseMapper<SysFile> {

	IPage<SysFile> selectFilePage(Page page, @Param("typeId") List<Long> typeId, @Param("fileName") String fileName);
}
