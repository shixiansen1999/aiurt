package com.aiurt.modules.sysfile.mapper;

import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.vo.SysFolderTreeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文件夹与文件的权限表 Mapper 接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface SysFolderMapper extends BaseMapper<SysFileType> {


    /**
     * 根据名称和父级ID、用户id、部门编码查询文件夹树形结构
     *
     * @param name     文件夹名称（可选，用于过滤匹配名称的文件夹）
     * @param pid 父级ID（可选，用于指定父级文件夹）
     * @param userId   用户id
     * @param orgCode  部门编码
     * @return 文件夹树形结构列表
     */
    List<SysFolderTreeVO> queryFolderTree(@Param("name") String name, @Param("pid") Long pid, @Param("userId") String userId, @Param("orgCode") String orgCode);
}
