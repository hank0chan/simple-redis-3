package cache.redis;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cache.util.ArraysSliceUtils;
import cache.util.JdkSerializableUtils;
import cache.util.SnappyUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 通用的Jedis连接工具类
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 21:24:43 - 13 Feb 2017
 * @detail Snappy压缩及使用分片存储方式，经过测试，相比较于直接使用Snappy压缩实现并没有性能上的优势。
 */
public class JedisSnappySliceUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(JedisSnappySliceUtils.class);
	
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
			props.load(JedisSnappySliceUtils.class.getResourceAsStream(REDIS_CONFIG_FILE));
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
		// 序列化
		byte[] keyBytes = JdkSerializableUtils.serialize(key);
		byte[] valueBytes = JdkSerializableUtils.serialize(value);
		try {
			// 压缩
			byte[] keyCompressBytes = SnappyUtils.compress(keyBytes);
			byte[] valueCompressBytes = SnappyUtils.compress(valueBytes);
			
			// TODO 分片处理 
			// 设置分片键
			byte[] firstKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 1);
			byte[] secondKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 2);
			byte[] thirdKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 3);
			byte[] fourthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 4);
			byte[] fifthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 5);
			byte[] sixthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 6);
			byte[] seventhKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 7);
			byte[] eighthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 8);
			// 设置分片数据值
			List<byte[]> values = ArraysSliceUtils.fastSlice(valueCompressBytes, 8);
			byte[] firstValueCompressBytes = values.get(0);
			byte[] secondValueCompressBytes = values.get(1);
			byte[] thirdValueCompressBytes = values.get(2);
			byte[] fourthValueCompressBytes = values.get(3);
			byte[] fifthValueCompressBytes = values.get(4);
			byte[] sixthValueCompressBytes = values.get(5);
			byte[] seventhValueCompressBytes = values.get(6);
			byte[] eighthValueCompressBytes = values.get(7);
			
			// 存入Redis并设置指定的key的过期时间
			jedis.set(firstKeyCompressBytes, firstValueCompressBytes);
			jedis.expire(firstKeyCompressBytes, expireTime);
			jedis.set(secondKeyCompressBytes, secondValueCompressBytes);
			jedis.expire(secondKeyCompressBytes, expireTime);
			jedis.set(thirdKeyCompressBytes, thirdValueCompressBytes);
			jedis.expire(thirdKeyCompressBytes, expireTime);
			jedis.set(fourthKeyCompressBytes, fourthValueCompressBytes);
			jedis.expire(fourthKeyCompressBytes, expireTime);
			jedis.set(fifthKeyCompressBytes, fifthValueCompressBytes);
			jedis.expire(fifthKeyCompressBytes, expireTime);
			jedis.set(sixthKeyCompressBytes, sixthValueCompressBytes);
			jedis.expire(sixthKeyCompressBytes, expireTime);
			jedis.set(seventhKeyCompressBytes, seventhValueCompressBytes);
			jedis.expire(seventhKeyCompressBytes, expireTime);
			jedis.set(eighthKeyCompressBytes, eighthValueCompressBytes);
			jedis.expire(eighthKeyCompressBytes, expireTime);
			// 回收资源
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
		// 序列化
		byte[] keyBytes = JdkSerializableUtils.serialize(key);
		try {
			// 压缩
			byte[] keyCompressBytes = SnappyUtils.compress(keyBytes);
			// TODO 合并分片处理
			// 得到分片键
			byte[] firstKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 1);
			byte[] secondKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 2);
			byte[] thirdKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 3);
			byte[] fourthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 4);
			byte[] fifthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 5);
			byte[] sixthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 6);
			byte[] seventhKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 7);
			byte[] eighthKeyCompressBytes = ArraysSliceUtils.setArraysLastFlag(keyCompressBytes, 8);
			// 根据分片键获取数据
			byte[] firstValueCompressBytes = jedis.get(firstKeyCompressBytes);
			byte[] secondValueCompressBytes = jedis.get(secondKeyCompressBytes);
			byte[] thirdValueCompressBytes = jedis.get(thirdKeyCompressBytes);
			byte[] fourthValueCompressBytes = jedis.get(fourthKeyCompressBytes);
			byte[] fifthValueCompressBytes = jedis.get(fifthKeyCompressBytes);
			byte[] sixthValueCompressBytes = jedis.get(sixthKeyCompressBytes);
			byte[] seventhValueCompressBytes = jedis.get(seventhKeyCompressBytes);
			byte[] eighthValueCompressBytes = jedis.get(eighthKeyCompressBytes);
			if(firstValueCompressBytes == null || secondValueCompressBytes == null 
					|| thirdValueCompressBytes == null || fourthValueCompressBytes == null
					|| fifthValueCompressBytes == null || sixthValueCompressBytes == null
					|| seventhValueCompressBytes == null || eighthValueCompressBytes == null) {
				return null;
			}
			// 合并得到的分片数据
			byte[] valueCompressBytes = ArraysSliceUtils.fastMerge(firstValueCompressBytes, 
					secondValueCompressBytes, thirdValueCompressBytes, fourthValueCompressBytes, 
					fifthValueCompressBytes, sixthValueCompressBytes, seventhValueCompressBytes,
					eighthValueCompressBytes);
			// 解压缩
			byte[] valueBytes = SnappyUtils.uncompress(valueCompressBytes);
			// 反序列化
			T value = JdkSerializableUtils.unserialize(valueBytes);
			// 回收资源
			recycleJedis(jedis);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
