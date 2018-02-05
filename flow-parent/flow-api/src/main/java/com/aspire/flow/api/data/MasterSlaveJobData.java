

package com.aspire.flow.api.data;

import org.apache.curator.framework.recipes.cache.ChildData;

import lombok.Getter;
import lombok.Setter;

/**
 * 主从模式ZK Job数据节点类
 * @author chenhaitao
 *
 */
@Setter
@Getter
public class MasterSlaveJobData extends AbstractGenericData<MasterSlaveJobData, MasterSlaveJobData.Data> {

    public MasterSlaveJobData(ChildData childData) {
        super(childData);
    }

    public MasterSlaveJobData(String path, byte[] bytes) {
        super(path, bytes);
    }

    public MasterSlaveJobData(String path, Data data) {
        super(path, data);
    }

    @Setter
    @Getter
    public static class Data extends AbstractJobData<Data> {

        private String nodePath;

        public void clearNodePath() {
            this.nodePath = null;
        }

        public void init() {
            super.init();
            setNodePath(null);
        }

        public void release() {
            clearNodePath();
            if (isStartup()) {
                setJobOperation("Start");
            }
        }

    }

}
