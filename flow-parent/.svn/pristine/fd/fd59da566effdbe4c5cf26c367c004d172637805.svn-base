package com.aspire.etl.flowengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.PropertyConfigurator;

import com.aspire.elt.exception.CfgException;
import com.aspire.elt.utils.FileUtils;

/**
 * 
 * <p>Title: FlowEngineCfgLoader</p>
 * <p>@Description:TODO</p>
 * <p>Company: aspire</p>	
 * @author chenhaitao
 * @date:2017年9月6日下午2:56:16
 * @version:1.0
 */
public class FlowEngineCfgLoader {

    public static FlowCfg load(String confPath) throws CfgException {

        String cfgPath = confPath + "/flowengine.cfg";
        String log4jPath = confPath + "/log4j.properties";

        Properties conf = new Properties();
        File file = new File(cfgPath);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new CfgException("can not find " + cfgPath);
        }
        try {
            conf.load(is);
        } catch (IOException e) {
            throw new CfgException("Read " + cfgPath + " error.", e);
        }

        FlowCfg cfg = new FlowCfg();
        
        Map<String, String> configs = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : conf.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("configs.")) {
                String value = entry.getValue() == null ? null : entry.getValue().toString();
                configs.put(key.replace("configs.", ""), value);
            }
        }

        cfg.setConfigs(configs);

        if (FileUtils.exist(log4jPath)) {
        	//  log4j 配置文件路径
            PropertyConfigurator.configure(log4jPath);
        }
        return cfg;
    }

}
