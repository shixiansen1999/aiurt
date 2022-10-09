package com.aiurt.modules.listener;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;

/**
 * 流向线监听事件
 */
public class SequenceflowTakenListener implements FlowableEventListener {

    @Override
    public void onEvent(FlowableEvent event) {

    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }
}
