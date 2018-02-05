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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;

import com.aspire.flow.api.MasterSlaveJobApi;
import com.aspire.flow.api.data.MasterSlaveJobData;
import com.aspire.flow.helper.JsonHelper;
import com.aspire.flow.helper.PathHelper;

/**
 * 
 * @author chenhaitao
 *
 */
public class MasterSlaveJobApiImpl extends AbstractCurdApiImpl implements MasterSlaveJobApi {

    public MasterSlaveJobApiImpl(CuratorFramework client) {
        super(client);
    }

    @Override
    public List<MasterSlaveJobData> getAllJobs() {
        List<ChildData> childDataList = getChildren(getMasterSlavePathApi().getJobPath());
        List<MasterSlaveJobData> masterSlaveJobDatas = new ArrayList<>();
        if(childDataList != null && childDataList.size() > 0){
        	for (ChildData childData : childDataList) {
        		MasterSlaveJobData jobData = new MasterSlaveJobData(childData);
        		masterSlaveJobDatas.add(jobData);
			}
        }
        return masterSlaveJobDatas;
        //return childDataList.stream().map(MasterSlaveJobData::new).collect(Collectors.toList());
    }

    @Override
    public void saveJob(String flowId, String name, MasterSlaveJobData.Data data) {
        data.prepareOperation();
        MasterSlaveJobData masterSlaveJobData = new MasterSlaveJobData(PathHelper.getJobPath(getMasterSlavePathApi().getJobPath(), flowId), data);
        masterSlaveJobData.getData().incrementVersion();
        if (checkExists(masterSlaveJobData.getPath())) {
            setData(masterSlaveJobData.getPath(), masterSlaveJobData.getDataBytes());
        } else {
            create(masterSlaveJobData.getPath(), JsonHelper.toBytes(masterSlaveJobData.getData()));
        }
    }

    @Override
    public void updateJob(String flowId, String name, MasterSlaveJobData.Data data) {
        MasterSlaveJobData masterSlaveJobData = new MasterSlaveJobData(PathHelper.getJobPath(getMasterSlavePathApi().getJobPath(), flowId), data);
        masterSlaveJobData.getData().incrementVersion();
        setData(masterSlaveJobData.getPath(), masterSlaveJobData.getDataBytes());
    }

    @Override
    public MasterSlaveJobData getJob(String flowId, String name) {
        return getJob(PathHelper.getJobPath(getMasterSlavePathApi().getJobPath(), flowId));
    }

    @Override
    public MasterSlaveJobData getJob(String path) {
        if (!checkExists(path)) {
            return null;
        }
        return new MasterSlaveJobData(getData(path));
    }

}
