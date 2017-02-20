package cache.concurrent.redis;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cache.data.DataDao;
import cache.service.SwiftCacheService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext.xml"})
public class RedisMaxIOTest {
	
	static final Logger LOGGER = LoggerFactory.getLogger("SWIFT");
	
	@Autowired
	SwiftCacheService swiftCacheService;
	@Autowired
	DataDao dataDao;
	
	/**
	 * 多线程调用测试
	 * @throws Exception
	 */
	@Test
	public void test2() throws Exception {
		int threads = 20;
		int key = 1;
		int row = 2000;
		int col = 2000;
		List<Long> times = new ArrayList<>();
		for(int i = 0; i < threads; i++) {
			System.out.println(i);
			long start = System.currentTimeMillis();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					dataDao.multi(key, row, col);
//					times.add((end - start));
				}
			});
			t.start();
			long end = System.currentTimeMillis();
			LOGGER.info("当前线程，数据获取耗时：" + (end - start) + "ms");
		}
		for(Long time : times) {
			System.out.println("耗时：" + time);
		}
	}

	/**
	 * Redis的纯粹数据存取操作消耗时间测试
	 * @throws Exception 
	 */
	@Test
	public void test() throws Exception {
		// 存
		for(int i = 0; i < 100; i++) {
			float[][] datas = redisCreateArrays(i, 2001, 2001);
			long start = System.currentTimeMillis();
			swiftCacheService.put("TestCache", 1200, (i + ""), datas);
			long end = System.currentTimeMillis();
			LOGGER.debug("第 " + i + " 次存数据操作消耗时间: " + (end - start) + "ms");
//			Thread.sleep(100);
		}
		// 取
		for(int i = 0; i < 100; i++) {
			long start = System.currentTimeMillis();
			swiftCacheService.get("TestCache", (i + ""));
			long end = System.currentTimeMillis();
			LOGGER.info("第 " + i + " 次取数据操作消耗时间: " + (end - start) + "ms");
//			Thread.sleep(100);
		}
	}
	
	private float[][] redisCreateArrays(int key, int row, int col) {
		float[][] result = new float[row][col];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				key++;
				result[i][j] = key;
			}
		}
		return result;
	}
}
