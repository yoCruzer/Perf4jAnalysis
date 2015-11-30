/**
 *
 */
package com.baidu.perf.main;

/**
 * 性能监控配置文件参数装载类
 *
 * @Title: PerfAnalysisParam.java
 * @Description: TODO(用一句话描述该文件做什么)
 * @author maolei
 * @date 2015年10月14日 下午6:56:15
 * @version V1.0
 */
public class PerfAnalysisParam {

    /**
     * 日志文件编码
     */
    private String charset = null;


    /**
     * 性能统计日志位置
     */
    private String perf_path = null;

    /**
     * 性能统计日志名称格式
     */
    private String perf_name = null;

    /**
     * 性能日志统计结果格式
     */
    private String perf_name_format = null;

	/**
     * 性能日志logger名称
     */
    private String perf_logger = null;

    /**
     * 性能日志时间间隔 毫秒
     */
    private int perf_span = 0;

    /**
     * 性能统计日志名称格式
     */
    private String detail_name = null;

    /**
     * 性能日志统计结果格式
     */
    private String detail_name_format = null;

	/**
     * 性能日志logger名称
     */
    private String detail_logger = null;

    /**
     * 性能日志时间间隔 毫秒
     */
    private int detail_span = 0;

    private String ip_format;

	/**
     * nio文件读取缓存区大小
     */
    private String nio_buffer_size = null;

    /**
     * 结果输出文件路径
     */
    private String output_file = null;

    /**
     * 项目名称
     */
    private String project_name = null;

    /**
     * 服务器列表
     */
    private String server_list = null;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getPerf_path() {
		return perf_path;
	}

	public void setPerf_path(String perf_path) {
		this.perf_path = perf_path;
	}

	public String getPerf_name() {
		return perf_name;
	}

	public void setPerf_name(String perf_name) {
		this.perf_name = perf_name;
	}

	public String getPerf_name_format() {
		return perf_name_format;
	}

	public void setPerf_name_format(String perf_name_format) {
		this.perf_name_format = perf_name_format;
	}

	public String getPerf_logger() {
		return perf_logger;
	}

	public void setPerf_logger(String perf_logger) {
		this.perf_logger = perf_logger;
	}

	public int getPerf_span() {
		return perf_span;
	}

	public void setPerf_span(int perf_span) {
		this.perf_span = perf_span;
	}

	public String getDetail_name() {
		return detail_name;
	}

	public void setDetail_name(String detail_name) {
		this.detail_name = detail_name;
	}

	public String getDetail_name_format() {
		return detail_name_format;
	}

	public void setDetail_name_format(String detail_name_format) {
		this.detail_name_format = detail_name_format;
	}

	public String getDetail_logger() {
		return detail_logger;
	}

	public void setDetail_logger(String detail_logger) {
		this.detail_logger = detail_logger;
	}

	public int getDetail_span() {
		return detail_span;
	}

	public void setDetail_span(int detail_span) {
		this.detail_span = detail_span;
	}

	public String getIp_format() {
		return ip_format;
	}

	public void setIp_format(String ip_format) {
		this.ip_format = ip_format;
	}

	public String getNio_buffer_size() {
		return nio_buffer_size;
	}

	public void setNio_buffer_size(String nio_buffer_size) {
		this.nio_buffer_size = nio_buffer_size;
	}

	public String getOutput_file() {
		return output_file;
	}

	public void setOutput_file(String output_file) {
		this.output_file = output_file;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getServer_list() {
		return server_list;
	}

	public void setServer_list(String server_list) {
		this.server_list = server_list;
	}

}
