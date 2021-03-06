package cache.redis;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cache.util.JdkSerializableUtils;
import cache.util.LZ4Utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 通用的Jedis连接工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 21:24:43 - 13 Feb 2017
 * @detail
 */
/**
 * LZ4压缩方式的Jedis工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 00:16:11 - 19 Feb 2017
 * @detail
 */
public class JedisLZ4Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(JedisLZ4Utils.class);
	
	/** redis配置文件名，默认classpath目录下 */
	private static final String REDIS_CONFIG_FILE = "/redis.properties";
	/** 最大连接数 */
	private static final String REDIS_MAX_TATOL = "redis.maxTotal";
	/** 设置空间连接 */
	private static final String REDIS_MAX_IDLE = "redis.maxIdle";
	/** 最大阻塞时间 */
	private static final String MAX_WAIT_MILLIS = "redis.maxWaitMillis";
	/**  */
	private static final String REDIS_TEST_ON_BORROW = "redis.testOnBorrow";
	/**  */
	private static final String REDIS_TEST_ON_RETURN = "redis.testOnReturn";
	/** Redis服务器IP */
	private static final String REDIS_IP = "redis.ip";
	/** Redis服务端口号 */
	private static final String REDIS_PORT = "redis.port";
	
	/** Jedis池化管理 */
	private static JedisPool jedisPool;
	
	static {
		Properties props = new Properties();
		try {
			props.load(JedisLZ4Utils.class.getResourceAsStream(REDIS_CONFIG_FILE));
			JedisPoolConfig conf = new JedisPoolConfig();
			conf.setMaxTotal(Integer.valueOf(props.getProperty(REDIS_MAX_TATOL)));
			conf.setMaxIdle(Integer.valueOf(props.getProperty(REDIS_MAX_IDLE)));
			conf.setMaxWaitMillis(Integer.valueOf(props.getProperty(MAX_WAIT_MILLIS)));
			conf.setTestOnBorrow(Boolean.valueOf(props.getProperty(REDIS_TEST_ON_BORROW)));
			conf.setTestOnReturn(Boolean.valueOf(props.getProperty(REDIS_TEST_ON_RETURN)));
			jedisPool = new JedisPool(conf, props.getProperty(REDIS_IP), 
					Integer.valueOf(props.getProperty(REDIS_PORT)));
		} catch (IOException e) {
			LOGGER.error("加载redis.properties异常！");
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取Jedis实例
	 * @return
	 */
	public static Jedis getJedis() {
		return jedisPool.getResource();
	}
	
	/**
	 * Jedis资源回收
	 * @param jedis
	 */
	public static void recycleJedis(Jedis jedis) {
		jedis.close();
	}
	
	/**
	 * 加入缓存，并定义缓存时效
	 * @param key 缓存键
	 * @param value 缓存值
	 * @param expireTime 缓存时效
	 * @throws IOException 
	 */
	public static void put(Object key, Object value, int expireTime) {
		Jedis jedis = getJedis();
		byte[] keyBytes = JdkSerializableUtils.serialize(key);
		byte[] valueBytes = JdkSerializableUtils.serialize(value);
		try {
			byte[] keyCompressBytes = LZ4Utils.compress(keyBytes);
			byte[] valueCompressBytes = LZ4Utils.compress(valueBytes);
			jedis.set(keyCompressBytes, valueCompressBytes);
			// 设置指定的key的过期时间
			jedis.expire(keyCompressBytes, expireTime);
			recycleJedis(jedis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取缓存
	 * @param key 缓存键
	 * @return
	 * @throws IOException 
	 */
	public static <T> T get(Object key) {
		Jedis jedis = getJedis();
		byte[] keyBytes = JdkSerializableUtils.serialize(key);
		try {
			byte[] keyCompressBytes = LZ4Utils.compress(keyBytes);
			byte[] valueCompressBytes = jedis.get(keyCompressBytes);
			if(valueCompressBytes == null) {
				return null;
			}
			byte[] valueBytes = LZ4Utils.uncompress(valueCompressBytes);
			T value = JdkSerializableUtils.unserialize(valueBytes);
			recycleJedis(jedis);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
