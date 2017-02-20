package cache.service.sample;

import java.io.Serializable;

import org.springframework.stereotype.Service;

import cache.service.SwiftCacheService;
import concurrent.kryo.KryoUtils;

/**
 * 尝试Redis缓存服务的第三种实现
 * @author hankChan
 * @Email hankchan101@gmail.com
 * @time 18:00:44 - 13 Feb 2017
 * @detail
 */
//@Service
public class KryoSwiftCacheServiceImpl implements SwiftCacheService {
	
	@Override
	public Serializable get(String cacheName, String cacheKey) {
		return KryoUtils.get(cacheKey);
	}

	@Override
	public void put(String cacheName, int expire, String cacheKey, Serializable result) {
		// expire是缓存时效，单位：秒
		// TODO 加入缓存时效
		KryoUtils.put(cacheKey, result, expire);
	}

}
