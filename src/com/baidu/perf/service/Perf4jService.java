/**
 *
 */
package com.baidu.perf.service;

import java.util.List;
import java.util.Map;

/**
 * Perf4j日志分析通用接口
 *
 * @Title: Perf4jService.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月15日 上午10:29:40
 * @version V1.0
 */
public interface Perf4jService {

    public String analysisByLine(String line, Map<String, Map<String, Object>> dataMap) throws Exception;

    public String getLoggerName();

    public List<String> getPropertyList();

    public String getLoggerFormat();

    public String getIpFormat();
}
