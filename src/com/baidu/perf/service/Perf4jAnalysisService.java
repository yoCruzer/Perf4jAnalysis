/**
 *
 */
package com.baidu.perf.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.perf.consts.PerfConst;
import com.baidu.perf.utils.StringUtil;

/**
 * 性能统计日志分析服务
 *
 * @Title: Perf4jAnalysisService.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月15日 上午10:22:36
 * @version V1.0
 */
public class Perf4jAnalysisService implements Perf4jService {

    private String loggerName = null;

    private String loggerFormat = null;
    
    private String ipFprmat = null;
    
    private List<String> propertyList = null;

    public Perf4jAnalysisService(String loggerName, String ipFormat, String loggerFormat) {
        this.loggerName = loggerName;
        this.loggerFormat = loggerFormat;
        this.ipFprmat = ipFormat;
        this.propertyList = new ArrayList<String>();
        this.propertyList.add(PerfConst.TAG_NAME);
        this.propertyList.add(PerfConst.TAG_COUNT);
        this.propertyList.add(PerfConst.TAG_AVG);
        this.propertyList.add(PerfConst.TAG_SLOW_COUNT);
        this.propertyList.add(PerfConst.TAG_SLOW_RATE);
        this.propertyList.add(PerfConst.TAG_FAILE_COUNT);
        this.propertyList.add(PerfConst.TAG_FAILE_RATE);
    }

    @Override
    public String analysisByLine(String line, Map<String, Map<String, Object>> dataMap) throws Exception {
        if (StringUtil.isBlank(line)) {
            return null;
        }

        line = line.replaceAll("\n", "");

        if (line.contains(this.loggerName)) {
            return line.substring(0, 10);
        }

        if (line.startsWith("Tag") && line.contains("Avg(ms)")) {
            return null;
        }

        String[] params = StringUtil.regularSplit(line, " +");
        if (null == params || params.length != 6) {
            return null;
        }

        // 接口标识
        String tag = params[0];
        // 平均相应时间
        Float avg = Float.valueOf(params[1]);
        // 调用次数
        String times = params[5].trim();
        Integer count = Integer.valueOf(times);

        String tagName = null;
        String source_ip = null;
        if(tag.indexOf('-') > 0){
        	tagName = tag.substring(tag.lastIndexOf('-') + 1, tag.length());
        	source_ip = tag.substring(0, tag.indexOf('-'));
        	return null;
        }else{
        	tagName = tag;
        }
        // 行结果合并到总结果集
        Map<String, Object> resultMap = dataMap.get(tagName);
        if (null == resultMap) {
            resultMap = new HashMap<String, Object>();
            resultMap.put(PerfConst.TAG_NAME, tagName);
            resultMap.put(PerfConst.TAG_COUNT, count);
            resultMap.put(PerfConst.TAG_AVG, avg);
            dataMap.put(tagName, resultMap);
        } else {
            int oldCount = (Integer) resultMap.get(PerfConst.TAG_COUNT);
            int newCount = oldCount + count;
            resultMap.put(PerfConst.TAG_COUNT, newCount);
            float oldAvg = (Float) resultMap.get(PerfConst.TAG_AVG);
            float newAvg = (count * avg + oldCount * oldAvg) / newCount;
            resultMap.put(PerfConst.TAG_AVG, newAvg);
        }
        return null;
    }

    @Override
    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    @Override
    public List<String> getPropertyList() {
        return this.propertyList;
    }

    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

    @Override
    public String getLoggerFormat() {
        return this.loggerFormat;
    }

    public void setLoggerFormat(String loggerFormat) {
        this.loggerFormat = loggerFormat;
    }

	@Override
	public String getIpFormat() {
		return this.ipFprmat;
	}

}
