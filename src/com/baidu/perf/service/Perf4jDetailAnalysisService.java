/**
 *
 */
package com.baidu.perf.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.baidu.perf.consts.PerfConst;
import com.baidu.perf.utils.StringUtil;

/**
 * 性能详细日志分析服务
 *
 * @Title: Perf4jDetailAnalysisService.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月15日 上午10:23:30
 * @version V1.0
 */
public class Perf4jDetailAnalysisService{

    private String loggerName = null;

    private String loggerFormat = null;

    private String ipFormat = null;
    
    public void setIpFormat(String ipFormat) {
		this.ipFormat = ipFormat;
	}

    public String getIpFormat() {
		return this.ipFormat;
	}
    
	private List<String> propertyList = null;

    public Perf4jDetailAnalysisService(String loggerName, String ipFormat, String loggerFormat) {
        this.loggerName = loggerName;
        this.loggerFormat = loggerFormat;
        this.ipFormat = ipFormat;
        this.propertyList = new ArrayList<String>();
        this.propertyList.add(PerfConst.TAG_NAME);
        this.propertyList.add(PerfConst.TAG_SLOW_COUNT);
        this.propertyList.add(PerfConst.TAG_MAX_SLOW);
        this.propertyList.add(PerfConst.TAG_FAILE_COUNT);
    }

    public String analysisByLine(String line, Map<String, List<Integer>> dataMap) throws Exception {
        if (StringUtil.isBlank(line)) {
            return null;
        }

        line = line.replaceAll("\n", "");
        // 解析时间
        int time = 0;
        List<String> timeList = StringUtil.regularMatch(line, "time\\[[0-9]+\\]");
        if (null != timeList && timeList.size() > 0) {
            String timeStr = timeList.get(0);
            timeStr = timeStr.replaceAll("time\\[", "");
            timeStr = timeStr.replaceAll("\\]", "");
            time = Integer.valueOf(timeStr);
        }

        // 解析接口名
        String tagName = null;
        List<String> nameList = StringUtil.regularMatch(line, "tag\\[[^\\[\\]\\f\\n\\r\\t\\v]+\\]");
        if (null != nameList && nameList.size() > 0) {
            String nameStr = nameList.get(0);
            nameStr = nameStr.replaceAll("tag\\[", "");
            nameStr = nameStr.replaceAll("\\]", "");
            if(nameStr.indexOf("-")>0){
            	return nameStr;
            }else{
            	tagName = nameStr;
            }
        }
        
        
        // 行结果合并到总结果集
        List<Integer> resultList = dataMap.get(tagName);
        if (null == resultList) {
            resultList = new LinkedList<Integer>();
            resultList.add(time);
            dataMap.put(tagName, resultList);
        } else {
        	resultList.add(time);
            dataMap.put(tagName, resultList);
        }
        return null;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getLoggerFormat() {
        return this.loggerFormat;
    }

    public void setLoggerFormat(String loggerFormat) {
        this.loggerFormat = loggerFormat;
    }

    public List<String> getPropertyList() {
        return this.propertyList;
    }

    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

}
