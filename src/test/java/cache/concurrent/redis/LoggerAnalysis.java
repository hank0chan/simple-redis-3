package cache.concurrent.redis;

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

import org.junit.Test;

/**
 * Redis缓存并发日志分析程序
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 15:50:43 - 17 Feb 2017
 * @detail
 */
public class LoggerAnalysis {

	/**
	 * 日志分析程序
	 * @throws IOException
	 */
	@Test
	public void testResult() throws IOException {
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
			
			/*int gtAvgNum10 = 0; // 比平均值还需要多消耗10ms的个数
			int gtAvgNum30 = 0; // 比平均值还需要多消耗30ms的个数
			int gtAvgNum50 = 0; // 比平均值还需要多消耗50ms的个数
			int ltAvgNum10 = 0; // 比平均值还少消耗10ms的个数
			int ltAvgNum30 = 0; // 比平均值还少消耗30ms的个数
			int ltAvgNum50 = 0; // 比平均值还少消耗30ms的个数
			for(Integer i : value) {
				if(i > (avgTime + 50)) {
					gtAvgNum50++;
				} else if(i > (avgTime + 30)) {
					gtAvgNum30++;
				} else if(i > (avgTime + 10)) {
					gtAvgNum10++;
				} else if(i < (avgTime - 50)) {
					ltAvgNum50++;
				} else if(i < (avgTime - 30)) {
					ltAvgNum30++;
				} else if(i < (avgTime - 10)) {
					ltAvgNum10++;
				}
			}
			System.out.println("其中，比平均值多消耗10ms的样本有：" + (gtAvgNum10*100/(value.size()*100.0)));
			System.out.println("其中，比平均值多消耗30ms的样本有：" + (gtAvgNum30*100/(value.size()*100.0)));
			System.out.println("其中，比平均值多消耗50ms的样本有：" + (gtAvgNum50*100/(value.size()*100.0)));*/
		}
	}
	
	private Map<Integer, String> calcu(String str) {
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
	
	@Test
	public void test2() {
		List<String> result = new ArrayList<>();
		String str = "hello[abc2012]addce-dfs[5]dfa-df[248]ms";
		String[] strs = str.split("\\[");
		for(String s : strs) {
			System.out.println(s);
			String[] s2 = s.split("\\]");
			result.add(s2[0]);
			System.out.println(s2[0]);
		}
		System.out.println(result.get(0) + ";" + result.get(1) + ";" + result.get(2) + ";" + result.get(3));
		String thread = result.get(2);
		String time = result.get(3);
		System.out.println(thread + ";" + time);
	}
}
