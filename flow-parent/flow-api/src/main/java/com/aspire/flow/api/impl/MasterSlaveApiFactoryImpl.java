/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aspire.flow.api.impl;

import org.apache.curator.framework.CuratorFramework;

import com.aspire.flow.api.MasterSlaveApiFactory;
import com.aspire.flow.api.MasterSlaveJobApi;
import com.aspire.flow.api.MasterSlaveNodeApi;
import com.aspire.flow.api.MasterSlavePathApi;

/**
 * 主从模式API的工厂接口实现
 * @author chenhaitao
 *
 */
public class MasterSlaveApiFactoryImpl implements MasterSlaveApiFactory {

    private CuratorFramework client;

    public MasterSlaveApiFactoryImpl(CuratorFramework client) {
        this.client = client;
    }

    @Override
    public MasterSlavePathApi pathApi() {
        return MasterSlavePathApiImpl.INSTANCE;
    }

    @Override
    public MasterSlaveNodeApi nodeApi() {
        return new MasterSlaveNodeApiImpl(client);
    }

    @Override
    public MasterSlaveJobApi jobApi() {
        return new MasterSlaveJobApiImpl(client);
    }

}
