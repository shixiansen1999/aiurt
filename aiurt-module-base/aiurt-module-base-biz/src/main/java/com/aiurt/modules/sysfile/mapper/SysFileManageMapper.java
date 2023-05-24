package com.aiurt.modules.sysfile.mapper;

import com.aiurt.modules.sysfile.entity.SysFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文档表Mapper 接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface SysFileManageMapper extends BaseMapper<SysFile> {

    IPage<SysFile> selectFilePage(Page page, @Param("typeId") List<Long> typeId, @Param("fileName") String fileName);
}
