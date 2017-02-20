package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;
import cache.redis.JedisUtils;
import cache.service.SwiftCacheService;

/**
 * 尝试Redis缓存服务的第一种实现
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 18:00:44 - 13 Feb 2017
 * @detail 目前来看，在5个并发量的情况下该实现是最优的，平均耗时140ms。可靠性达到94%
 */
//@Service
public class JedisSwiftCacheServiceImpl implements SwiftCacheService {
	
	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return JedisUtils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		JedisUtils.put(cacheKey, result, expire);
	}

}
