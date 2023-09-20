package com.aiurt.modules.device.handler;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.entity.Column;
import com.aiurt.modules.handler.validator.ValidationResult;
import com.aiurt.modules.handler.validator.rule.RowValidationRule;
import com.aiurt.modules.material.entity.MaterialBase;
import com.aiurt.modules.material.mapper.MaterialBaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author:lkj
 * @create: 2023-07-03 11:50
 * @Description:
 */
@Component("DeviceAssemblyRowMaterialValidationRuleHandle")
public class DeviceAssemblyRowMaterialValidationRuleHandle implements RowValidationRule {
    @Resource
    private MaterialBaseMapper materialBaseMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public ValidationResult validate(Map<String, Column> row, Column column) {
        if (ObjectUtil.isEmpty(column.getData())) {
            return new ValidationResult(false, String.format("%s该字段值不能为空", column.getName()));
        }

        Object baseTypeName = getBaseTypeName(row);
        Object materialName = getMaterialName(row);

        QueryWrapper<MaterialBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", (String) column.getData());
        queryWrapper.like("name", materialName);
        queryWrapper.eq("del_flag", 0);
        MaterialBase one = materialBaseMapper.selectOne(queryWrapper);

        if (ObjectUtil.isEmpty(one)) {
            return new ValidationResult(false, String.format("%s系统中不存在该物资编码", column.getName()));
        }else {
            if (ObjectUtil.isNotEmpty(baseTypeName) && !one.getName().equals(materialName)) {
                return new ValidationResult(false, String.format("%s物资类型与物资不符合",row.get("base_type_code").getName()));
            }
            return new ValidationResult(true, null);
        }
    }

    /**
     * 获取物资类型名称。
     *
     * @param row 正在被验证的数据集中的行。
     * @return 物资类型名称。
     */
    private Object getBaseTypeName(Map<String, Column> row) {
        Column baseTypeName = row.get("base_type_code");
        if (baseTypeName != null) {
            return baseTypeName.getData();
        }
        return null;
    }

    /**
     * 获取组件名称。
     *
     * @param row 正在被验证的数据集中的行。
     * @return 组件名称。
     */
    private Object getMaterialName(Map<String, Column> row) {
        Column materialName = row.get("material_name");
        if (materialName != null) {
            return materialName.getData();
        }
        return null;
    }

    @Override
    public void setParams(Map<String, String> params) {

    }
}
