
package com.aspire.flow.api.data;

/**
 * ZK数据节点抽象类
 * @author chenhaitao
 *
 */
public abstract class AbstractData {

    protected String id;

    protected String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
