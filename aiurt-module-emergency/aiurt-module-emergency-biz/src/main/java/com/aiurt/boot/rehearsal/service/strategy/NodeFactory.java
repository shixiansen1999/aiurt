package com.aiurt.boot.rehearsal.service.strategy;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.rehearsal.service.strategy.node.FlowPass;
import com.aiurt.boot.rehearsal.service.strategy.node.ProgramDirectorsApprove;
import com.aiurt.boot.rehearsal.service.strategy.node.ProgramDirectorsReject;
import com.aiurt.common.exception.AiurtBootException;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author CJB
 * @Description: 节点工厂类
 */
public class NodeFactory {

    private static Map<Integer, NodeAudit> NODE_MAP = new HashMap<>(16);

    static {
        NODE_MAP.put(Node.PROGRAM_DIRECTORS_APPROVE, new ProgramDirectorsApprove());
        NODE_MAP.put(Node.PROGRAM_DIRECTORS_REJECT, new ProgramDirectorsReject());
        NODE_MAP.put(Node.FLOW_PASS, new FlowPass());
    }

    private NodeFactory() {
    }

    public static NodeAudit getNode(Integer node) {
        NodeAudit nodeAudit = NODE_MAP.get(node);
        if (ObjectUtil.isEmpty(nodeAudit)) {
            throw new AiurtBootException("未发现该流程节点！");
        }
        return nodeAudit;
    }

    private interface Node {
        /**
         * 演练计划负责人审批
         */
        Integer PROGRAM_DIRECTORS_APPROVE = 2;
        /**
         * 演练计划负责人驳回
         */
        Integer PROGRAM_DIRECTORS_REJECT = 3;
        /**
         * 流程已通过
         */
        Integer FLOW_PASS = 4;
    }
}
