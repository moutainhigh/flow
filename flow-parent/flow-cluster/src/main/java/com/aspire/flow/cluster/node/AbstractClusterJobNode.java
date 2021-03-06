
package com.aspire.flow.cluster.node;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;

import com.aspire.flow.cluster.startup.BootStrap;
import com.aspire.flow.helper.AssertHelper;
import com.aspire.flow.helper.LoggerHelper;
import com.aspire.flow.scanner.node.AbstractNode;
import com.aspire.flow.scanner.node.Node;
import com.aspire.flow.scheduler.DefaultManualScheduleManager;
import com.aspire.flow.scheduler.ManualScheduleManager;

/**
 * 集群Job节点抽象类,封装了节点状态的切换,子类只需要实现自己加入和退出集群的方法.
 *
 * @author chenhaitao
 */
public abstract class AbstractClusterJobNode extends AbstractNode implements Node {

    protected AtomicReference<State> state;

    protected ManualScheduleManager schedulerManager;

    public AbstractClusterJobNode() {
        super(BootStrap.properties());
        this.state = new AtomicReference<>();
        this.schedulerManager = new DefaultManualScheduleManager();
        this.state.set(State.LATENT);
    }

    protected enum State { LATENT, JOINED, EXITED}

    protected boolean isJoined() {
        return this.state.get() == State.JOINED;
    }


    @Override
    public void join() {
        AssertHelper.isTrue(state.compareAndSet(State.LATENT, State.JOINED), "illegal state .");
        doJoin();
    }

    @Override
    public void exit() {
        AssertHelper.isTrue(state.compareAndSet(State.JOINED, State.EXITED), "illegal state .");
        doExit();
    }

    protected abstract void doJoin();

    protected abstract void doExit();

    /**
     * Master节点抽象监听器.
     * 本抽象类封装了获取Master权限和失去Master权限时线程的挂起控制
     */
    protected abstract class AbstractLeadershipSelectorListener implements LeaderSelectorListener {

        private final AtomicInteger leaderCount = new AtomicInteger();

        private Object mutex = new Object();

        public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
            LoggerHelper.info(getNodeIp() + " is now the leader ,and has been leader " + this.leaderCount.getAndIncrement() + " time(s) before.");
            boolean isJoined = isJoined();
            try {
                if (isJoined) {
                    acquireLeadership();
                }
            } catch (Throwable e) {
                relinquishLeadership();
                LoggerHelper.warn(getNodeIp() + " startup failed,relinquish leadership.", e);
                return;
            }
            try {
                synchronized (mutex) {
                    mutex.wait();
                }
            } catch (InterruptedException e) {
                LoggerHelper.info(getNodeIp() + " has been interrupted.");
            }
        }

        public void stateChanged(CuratorFramework client, ConnectionState newState) {
            LoggerHelper.info(getNodeIp() + " state has been changed [" + newState + "]");
            if (!newState.isConnected()) {
                relinquishLeadership();
                synchronized (mutex) {
                    mutex.notify();
                }
            }
        }

        public abstract void acquireLeadership() throws Exception;

        public abstract void relinquishLeadership();

    }

}
