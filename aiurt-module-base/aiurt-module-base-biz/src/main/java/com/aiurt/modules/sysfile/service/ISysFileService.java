package com.aiurt.modules.sysfile.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.param.FileAppParam;
import com.aiurt.modules.sysfile.vo.FIlePlanVO;
import com.aiurt.modules.sysfile.vo.FileAppVO;

import java.util.List;

/**
 * @Description: 文档表
 * @Author: swsc
 * @Date: 2021-10-26
 * @Version: V1.0
 */
public interface ISysFileService extends IService<SysFile> {

	IPage<FileAppVO> selectAppList(FileAppParam param);

	List<FIlePlanVO> selectList();

}
