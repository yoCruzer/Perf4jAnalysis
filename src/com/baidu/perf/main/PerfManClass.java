/**
 *
 */
package com.baidu.perf.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.baidu.perf.service.Perf4jAnalysisService;
import com.baidu.perf.service.Perf4jDetailAnalysisService;
import com.baidu.perf.service.Perf4jService;
import com.baidu.perf.utils.FileIOUtil;
import com.baidu.perf.utils.NumberUtil;
import com.baidu.perf.utils.TimeUtil;
import com.google.gson.Gson;
public class PerfManClass {

    public static final String CONF_PATH_USER = "conf/user_perf4j_analysis.conf";
    public static final String CONF_PATH_PACKAGE = "conf/package_perf4j_analysis.conf";
    public static final String CONF_PATH_NOTIFY = "conf/notify_perf4j_analysis.conf";
    public static final String CONF_PATH_ONESDK = "conf/onesdk_perf4j_analysis.conf";
    public static final String CONF_PATH_TEST = "E:\\Perf4jAnalysis(Linux)\\conf\\test.conf";
    /**
     * @param args
     */
    public static void main(String[] args) {
    	String serviceName = "CONF_PATH_TEST";//args[0];
    	List<String> confList = new LinkedList<String>();
		try {
			confList = FileIOUtil.getFileContextBIO((String)PerfManClass.class.getField(serviceName).get(PerfManClass.class), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("请输入如下属性：CONF_PATH_USER / CONF_PATH_PACKAGE / CONF_PATH_NOTIFY/ CONF_PATH_ONESDK  之一!");
		}
        if (null == confList || confList.size() == 0) {
            return;
        }
        Map<String, String> confMap = new HashMap<String, String>();
        for (String line : confList) {
            if (null == line || line.trim().length() == 0 || line.trim().startsWith("#")) {
                continue;
            }

            String[] conf = line.split("=");
            if (null == conf || conf.length != 2) {
                continue;
            }

            confMap.put(conf[0].trim(), conf[1].trim());
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(confMap);
        PerfAnalysisParam confParam = gson.fromJson(jsonStr, PerfAnalysisParam.class);

        String yesterday = TimeUtil.getDateStrShift(-1);
        String yesterdayNoSplit = yesterday.replaceAll("-", "");

        int size = NumberUtil.getValueFromArithmetic(confParam.getNio_buffer_size());
        String[] serverIps = confParam.getServer_list().split(",");
        
        for(String serverIp: serverIps){
        	System.out.println(serverIp);
        	List<String> resultList = new ArrayList<String>();
	        String perfPathDetail = confParam.getPerf_path() + confParam.getDetail_name() + yesterday + "." + serverIp;
	        Perf4jAnalysisService dService = new Perf4jAnalysisService(confParam.getPerf_logger(), confParam.getIp_format(), confParam.getPerf_name_format());
	        List<String> detailResult = FileIOUtil.getPerf4jStatisAnalysisNIO(perfPathDetail, size, confParam.getCharset(), dService);
	        
	        //	        if (null != detailResult) {
//	        	resultList.add("主机：" + serverIp + "  日期：" + yesterday + "\n");
//		        resultList.add(confParam.getProject_name() + "性能分析报告：" + "\n");
//	        	resultList.add("基本统计\n");
//	            resultList.addAll(detailResult.get("sta"));
////	            resultList.add("服务器调用情况\n");
////	            resultList.addAll(detailResult.get("ip"));
//	        }	        
	        resultList.addAll(detailResult);
	        String outputName = confParam.getProject_name() + yesterdayNoSplit+serverIp+".txt";
	        FileIOUtil.writeFileSimple(confParam.getOutput_file(), outputName, confParam.getCharset(), resultList);
        }
        
    }
}
