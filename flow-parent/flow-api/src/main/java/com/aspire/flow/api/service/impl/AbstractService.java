package com.aspire.flow.api.service.impl;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aspire.flow.api.MasterSlaveApiFactory;
import com.aspire.flow.api.impl.MasterSlaveApiFactoryImpl;

/**
 * 抽象的服务实现类.
 * 它包含了主从和主备API工厂实例,可以帮助子类方便的进行ZK操作.
 * @author chenhaitao
 *
 */
@Service
public class AbstractService implements InitializingBean {

    @Autowired
    private CuratorFramework client;


    protected MasterSlaveApiFactory masterSlaveApiFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.masterSlaveApiFactory = new MasterSlaveApiFactoryImpl(client);
    }

}
