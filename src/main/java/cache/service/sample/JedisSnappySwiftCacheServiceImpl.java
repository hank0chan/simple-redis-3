package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import cache.redis.JedisSnappyUtils;
import cache.service.SwiftCacheService;

/**
 * 尝试Redis缓存服务的Snappy压缩实现
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 18:00:44 - 13 Feb 2017
 * @detail 通过使用Snappy压缩方式的实现，在高并发的场景，如：10，16个并发量的缓存处理。
 * 响应时间都非常稳定，不会出现响应速度不稳定的情况。10个并发平均响应时间328ms。
 * 16个并发量的处理最高响应时间基本不会超过1000ms。
 * 但是并发请求数低于5个的时候响应处理时间不如普通序列化的实现方式。
 */
//@Service
public class JedisSnappySwiftCacheServiceImpl implements SwiftCacheService {
	
	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return JedisSnappyUtils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		JedisSnappyUtils.put(cacheKey, result, expire);
	}

}
