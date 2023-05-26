package com.aiurt.modules.sysfile.mapper;

import com.aiurt.modules.sysfile.entity.SysFile;
import com.aiurt.modules.sysfile.vo.FileAppVO;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.param.SysFileWebParam;
import com.aiurt.modules.sysfile.vo.SysFileManageVO;
import com.aiurt.modules.sysfile.vo.TypeNameVO;
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

    /**
     * 根据条件查询文件分页列表
     *
     * @param page             分页对象，用于返回分页结果
     * @param sysFileWebParam  文件查询参数对象，包含查询条件
     * @param currLoginUserId  当前登录用户ID
     * @param currLoginOrgCode 当前登录用户所属组织机构代码
     * @param userNames        用户账号集合
     * @return 文件分页列表
     */
    List<SysFileManageVO> getFilePageList(@Param("page") Page<SysFileManageVO> page, @Param("sysFileWebParam") SysFileWebParam sysFileWebParam, @Param("currLoginUserId") String currLoginUserId, @Param("currLoginOrgCode") String currLoginOrgCode, @Param("userNames") List<String> userNames);

    /**
     * 根据文件夹编码查询所有子级文件夹下的文件的文件的类型
     *
     * @param folderCodeCc     文件夹编码层级
     * @param currLoginUserId  用户id
     * @param currLoginOrgCode 用户部门code
     * @return
     */
    List<TypeNameVO> queryTypeByFolderCode(@Param("folderCodeCc") String folderCodeCc, @Param("currLoginUserId") String currLoginUserId, @Param("currLoginOrgCode") String currLoginOrgCode);

    /**
     * app查询父节点
     *
     * @param page     分页对象
     * @param fileName 文件名
     * @param username 用户名
     * @param orgCode  组织机构编码
     * @return 文件应用的分页列表
     */
    Page<FileAppVO> listPrent(Page<FileAppVO> page, @Param("fileName") String fileName, @Param("username") String username, @Param("orgCode") String orgCode);

    /**
     * 查询当前节点下有权限的文件和文件夹
     *
     * @param page     分页对象
     * @param parentId 父级ID
     * @param fileName 文件名
     * @param username 用户名
     * @param orgCode  组织机构编码
     * @return 文件应用的分页列表
     */
    Page<FileAppVO> listPage(Page<FileAppVO> page, @Param("parentId") Long parentId, @Param("fileName") String fileName, @Param("username") String username, @Param("orgCode") String orgCode);
}
