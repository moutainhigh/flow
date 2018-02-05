package com.aspire.etl.flowengine.taskcontext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.aspire.etl.flowdefine.TaskType;
import com.aspire.etl.flowmetadata.dao.FlowMetaData;
import com.aspire.etl.tool.Utils;

/**
 * 
 * @ClassName: ImportPropContext
 * @Description: TODO
 * @author duke xia
 * @date 2017年6月12日 下午10:21:54
 * 
 */
public class ImportPropContext {
	public Boolean call( Map<String, String> interfaceMap ) throws Exception {
		boolean isOK = false;
		int retValue = -1;
		Logger log = Logger.getLogger( this.getClass() );

		FlowMetaData flowMetaData = FlowMetaData.getInstance();
		String className = flowMetaData.queryTaskType( TaskType.SHELL ).getEnginePlugin();
		String classDir = flowMetaData.queryTaskType( TaskType.SHELL ).getEnginePluginJar();
		try {
			Class pluginClass = Utils.loadClassByName( classDir, className );
			Object pluginObject = null;

			Constructor constructor = pluginClass.getConstructor();
			pluginObject = constructor.newInstance( new Object[]{} );

			Method mLoadValue = pluginClass.getMethod( "executeShell", new Class[]{ java.util.Map.class } );
			retValue = (Integer) mLoadValue.invoke( pluginObject, new Object[]{ interfaceMap } );

			isOK = ( retValue == 0 ) ? true : false;

		} catch ( MalformedURLException e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		} catch ( ClassNotFoundException e1 ) {
			log.error( e1 + "从目录" + classDir + " 加载" + className + "失败！", e1 );
		} catch ( InstantiationException e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		} catch ( IllegalAccessException e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		} catch ( IllegalArgumentException e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		} catch ( NoSuchMethodException e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		} catch ( Exception e1 ) {
			log.error( e1 + "调用" + classDir + " 下的" + className + "失败！", e1 );
		}
		return isOK;
	}

}
