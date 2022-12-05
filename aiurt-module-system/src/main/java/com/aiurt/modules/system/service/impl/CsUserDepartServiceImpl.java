package com.aiurt.modules.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.system.entity.CsUserDepart;
import com.aiurt.modules.system.entity.SysDepart;
import com.aiurt.modules.system.mapper.CsUserDepartMapper;
import com.aiurt.modules.system.mapper.SysDepartMapper;
import com.aiurt.modules.system.service.ICsUserDepartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 用户部门权限表
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class CsUserDepartServiceImpl extends ServiceImpl<CsUserDepartMapper, CsUserDepart> implements ICsUserDepartService {
    @Autowired
    private CsUserDepartMapper csUserDepartMapper;

    @Resource
    private SysDepartMapper departMapper;

    @Override
    public List<CsUserDepartModel> getDepartByUserId(String id) {
        List<CsUserDepartModel> departByUserId = csUserDepartMapper.getDepartByUserId(id);
        return departByUserId;
    }

    public List<CsUserDepartModel> queryDepartTree() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<CsUserDepartModel> list = getDepartByUserId(loginUser.getId());
        List<CsUserDepartModel> models = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>(16);
        if (CollUtil.isNotEmpty(list)) {
            for (CsUserDepartModel sysDepart : list) {
                sysDepart.setSelect(true);
                sysDepart.setId(sysDepart.getDepartId());
                models.add(sysDepart);
                map.put(sysDepart.getOrgCode(), sysDepart);
                String orgCodeCc = sysDepart.getOrgCodeCc();
                if (StrUtil.isNotBlank(orgCodeCc)) {
                    List<String> orgCodes = StrUtil.splitTrim(orgCodeCc,"/" );
                    for (String orgCode : orgCodes) {
                        CsUserDepartModel model = new CsUserDepartModel();
                        SysDepart depart = departMapper.queryDepartByOrgCode(orgCode);
                        Object o = map.get(depart.getOrgCode());
                        if (ObjectUtil.isEmpty(o)) {
                            BeanUtil.copyProperties(depart,model);
                            model.setSelect(false);
                            map.put(orgCode, depart);
                            models.add(model);
                        }

                    }
                }
            }
        }

        if (CollUtil.isNotEmpty(models)) {
            List<CsUserDepartModel> modelList = models.stream().filter(f -> StrUtil.isEmpty(f.getParentId())).collect(Collectors.toList());
            getChildrenDepartTree(modelList,models);
            return modelList;
        }
        return new ArrayList<>();

    }

   private void getChildrenDepartTree (List<CsUserDepartModel> modelList,List<CsUserDepartModel> models) {
       for (CsUserDepartModel csUserDepartModel : modelList) {
           List<CsUserDepartModel> childrenDeparts = models.stream().filter(f -> csUserDepartModel.getId().equals(f.getParentId())).collect(Collectors.toList());
           if (CollUtil.isNotEmpty(childrenDeparts)) {
               csUserDepartModel.setChildrenList(childrenDeparts);
               getChildrenDepartTree(childrenDeparts,models);
           }
       }
   }

}
