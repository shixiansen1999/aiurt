package com.aiurt.modules.system.vo;

import com.aiurt.modules.system.entity.SysUser;
import lombok.Data;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.system.vo
 * @className: DepartAndUserTree
 * @author: life-0
 * @date: 2022/8/10 16:56
 * @description: TODO
 * @version: 1.0
 */
@Data
public class DepartAndUserTree {
    String id;
    String parentId;
    String departName;
    List<SysUser> users;
    List<DepartAndUserTree> children;

}
