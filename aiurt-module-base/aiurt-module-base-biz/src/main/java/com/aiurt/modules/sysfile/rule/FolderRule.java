package com.aiurt.modules.sysfile.rule;

import com.aiurt.common.handler.IFillRuleHandler;
import com.aiurt.common.util.YouBianCodeUtil;
import com.aiurt.modules.sysfile.constant.SysFileConstant;
import com.aiurt.modules.sysfile.entity.SysFileType;
import com.aiurt.modules.sysfile.service.ISysFolderService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.netty.util.internal.StringUtil;
import org.jeecg.common.util.SpringContextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author wgp
 * @Date 2023/05/29 11:33
 * @Description: 文件夹编码生成规则
 */
public class FolderRule implements IFillRuleHandler {

    @Override
    public Object execute(JSONObject params, JSONObject formData) {
        ISysFolderService sysFolderService = (ISysFolderService) SpringContextUtils.getBean("sysFolderServiceImpl");

        LambdaQueryWrapper<SysFileType> query = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<SysFileType> query1 = new LambdaQueryWrapper<>();

        // 创建一个List集合,存储查询返回的所有sysFileTypes对象
        List<SysFileType> sysFileTypes = new ArrayList<>();
        String[] strArray = new String[1];

        // 定义新编码字符串
        String newOrgCode = "";

        // 定义旧编码字符串
        String oldOrgCode = "";

        Long parentId = null;
        if (formData != null && formData.size() > 0) {
            Object obj = formData.get("parentId");
            if (obj != null) {
                parentId = Long.valueOf(obj.toString());
            }
        } else {
            if (params != null) {
                Object obj = params.get("parentId");
                if (obj != null) {
                    parentId = Long.valueOf(obj.toString());
                }
            }
        }

        // 如果是最高级,则查询出同级的code, 调用工具类生成编码并返回
        if (SysFileConstant.NUM_LONG_0.equals(parentId)) {
            // 判断数据库中的表是否为空,空则直接返回初始编码
            query1.eq(SysFileType::getParentId, "")
                    .or()
                    .isNull(SysFileType::getParentId);
            query1.orderByDesc(SysFileType::getFolderCode);
            sysFileTypes = sysFolderService.list(query1);

            if (sysFileTypes == null || sysFileTypes.size() == 0) {
                strArray[0] = YouBianCodeUtil.getNextYouBianCode(null);
                return strArray;
            } else {
                SysFileType sysFileType = sysFileTypes.get(0);
                oldOrgCode = sysFileType.getFolderCode();
                newOrgCode = YouBianCodeUtil.getNextYouBianCode(oldOrgCode);
            }
        } else {
            // 反之则查询出所有同级的文件夹,获取结果后有两种情况,有同级和没有同级
            // 封装查询文件夹的条件
            query.eq(SysFileType::getParentId, parentId);
            // 降序排序
            query.orderByDesc(SysFileType::getFolderCode);
            // 查询出同级文件夹的集合
            List<SysFileType> parentList = sysFolderService.list(query);
            // 查询出父级文件夹
            SysFileType fileType = sysFolderService.getById(parentId);
            // 获取父级部门的Code
            String parentCode = fileType.getFolderCode();

            // 处理同级文件夹为null的情况
            if (parentList == null || parentList.size() == 0) {
                // 直接生成当前的文件夹编码并返回
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, null);
            } else {
                // 处理有同级文件夹的情况
                // 获取同级文件夹的编码,利用工具类
                String subCode = parentList.get(0).getFolderCode();
                // 返回生成的当前文件夹编码
                newOrgCode = YouBianCodeUtil.getSubYouBianCode(parentCode, subCode);
            }
        }
        // 返回最终封装了文件夹编码
        strArray[0] = newOrgCode;
        return strArray;
    }
}
