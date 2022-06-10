package com.aiurt.boot.modules.fastdfs.mapper;

import com.swsc.copsms.modules.fastdfs.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: 附件表
 * @Author: swsc
 * @Date:   2020-10-23
 * @Version: V1.0
 */
@Mapper
@Repository
public interface FileInfoMapper extends BaseMapper<FileInfo> {

}
