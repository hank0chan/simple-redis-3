package cache.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Redis缓存并发日志分析程序
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 15:50:43 - 17 Feb 2017
 * @detail <h1>使用说明</h1>对日志格式类似以下：
 * <p>14:15:01.316 [Thread-2185] INFO  SWIFT - Cache Hit! key: cache.data.DataDao.multi(int,int,int).5,2000,2000
 * <p>14:15:01.316 [Thread-2185] INFO  SWIFT - 当前并发线程数：[10]，数据获取耗时：[453]ms
 * <p>的日志内容进行分析。
 * <p>主要原理：通过扫描分析每一行日志中，是否存在三个中括号“[ ]”的日志记录。如果是，将后面两个“[ ]”中的内容提取出来进行分析。
 * 最后得到指定的并发数下的平均耗时，最小耗时，最大耗时等信息。
 * <h1>注意必须是每行有且只有三个中括号，并且后两个括号中不允许存在空格。
 */
public class LoggerAnalysisMain {

	public static void main(String[] args) throws IOException {
		Map<Integer, List<Integer>> threadsTime = new HashMap<>();
		List<String> list = new ArrayList<>();
		String encoding = "UTF-8";
		File file = new File("logger.log");
		if(file.isFile() && file.exists()) {
			InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null) {
				list.add(lineTxt);
				String str = lineTxt;
				Map<Integer, String> map = calcu(str);
				if(!map.isEmpty()) {
					for(Entry<Integer, String> entry : map.entrySet()) {
						Integer key = entry.getKey();
						String value = entry.getValue();
						if(threadsTime.containsKey(key)) {
							threadsTime.get(key).add(Integer.valueOf(value));
						} else {
							List<Integer> v = new ArrayList<>();
							v.add(Integer.valueOf(value));
							threadsTime.put(key, v);
						}
					}
				}
			}
			bufferedReader.close();
			read.close();
		} else {
			System.out.println("文件不存在。。");
		}
		System.out.println("Success..");
		// 开始输出分析结果
		for(Entry<Integer, List<Integer>> entry : threadsTime.entrySet()) {
			int key = entry.getKey();
			List<Integer> value = entry.getValue();
			int time = 0; // 消耗时间总和
			for(Integer i : value) {
				time += i;
			}
			int maxTime = Collections.max(value);
			int minTime = Collections.min(value);
			int avgTime = (time/value.size());   // 平均耗时
			System.out.println(key + "个并发情况下，平均耗时: " + avgTime + "ms；" 
					+ "最小耗时：" + minTime + "ms；最大耗时：" + maxTime
					+ "ms；共有：" + value.size() + "个测试样本");
			// 分析耗时分布情况
			int AvgTime30 = 0; // 在平均耗时正负30ms范围内的个数
			for(Integer i : value) {
				if(i < (avgTime+30)) {
					AvgTime30++;
				}
			}
			System.out.println("总个数有: " + value.size() + "；在不超过耗时平均值30ms的个数有：" 
					+ AvgTime30 + "；百分比为：" + (AvgTime30*100/(value.size()*100.0)));
		}
	}
	
	private static Map<Integer, String> calcu(String str) {
		Map<Integer, String> map = new HashMap<>();
		List<String> result = new ArrayList<>();
		String[] strs = str.split("\\[");
		if(strs.length < 3) {
			return map;
		}
		for(String s : strs) {
			String[] s2 = s.split("\\]");
			result.add(s2[0]);
		}
		String threadNum = result.get(2);
		String time = result.get(3);
		map.put(Integer.valueOf(threadNum), time);
		return map;
	}
}
