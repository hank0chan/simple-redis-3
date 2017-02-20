# simple-redis-3
最终版本-基于Redis的自定义缓存实现，包括多个实现版本（Redis普通实现；Snappy压缩实现；Snappy压缩及数据分片实现）。提供对应的日志分析
### 使用说明
#### 1. 将swift-redis代码导入Eclipse中。
#### 2. 修改Redis地址：在配置文件redis.properties修改
#### 3. 在cache.service.sample包中选择其中一种实现方式，在实现类上加上@Service注解启用该实现方式（注意检查该包下只能有一种实现方式）。
#### 4. 在tomcat下启动运行即可。