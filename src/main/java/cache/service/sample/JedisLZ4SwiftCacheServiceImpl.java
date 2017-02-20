package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import cache.redis.JedisLZ4Utils;
import cache.service.SwiftCacheService;

/**
 * 尝试Redis缓存服务的LZ4压缩实现
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 00:15:42 - 19 Feb 2017
 * @detail 通过使用LZ4压缩方式的实现，在相同条件下，响应处理效率不如Snappy压缩方式的实现。
 * <h1>弃用。10或16个并发条件下效率大大低于Snappy实现方式
 */
//@Service
@Deprecated
public class JedisLZ4SwiftCacheServiceImpl implements SwiftCacheService {
	
	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return JedisLZ4Utils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		JedisLZ4Utils.put(cacheKey, result, expire);
	}

}
