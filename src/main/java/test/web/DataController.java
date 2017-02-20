package test.web;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cache.data.DataService;
import cache.util.TimeUtils;

@Controller
public class DataController {

	private static final Logger LOGGER = LoggerFactory.getLogger("SWIFT");
	
	@Autowired
	DataService dataService;
	
	@RequestMapping("/multi{threads}/{key}-{row}-{col}.json")
	public @ResponseBody APIResult multi(@PathVariable("threads") int threads, 
			@PathVariable("key") int key, @PathVariable("row") int row, @PathVariable("col") int col) throws Exception {
		APIResult apiResult = APIResult.prepare();
		long start = System.currentTimeMillis();
		int result = dataService.multi(threads, key, row, col);
		long end = System.currentTimeMillis();
//		LOGGER.info("本次数据获取耗时：" + (end - start) + "ms");
		return result == 0 ? apiResult.error("") 
				: apiResult.ok("结果长度：" + result + "；耗时：" + (end - start) + "ms");
	}
	
	@RequestMapping("/rediskey/{key}-{row}-{col}.json")
	public @ResponseBody APIResult redisKey(@PathVariable("key") int key, 
			@PathVariable("row") int row, @PathVariable("col") int col) {
		APIResult apiResult = APIResult.prepare();
		long start = System.currentTimeMillis();
		int result = dataService.redisKey(key, row, col);
		long end = System.currentTimeMillis();
		LOGGER.info("本次数据获取耗时：" + (end - start) + "ms");
		return result == 0 ? apiResult.error("") 
				: apiResult.ok("结果长度：" + result + "；耗时：" + (end - start) + "ms");
	}
	
	@RequestMapping("/redis/{row}-{col}.json")
	public @ResponseBody APIResult redis(@PathVariable("row") int row, @PathVariable("col") int col) {
		APIResult apiResult = APIResult.prepare();
		long start = System.currentTimeMillis();
		int result = dataService.redis(row, col);
		long end = System.currentTimeMillis();
		LOGGER.info("本次数据获取耗时：" + (end - start) + "ms");
		return result == 0 ? apiResult.error("") 
				: apiResult.ok("结果长度：" + result + "；耗时：" + (end - start) + "ms");
	}
	
	@RequestMapping("/health.json")
	public @ResponseBody String health() {
		return "Health Checked! " + TimeUtils.YYYYMMDDHHMMSS.format(new Date());
	}
	
}
