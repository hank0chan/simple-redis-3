package cache.concurrent.redis;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

public class ShardedJedisPoolTest {

	/**
	 * 废弃，耗时15000ms量级
	 */
	/*@SuppressWarnings("deprecation")
	@Test
	public void test() {
		List<JedisShardInfo> shards = Arrays.asList(
				new JedisShardInfo("127.0.0.1", 6379),
				new JedisShardInfo("127.0.0.1", 6380));
		ShardedJedisPool pool = new ShardedJedisPool(new JedisPoolConfig(), shards);
		ShardedJedis one = pool.getResource();
		
		ShardedJedisPipeline pipeline = one.pipelined();
		
		long start = System.currentTimeMillis();
		for(int i = 0; i < 2000; i++) {
			for(int j = 0; j < 2000; j++) {
				pipeline.set("spn" + i + j, "n" + i + j);
			}
		}
		List<Object> results = pipeline.syncAndReturnAll();
		long end = System.currentTimeMillis();
		pool.returnResource(one);
		System.out.println("Simple@Pool SET: " + (end - start) + "ms");
		pool.destroy();
	}*/
}
