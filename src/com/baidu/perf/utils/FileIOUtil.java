/**
 *
 */
package com.baidu.perf.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.baidu.perf.service.Perf4jAnalysisService;
import com.baidu.perf.service.Perf4jDetailAnalysisService;
import com.baidu.perf.service.Perf4jService;

/**
 * 文件io公共工具
 *
 * @Title: FileIOUtil.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月14日 下午1:45:55
 * @version V1.0
 */
@SuppressWarnings("unused")
public class FileIOUtil {

	private static final int TIME_LEVEL = 11;
    /**
     * nio方式统计文件行数，适合处理大文件，且结果比bio方式更精准
     *
     * @param filename
     */
    public static long getFileLinesNIO(String filename, int bufferSize) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(filename));
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            FileChannel channel = fis.getChannel();

            long lines = 1, readCount;
            while (-1 != (readCount = channel.read(buffer))) {
                buffer.flip();
                for (int i = 0; i < readCount; i++) {
                    if (buffer.get(i) == '\n') {
                        lines++;
                    }
                }
            }

            channel.close();
            return lines;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * NIO方式读取文件内容<br>
     * 因为是解析所有行后一起返回，大文件会造成内存溢出<br>
     * 对于大文件，需要把解析的工作也一起放在本方法中处理，不在内存中保留行数据
     *
     * @param filename
     * @return
     */
    public static List<String> getFileContextNIO(String filename, int bufferSize, String cs) {

        FileInputStream fis = null;
        List<String> resultList = new ArrayList<String>();
        try {
            Charset charset = Charset.forName(cs);
            fis = new FileInputStream(new File(filename));
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            FileChannel channel = fis.getChannel();

            StringBuilder aline = new StringBuilder("");
            while (channel.read(buffer) != -1) {
                buffer.flip();
                CharBuffer lineBuffer = charset.decode(buffer);
                for (int i = 0; i < lineBuffer.length(); i++) {
                    if (lineBuffer.get(i) == '\n') {
                        resultList.add(aline.toString());
                        aline = new StringBuilder("");
                        continue;
                    }

                    aline.append(lineBuffer.get(i));
                }
                buffer.clear();
            }

            channel.close();
            return resultList;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * bio方式统计文件行数，效率较nio差，适合小文件
     *
     * @param filename
     * @return
     */
    public static long getFileLinesBIO(String filename) {

        FileInputStream fis = null;
        try {

            fis = new FileInputStream(new File(filename));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            long lines = 1;
            while (reader.readLine() != null) {
                lines++;
            }

            reader.close();
            return lines;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return 0;
    }

    /**
     * NIO方式读取文件内容<br>
     * 因为是解析所有行后一起返回，大文件会造成内存溢出<br>
     * 对于大文件，需要把解析的工作也一起放在本方法中处理，不在内存中保留行数据
     *
     * @param filename
     * @return
     */
    public static List<String> getPerf4jStatisAnalysisNIO(String filename, int bufferSize, String cs, Perf4jAnalysisService service) {

        FileInputStream fis = null;
        List<String> resultList = new ArrayList<String>();
        Map<String, Map<String, Object>> dataMap = new HashMap<String, Map<String, Object>>();
        Map<String, Integer> ipMap = new HashMap<String , Integer>();
        try {
            Charset charset = Charset.forName(cs);
            fis = new FileInputStream(new File(filename));
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            FileChannel channel = fis.getChannel();

            StringBuilder aline = new StringBuilder("");
            while (channel.read(buffer) != -1) {
                buffer.flip();
                CharBuffer lineBuffer = charset.decode(buffer);
                for (int i = 0; i < lineBuffer.length(); i++) {
                    if (lineBuffer.get(i) == '\n') {
                        // 解析每行数据，合并到总数据map
                        String ip_source = service.analysisByLine(aline.toString(), dataMap);
                        aline = new StringBuilder("");
                        if(ipMap.containsKey(ip_source)){
                        	ipMap.put(ip_source, ipMap.get(ip_source) + 1);
                        }else{
                        	ipMap.put(ip_source, 1);
                        }
                    }

                    aline.append(lineBuffer.get(i));
                }

                // charset.decode(buffer) 导致buffer的position被重置为limit，需要再重置
                buffer.clear();
            }

            // 组装输出结果字符串
            String dataFormater = service.getLoggerFormat();
            List<String> propertyList = service.getPropertyList();
            int total = 0;
            for (String key : dataMap.keySet()) {
                Map<String, Object> lineMap = dataMap.get(key);
                Object[] datas = new Object[propertyList.size()];
                for (int i = 0; i < propertyList.size(); i++) {
                    datas[i] = lineMap.get(propertyList.get(i));
                }
                total += (Integer)datas[1];
                String data = String.format(dataFormater, datas) + "\n";
                resultList.add(data);
            }
            System.out.println(total);
            channel.close();
            return resultList;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    /**
     * NIO方式读取文件内容<br>
     * 因为是解析所有行后一起返回，大文件会造成内存溢出<br>
     * 对于大文件，需要把解析的工作也一起放在本方法中处理，不在内存中保留行数据
     *
     * @param filename
     * @return
     */
    public static Map<String, List<String>> getPerf4jDetailAnalysisNIO(String filename, int bufferSize, String cs, Perf4jDetailAnalysisService service) {

        FileInputStream fis = null;
        Map<String , List<String>> resultMap = new HashMap<String, List<String>>();
        List<String> staticList = new ArrayList<String>();
        List<String> iptagList = new ArrayList<String>();
        Map<String, List<Integer>> dataMap = new HashMap<String, List<Integer>>();
        Map<String, Integer> ipMap = new HashMap<String , Integer>();
        try {
            Charset charset = Charset.forName(cs);
            fis = new FileInputStream(new File(filename));
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            FileChannel channel = fis.getChannel();

            StringBuilder aline = new StringBuilder("");
            while (channel.read(buffer) != -1) {
                buffer.flip();
                CharBuffer lineBuffer = charset.decode(buffer);
                for (int i = 0; i < lineBuffer.length(); i++) {
                    if (lineBuffer.get(i) == '\n') {
                        String ip_source = service.analysisByLine(aline.toString(), dataMap);
                        aline = new StringBuilder("");
                        if(!StringUtil.isBlank(ip_source)){
	                        if(ipMap.containsKey(ip_source)){
	                        	ipMap.put(ip_source, ipMap.get(ip_source) + 1);
	                        }else{
	                        	ipMap.put(ip_source, 1);
	                        }
                        }
                    }
                    aline.append(lineBuffer.get(i));
                }
                buffer.clear();
            }

            // 组装输出结果字符串
            String dataFormater = service.getLoggerFormat();
            List<String> propertyList = service.getPropertyList();
            for (String key : dataMap.keySet()) {
                List<Integer> lineMap = dataMap.get(key);
                int total = 0;
                Collections.sort(lineMap);
                int medium = lineMap.get(lineMap.size()/2);
                int[] datas = new int[TIME_LEVEL+1];
                for(Integer time : lineMap){
                	total += time;
                	int level = time/200;
                	if(level >= TIME_LEVEL){
                		level = TIME_LEVEL;
                	}
                	datas[level] += 1;
                }
                float avg = total/Float.valueOf(lineMap.size());
                Object[] statisDatas = new Object[4];
                statisDatas[0] = key;
                statisDatas[1] = medium;
                statisDatas[2] = avg;
                statisDatas[3] = lineMap.size();
                String dataStatis = String.format(dataFormater, statisDatas) + "\n";
                staticList.add(dataStatis);
                StringBuilder sb = new StringBuilder();
                sb.append("DIS").append("\t");
                for(Integer v:datas){
                	sb.append(v).append("\t");
                }
                String dataDistri = sb.toString() + "\n";
                staticList.add(dataDistri);
            }
            
            //String ipFormater = service.getIpFormat();
            //Map<String, List<String>> ipTagMap = new HashMap<String, List<String>>();
//            for(Entry<String, Integer> entity : ipMap.entrySet()){
//            	Object[] datas = new Object[2];
//            	String ipSource = entity.getKey().substring(0, entity.getKey().indexOf("-"));
//            	datas[0] = entity.getKey().substring(entity.getKey().lastIndexOf("-")+1, entity.getKey().length());
//            	datas[1] = entity.getValue();
//            	String data = "\t"+String.format(ipFormater, datas) + "\n";
//            	if(ipTagMap.containsKey(ipSource)){
//            		List<String> ipList = ipTagMap.get(ipSource);
//            		ipList.add(data);
//            	}else{
//            		List<String> ipList = new LinkedList<String>();
//            		ipList.add(data);
//            		ipTagMap.put(ipSource, ipList);
//            	}
//            }
//            for(Entry<String, List<String>> entity: ipTagMap.entrySet()){
//            	iptagList.add(entity.getKey()+"\n");
//            	iptagList.addAll(entity.getValue());
//            }
            channel.close();
            resultMap.put("sta", staticList);
            resultMap.put("ip", iptagList);
            return resultMap;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    
    /**
     * BIO方式读取文件内容<br>
     * 因为是解析所有行后一起返回，大文件会造成内存溢出<br>
     * 对于大文件，需要把解析的工作也一起放在本方法中处理，不在内存中保留行数据
     *
     * @param filename
     * @return
     */
    public static List<String> getFileContextBIO(String filename, String charset) {

        FileInputStream fis = null;
        List<String> relustList = new ArrayList<String>();
        try {
            fis = new FileInputStream(new File(filename));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, charset));

            String line = null;
            while ((line = reader.readLine()) != null) {
                relustList.add(line);
            }

            reader.close();
            return relustList;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static void writeFileSimple(String filepath, String filename, String charset, List<String> context) {
        try {
            // 建立文件夹
            File fp = new File(filepath);
            if (!fp.exists()) {
                fp.mkdir();
            }

            // 写数据
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filepath + filename), charset);
            for (String line : context) {
                out.write(line);
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        // FileIOUtil.getFileLinesBIO("D:\\CRT downloads\\standalone_perf4j.log.2015-10-11");
        // FileIOUtil.getFileLinesNIO("D:\\CRT downloads\\standalone_app.log.2015101100", 1024 * 512);
        // FileIOUtil.getFileContextNIO("D:\\CRT downloads\\standalone_app.log.2015101100", 1024 * 512, "utf-8");
        // FileIOUtil.getFileContextBIO("D:\\CRT downloads\\standalone_perf4j.log.2015-10-11");

        List<String> aaa = new ArrayList<String>();
        aaa.add("12131341\n");
        aaa.add("sdafafaf\ndadwada\n");
        writeFileSimple("C:\\Users\\maolei\\Desktop\\Perf4jAnalysis\\", "abc.txt", "UTF-8", aaa);
    }
}
