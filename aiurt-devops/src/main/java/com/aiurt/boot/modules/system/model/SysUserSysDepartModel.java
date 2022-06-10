package com.aiurt.boot.modules.system.model;

import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysUser;
import lombok.Data;

/**
 * 包含 SysUser 和 SysDepart 的 Model
 *
 * @author sunjianlei
 */
@Data
public class SysUserSysDepartModel {

    private SysUser sysUser;
    private SysDepart sysDepart;

}
