package cache.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService {

	private static final Logger LOGGER = LoggerFactory.getLogger("SWIFT");
	
	@Autowired
	DataDao dataDao;
	
	public int multi(int threads, int key, int row, int col) throws Exception {
		int result = 0;
		for(int i = 0; i < threads; i++) {
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					long start = System.currentTimeMillis();
					float[][] result = dataDao.multi(key, row, col);
					System.out.println(result.length);
					long end = System.currentTimeMillis();
					LOGGER.info("当前并发线程数：[" + threads + "]，数据获取耗时：[" + (end - start) + "]ms");
				}
			});
			t.start();
			result++;
		}
		return result;
	}
	
	public int redisKey(int key, int row, int col) {
		float[][] datas = dataDao.redisKey(key, row, col); 
		return datas.length;
	}
	
	public int redis(int row, int col) {
		float[][] datas = dataDao.redis(row, col); 
		return datas.length;
	}
}
