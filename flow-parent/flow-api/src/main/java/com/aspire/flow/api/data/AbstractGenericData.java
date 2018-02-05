
package com.aspire.flow.api.data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.curator.framework.recipes.cache.ChildData;

import com.aspire.flow.exception.UnknownGenericTypeException;
import com.aspire.flow.helper.JsonHelper;
import com.aspire.flow.helper.PathHelper;

/**
 * ZK数据节点抽象的通用类
 * @author chenhaitao
 *
 * @param <E>
 * @param <T>
 */
public abstract class AbstractGenericData<E extends AbstractGenericData, T extends Comparable<T>> extends AbstractData implements Comparable<E> {

    private T data;

    public AbstractGenericData(ChildData childData) {
        this(childData.getPath(), childData.getData());
    }

    public AbstractGenericData(String path, byte[] bytes) {
        this.path = path;
        this.id = PathHelper.getEndPath(this.path);
        this.data = JsonHelper.fromJson(bytes, getGenericType());
    }

    public AbstractGenericData(String path, T data) {
        this.path = path;
        this.id = PathHelper.getEndPath(this.path);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private Class<T> getGenericType() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<T>) parameterizedType.getActualTypeArguments()[1];
        }
        throw new UnknownGenericTypeException();
    }

    public byte[] getDataBytes() {
        return JsonHelper.toBytes(data);
    }

    @Override
    public int compareTo(E e) {
        return data.compareTo((T) e.getData());
    }

    @Override
    public String toString() {
        return "GenericData{" +
                "data=" + JsonHelper.toJson(data) +
                '}';
    }

}
