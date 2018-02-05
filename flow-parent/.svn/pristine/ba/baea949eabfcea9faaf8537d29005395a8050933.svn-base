package com.aspire.etl.tool;

import java.util.*;

public class IPFilter {
	
	static List<String> whiteList = null;
	
	static List<String> blackList = null;
	
	public void init(List<String> whiteList){
		init(whiteList, null);
	}

	public void init(List<String> whiteList, List<String> blackList) {
		IPFilter.whiteList = whiteList;
		IPFilter.blackList = blackList;
	}
	
	public boolean isPermit(String ip){
		boolean result = false;
		//先检查黑名单
		result = isInList(ip, blackList);
		if(result)//在黑名单里
			return false;
		//检查白名单
		result = isInList(ip, whiteList);
		return result;
	}

	//检查IP是否在列表里
	private boolean isInList(String ip, List<String> list) {
		if(list != null && !list.isEmpty()){
			for(String s : list){
				if(ipMatch(ip, s))
					return true;
			}
		}
		return false;
	}
	
	//检查IP是否匹配
	private boolean ipMatch(String ip, String s) {
		String[] ss = s.split("\\.");
		String[] ips = ip.split("\\.");
		for(int i = 0; i < ss.length; ++i){
			//*号免费通过
			if("*".equals(ss[i]))
				continue;
			if(!ss[i].equals(ips[i]))
				return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IPFilter filter = new IPFilter();
		blackList = new ArrayList<String>();
//		blackList.add("*.*.*.*");
		blackList.add("192.168.0.*");
		blackList.add("192.168.1.11");
		whiteList = new ArrayList<String>();
		whiteList.add("192.168.1.*");
		System.out.println(filter.isPermit("192.168.0.21"));
		System.out.println(filter.isPermit("192.168.1.21"));
		System.out.println(filter.isPermit("192.168.1.11"));
		System.out.println(filter.isPermit("192.168.2.21"));
	}

}
