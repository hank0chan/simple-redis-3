package concurrent.kryo;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Jedis的Kryo序列化方式的缓存工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 21:24:43 - 13 Feb 2017
 * @detail
 */
public class KryoUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(KryoUtils.class);
	
	/** redis配置文件名，默认classpath目录下 */
	private static final String REDIS_CONFIG_FILE = "/redis.properties";
	/**  */
	private static final String REDIS_MAX_TATOL = "redis.maxTotal";
	/**  */
	private static final String REDIS_MAX_IDLE = "redis.maxIdle";
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
			props.load(KryoUtils.class.getResourceAsStream(REDIS_CONFIG_FILE));
			JedisPoolConfig conf = new JedisPoolConfig();
			conf.setMaxTotal(Integer.valueOf(props.getProperty(REDIS_MAX_TATOL)));
			conf.setMaxIdle(Integer.valueOf(props.getProperty(REDIS_MAX_IDLE)));
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
	 */
	public static void put(Object key, Object value, int expireTime) {
		Jedis jedis = getJedis();
		// TODO 更换序列化工具
		jedis.set(KryoSerializableUtils.serialize(key), KryoSerializableUtils.serialize(value));
		// 设置指定的key的过期时间
		jedis.expire(KryoSerializableUtils.serialize(key), expireTime);
		recycleJedis(jedis);
	}
	
	/**
	 * 获取缓存
	 * @param key 缓存键
	 * @return
	 */
	public static <T> T get(Object key) {
		Jedis jedis = getJedis();
		// TODO 更换序列化工具
		byte[] valueBytes = jedis.get(KryoSerializableUtils.serialize(key));
		if(valueBytes == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		T value = (T) KryoSerializableUtils.unserialize(valueBytes);
		recycleJedis(jedis);
		return value;
	}
	
}
