package com.aiurt.modules.sysfile.service;

import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.entity.SysFolderFilePermission;
import com.aiurt.modules.sysfile.param.SysFolderFilePermissionParam;
import com.aiurt.modules.sysfile.param.SysFolderParam;
import com.aiurt.modules.sysfile.vo.SysFolderDetailVO;
import com.aiurt.modules.sysfile.vo.SysFolderFilePermissionVO;
import com.aiurt.modules.sysfile.vo.SysFolderTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文件夹接口
 * @Author: wgp
 * @Date: 2023-05-22
 * @Version: V1.0
 */
public interface ISysFolderService extends IService<SysFileType> {

    /**
     * 添加文件夹
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要添加的系统文件夹参数信息
     */
    void addFolder(HttpServletRequest req, SysFolderParam param);

    /**
     * 查询文件夹树形结构列表
     *
     * @param name 文件夹名称，用于筛选符合名称的文件夹
     * @param pid  文件夹父级id，用于筛选符合id的文件夹
     * @return 查询结果，包含SysFolderTreeVO对象列表
     */
    List<SysFolderTreeVO> queryFolderTree(String name, Long pid);

    /**
     * 编辑文件夹
     *
     * @param req   HttpServletRequest对象，用于获取请求参数和会话信息
     * @param param SysFolderParam对象，包含要编辑的系统文件夹参数信息
     * @return Result对象，表示编辑操作的结果
     */
    void edit(HttpServletRequest req, SysFolderParam param);

    /**
     * 获取系统文件夹详情
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param id  系统文件夹的ID
     * @return SysFolderDetailVO对象，表示系统文件夹的详情信息
     */
    SysFolderDetailVO detail(HttpServletRequest req, Long id);

    /**
     * 删除系统文件夹
     *
     * @param req HttpServletRequest对象，用于获取请求参数和会话信息
     * @param ids 系统文件夹ID列表
     */
    void deleteFolder(HttpServletRequest req, List<Long> ids);

    /**
     * 获取文件夹的权限详情列表
     *
     * @param sysFolderFilePermissions 文件夹文件权限列表
     * @return 权限详情列表
     */
    List<SysFolderFilePermissionVO> getPermissionDetails(List<SysFolderFilePermission> sysFolderFilePermissions);

    /**
     * 根据文件夹ID集合获取文件的权限列表
     *
     * @param ids 文件夹ID集合
     * @return 文件夹权限列表，以文件夹ID为键，权限列表为值的映射
     */
    Map<Long, List<SysFolderFilePermission>> getPermissionByFolderId(List<Long> ids);

    /**
     * 构建文件夹的等级和编码和编码层级数据，兼容历史数据
     */
    void buildData();

    /**
     * 根据文件夹ID列表获取文件夹层级路径编码的映射关系。
     *
     * @param folderList 文件夹ID列表
     * @return 文件夹ID与层级路径编码的映射关系
     */
    Map<Long, String> getFolderCodeCcByFolderId(List<Long> folderList);

    /**
     * 校验 SysFolderFilePermissionParam 参数列表的有效性。
     * 参数列表不能为空，并且必须至少包含一个权限为 6(可管理权限) 的项，以及至少有一个非空的用户ID列表或部门列表。
     *
     * @param sysFolderFilePermissionParams SysFolderFilePermissionParam 参数列表
     */
    void validateSysFolderFilePermissionParams(List<SysFolderFilePermissionParam> sysFolderFilePermissionParams);

    /**
     * 重命名文件夹
     *
     * @param id   文件夹id
     * @param name 文件夹名称
     */
    void renameFolder(Long id, String name);

    /**
     * 移动文件夹或者文件
     *
     * @param fileId 移动文件id
     * @param fileTypeId  移动文件夹id
     * @param targetFileTypeId  移入文件夹id
     * @return 结果
     */
    void moveFile(Long fileId, Long fileTypeId, Long targetFileTypeId);
}

