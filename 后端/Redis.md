Redis

## 1. Linux安装

```bash
wget http://download.redis.io/releases/redis-6.2.6.tar.gz
tar -zxzf redis-6.2.6.tar.gz
cd redis-6.2.6

# 先安装c++
yum install gcc-c++

make

# 启动服务
cd src
./redis-server

# 以配置文件启动服务
./redis-server ../redis.conf

# 启动客户端
./redis-cli
redis-cli

# 配置文件中开启后台运行
daemonize yes

# 开启远程连接
# 配置密码
requirepass 12345678
# 修改bind可访问ip 如果全部允许 就把bind注释掉
bind 127.0.0.1
# 关闭保护模式
protected-mode no

# 有密码登录
redis-cli -a 12345678
# 或者
redis-cli
auth 12345678
```

或者宝塔无脑安装

## 2. 性能测试

```bash
redis-benchmark [option] [option value]
```

| 序号 | 选项                      | 描述                                       | 默认值    |
| :--- | :------------------------ | :----------------------------------------- | :-------- |
| 1    | **-h**                    | 指定服务器主机名                           | 127.0.0.1 |
| 2    | **-p**                    | 指定服务器端口                             | 6379      |
| 3    | **-s**                    | 指定服务器 socket                          |           |
| 4    | **-c**                    | 指定并发连接数                             | 50        |
| 5    | **-n**                    | 指定请求数                                 | 10000     |
| 6    | **-d**                    | 以字节的形式指定 SET/GET 值的数据大小      | 2         |
| 7    | **-k**                    | 1=keep alive 0=reconnect                   | 1         |
| 8    | **-r**                    | SET/GET/INCR 使用随机 key, SADD 使用随机值 |           |
| 9    | **-P**                    | 通过管道传输 <numreq> 请求                 | 1         |
| 10   | **-q**                    | 强制退出 redis。仅显示 query/sec 值        |           |
| 11   | **--csv**                 | 以 CSV 格式输出                            |           |
| 12   | ***-l\*（L 的小写字母）** | 生成循环，永久执行测试                     |           |
| 13   | **-t**                    | 仅运行以逗号分隔的测试命令列表。           |           |
| 14   | ***-I\*（i 的大写字母）** | Idle 模式。仅打开 N 个 idle 连接并等待。   |           |

## 3. 基本命令

```bash
# 默认有16个数据库
# 0-15
# 切换到0
select 0
# 数据库大小
dbsize
# 清空当前数据库
flushdb
# 清空所有数据库
flushall
# 查看所有key
keys *
# 设置key-value
set key1 aaa
# 根据key获取value
get key1
# 是否存在某个key
exists key1
# 将某个key移动到指定库中
move key1 0
# 删除某个key
del key1
# 设置过期时间x秒
expire key1 10
# 查看剩余时间
ttl key1
# 查看key的类型
type key1
```

## 4. String

```bash
# 如果不存在创建，如果存在在后面追加字符串
append key1 aaa
# 获取字符串长度
strlen key1
# key++如果是整数
incr key1
# key--如果是整数
decr key1
# key自增步长
incrby key1 10
# key自增步长
decrby key1 10
# 截取字符串 包头包尾 -1代表最后一个 -2代表倒数第二个
getrange key1 0 3
# 设置value并设置过期时间
setex key1 30 aaa
# 当key不存在时设置值
setnx key1 aaa
# 批量set
mset key1 v1 key2 v2
# 批量get
mget key1 key2
# 批量当key不存在时设置值，如果有一个失败，都不会成功，原子性的操作
msetnx key1 v1 key2 v2
# 先get后set，set并返回原来的值
getset key1 ddd
# 对象 json 的储存方式
set user:1 {name:aaa,age:13}
mset user:1:name aaa user:1:age 13
```

## 5. List

```bash
# 向list中存入元素，类似队列，先存入a，后存入的会把先存入的元素向后推
lpush list1 a b c d
# 向list尾部添加
rpush list1 a b c d
# 获取list指定索引的元素
lindex list1 1
# 获取list指定索引范围的元素
lrange list1 0 3
# 获取list长度
llen list1
# 移除list第一个元素
lpop list1
# 移除list最后一个元素
rpop list1
# 移除list指定个数的指定元素，指定元素从头到尾的顺序
lrem list1 2 a
# 截取list，范围之外的元素全部移除
ltrim list1 0 3
# 移除list最后一个元素并将它添加到另一个list的第一个
rpoplpush list1 list2
# 在list中指定索引更新元素，这个列表必须存在，这个索引必须存在元素
lset list1 0 a
# 向list中指定元素的前面或者后面添加元素，指定元素从头到尾的顺序
linsert list1 before|after newValue oldValue
# 向list从头添加元素，如果list存在的话
lpushx list1 a
# 向list从尾添加元素，如果list存在的话
rpushx list1 a
```

## 6. Set

```bash
# 向set中添加一个或多个元素
sadd set1 a [b]
# 查询set中所有元素
smembers set1
# 查询set中是否存在某个元素
sismember set1 a
# 获取set的元素个数
scard set1
# 删除set中的指定的一个或者多个元素
srem set1 a [b]
# 随机返回set中的一个或者n个元素
srandmember set1 [n]
# 在set中随机移除并返回一个元素
spop set1
# 移动set中的指定元素到另一个set
smove set1 set2 a
# 查看多个set之间的差集
sdiff set1 set2 set3
# 查看多个set之间的交集
sinter set1 set2 set3
# 查看多个set之间的并集
sunion set1 set2 set3
# 查看多个set之间的差集，并存到一个set中
sdiffstore dest set1 set2 set3
# 查看多个set之间的交集，并存到一个set中
sinterstore dest set1 set2 set3
# 查看多个set之间的并集，并存到一个set中
sunionstore dest set1 set2 set3
```

## 7. Hash

```bash
# 给hash中添加键值对
hset hash1 key1 aa
# 批量给hash中添加键值对
hmset hash1 key1 aa key2 bb
# 获取hash中指定key的值
hget hash1 key1
# 批量获取hash中指定key的值
hmget hash1 key1 key2
# 获取hash中所有键值对，以一行k一行v显示
hgetall hash1
# 删除hash中的某个键值对
hdel hash1 key1
# 获取hash中键值对数量
hlen hash1
# 在hash中某个key是否存在
hexists hash1 key1
# 获取hash中所有key
hkeys hash1
# 获取hash中所有value
hvals hash1
# 在hash中指定key自增，自减用负数
hincrby hash1 key1 3
# 在hash中如果不存在指定key可以设置
hsetnx hash1 key1 aa
# 对象 的储存方式
hset user:1 name aa age 13
```

## 8. Sorted Set

```bash
# 向zset指定大小添加一个或者多个元素
zadd zset1 1 a [2 b]
# 在zset中获取索引范围的元素[携带大小]
zrange zset1 0 -1 [withscores]
# 在zset中获取索引范围的元素[携带大小] 反过来
zrevrange zset1 0 -1 [withscores]
# 在zset中获取大小指定范围内从小到大的元素[携带大小] [个数]
zrangebyscore zset1 -inf +inf [withscores] [n]
# 在zset中获取大小指定范围内从大到小的元素[携带大小] [个数]
zrevrangebyscore zset1 +inf -inf [withscores] [n]
# 返回zset中元素个数
zcard zset1
# 返回zset中大小在指定范围内的元素个数
zcount zset1 -inf +inf
# 返回zset中指定元素的索引
zrank zset1 a
# 返回zset中指定元素的大小
zscore zset1 a
# 在zset中移除指定元素
zrem zset1 a
# 在zset中删除指定索引范围的元素
zremrangebyrank zset1 0 -1
# 在zset中删除指定大小范围内的元素
zremrangebyscore zset1 -inf +inf
```

## 9. Geospatial

需要填写经度纬度，可以下载城市数据用java批量导入

底层是zset，可以用zset的方法操作

```bash
# 添加geo地理位置，可以批量添加
geoadd china 121 31 shanghai [120 31 suzhou]
# 获取geo地理位置经度纬度，可以批量获取
geopos china shanghai [suzhou]
# 获取geo两个位置之间的距离，可以指定单位默认是米[米|千米|英里|英尺]
geodist china shanghai suzhou [m|km|mi|ft]
# 在geo中获取指定经度纬度位置的指定半径内的地理位置[米|千米|英里|英尺][携带经纬度][携带距离][携带52位hash][个数 n][升序|降序][储存名称和hash到zset key][储存名称和距离到zset key]
georadius key longitude latitude radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]
# 在geo中获取指定位置的指定半径内的地理位置[米|千米|英里|英尺][携带经纬度][携带距离][携带52位hash][个数 n][升序|降序][储存名称和hash到zset key][储存名称和距离到zset key]
georadiusbymember key member radius m|km|ft|mi [WITHCOORD] [WITHDIST] [WITHHASH] [COUNT count] [ASC|DESC] [STORE key] [STOREDIST key]
# 获取geo指定位置的11位geohash值
geohash china shanghai
```

## 10. Hyperloglog

获取基数=数据集中去重之后的个数

```bash
# 向hyperloglog中添加元素，可以批量
pfadd hyper1 a [b c]
# 在hyperloglog中统计基数，可以批量
pfcount hyper1 [hyper2]
# 将多个hyperloglog合并并储存到一个hyperloglog中
pfmerge desthyper hyper1 hyper2
```

## 11. Bitmap

位图储存，一个位只有0和1

应用比如，用户活跃数

```bash
# 设置bitmaps指定索引的0或者1
setbit bit1 0 1
# 获取bitmaps指定索引的0或者1
getbit bit1 0
# 统计bitmaps中是1的个数 [从 到]
bitcount bit1 [start end]
```

## 12. 事务

Redis 事务可以一次执行多个命令， 并且带有以下三个重要的保证：

- 批量操作在发送 EXEC 命令前被放入队列缓存。
- 收到 EXEC 命令后进入事务执行，事务中任意命令执行失败，其余的命令依然被执行。
- 在事务执行过程，其他客户端提交的命令请求不会插入到事务执行命令序列中。

一个事务从开始到执行会经历以下三个阶段：

- 开始事务。
- 命令入队。
- 执行事务。

单个 Redis 命令的执行是原子性的，但 Redis 没有在事务上增加任何维持原子性的机制，所以 Redis 事务的执行并不是原子性的。

事务可以理解为一个打包的批量执行脚本，但批量指令并非原子化的操作，中间某条指令的失败不会导致前面已做指令的回滚，也不会造成后续的指令不做。

```bash
# 开启事务
multi

...... # 命令

# 执行事务
exec

# 放弃事务
discard

# 监视 监视的key如果在监视之后事务执行之前发生更新事务将被打断 可以批量 可以实现乐观锁
watch key1 [key2]

...... # 事务

# 解除监视 无论事务是否成功都会自动解除
unwatch
```

## 13. Jedis

导入依赖

```xml
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>4.1.0</version>
</dependency>
```

开启连接

```java
Jedis jedis = new Jedis("47.100.36.90", 6379);
jedis.auth("Ytc19980211..");
System.out.println(jedis.ping());
```

使用，jedis.xxx与命令一样使用

事务

```java
try {
    multi.set("k1","v1");
    multi.set("k2","v2");
    multi.exec();
} catch (Exception e) {
    multi.discard();
    e.printStackTrace();
} finally {
    System.out.println(jedis.get("k1"));
    System.out.println(jedis.get("k2"));
    jedis.close();
}
```

关闭连接

```java
jedis.close();
```

## 14. Springboot整合

Springboot中与redis的整合在2.0后从Jedis替换为Lettuce

在创建时选择NoSQL中的Redis依赖或者手动导入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

`Jedis` : 采用的直连，多个线程操作的话，是不安全的，如果想要避免不安全，使用Jedis Pool连接池，更像 BIO 模式

`Lettuce` : 采用Netty，实例可以在多个线程中进行共享，不存在线程不安全的情况，可以减少线程数据，更像 NIO 模式

配置

```yaml
spring:
  redis:
    host: 47.100.36.90
    port: 6379
    password: Ytc19980211..
```

使用，再往后点就能操作了

```java
@Autowired
RedisTemplate redisTemplate;

@Test
void contextLoads() {
    redisTemplate.getConnectionFactory().getConnection().flushDb(); // 常用操作
        redisTemplate.opsForValue(); // bitmap跟string在一起
        redisTemplate.opsForList();
        redisTemplate.opsForHash();
        redisTemplate.opsForSet();
        redisTemplate.opsForZSet();
        redisTemplate.opsForGeo();
        redisTemplate.opsForHyperLogLog();
}
```

不需要手动关闭连接

set对象需要实现序列化Serializable接口

set对象可以通过转为json，也可以直接传递对象

可以通过自定义RedisTemplate配置具体的序列化方式，这样在控制台中就不会有转义过的key了

```java
@Configuration
public class RedisConfig {
    @Bean
    @SuppressWarnings("all") //压制警告
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        // 将template 泛型设置为 <String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate();
        // 连接工厂，不必修改
        template.setConnectionFactory(redisConnectionFactory);
        //Json序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        //String序列化配置
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 序列化设置
        // key、hash的key 采用 String序列化方式
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // value、hash的value 采用 Jackson 序列化方式
        template.setValueSerializer(RedisSerializer.json());
        template.setHashValueSerializer(RedisSerializer.json());
        //把所有配置设置进去
        template.afterPropertiesSet();
        return template;
    }
}
```

可以封装一下RedisUtil

```java
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类*/
public class RedisUtil {
    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    /** -------------------key相关操作--------------------- */

    /**
     * 删除key
     * 
     * @param key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 批量删除key
     * 
     * @param keys
     */
    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    /**
     * 序列化key
     * 
     * @param key
     * @return
     */
    public byte[] dump(String key) {
        return redisTemplate.dump(key);
    }

    /**
     * 是否存在key
     * 
     * @param key
     * @return
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置过期时间
     * 
     * @param key
     * @param timeout
     * @param unit
     * @return
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 设置过期时间
     * 
     * @param key
     * @param date
     * @return
     */
    public Boolean expireAt(String key, Date date) {
        return redisTemplate.expireAt(key, date);
    }

    /**
     * 查找匹配的key
     * 
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 将当前数据库的 key 移动到给定的数据库 db 当中
     * 
     * @param key
     * @param dbIndex
     * @return
     */
    public Boolean move(String key, int dbIndex) {
        return redisTemplate.move(key, dbIndex);
    }

    /**
     * 移除 key 的过期时间，key 将持久保持
     * 
     * @param key
     * @return
     */
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }

    /**
     * 返回 key 的剩余的过期时间
     * 
     * @param key
     * @param unit
     * @return
     */
    public Long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 返回 key 的剩余的过期时间
     * 
     * @param key
     * @return
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 从当前数据库中随机返回一个 key
     * 
     * @return
     */
    public String randomKey() {
        return redisTemplate.randomKey();
    }

    /**
     * 修改 key 的名称
     * 
     * @param oldKey
     * @param newKey
     */
    public void rename(String oldKey, String newKey) {
        redisTemplate.rename(oldKey, newKey);
    }

    /**
     * 仅当 newkey 不存在时，将 oldKey 改名为 newkey
     * 
     * @param oldKey
     * @param newKey
     * @return
     */
    public Boolean renameIfAbsent(String oldKey, String newKey) {
        return redisTemplate.renameIfAbsent(oldKey, newKey);
    }

    /**
     * 返回 key 所储存的值的类型
     * 
     * @param key
     * @return
     */
    public DataType type(String key) {
        return redisTemplate.type(key);
    }

    /** -------------------string相关操作--------------------- */

    /**
     * 设置指定 key 的值
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 获取指定 key 的值
     * @param key
     * @return
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 返回 key 中字符串值的子字符
     * @param key
     * @param start
     * @param end
     * @return
     */
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }

    /**
     * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)
     * 
     * @param key
     * @param value
     * @return
     */
    public String getAndSet(String key, String value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)
     * 
     * @param key
     * @param offset
     * @return
     */
    public Boolean getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 批量获取
     * 
     * @param keys
     * @return
     */
    public List<String> multiGet(Collection<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 设置ASCII码, 字符串'a'的ASCII码是97, 转为二进制是'01100001', 此方法是将二进制第offset位值变为value
     * 
     * @param key 位置
     * @param value
     *            值,true为1, false为0
     * @return
     */
    public boolean setBit(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 将值 value 关联到 key ，并将 key 的过期时间设为 timeout
     * 
     * @param key
     * @param value
     * @param timeout
     *            过期时间
     * @param unit
     *            时间单位, 天:TimeUnit.DAYS 小时:TimeUnit.HOURS 分钟:TimeUnit.MINUTES
     *            秒:TimeUnit.SECONDS 毫秒:TimeUnit.MILLISECONDS
     */
    public void setEx(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 只有在 key 不存在时设置 key 的值
     * 
     * @param key
     * @param value
     * @return 之前已经存在返回false,不存在返回true
     */
    public boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 用 value 参数覆写给定 key 所储存的字符串值，从偏移量 offset 开始
     * 
     * @param key
     * @param value
     * @param offset
     *            从指定位置开始覆写
     */
    public void setRange(String key, String value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    /**
     * 获取字符串的长度
     * 
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    /**
     * 批量添加
     * 
     * @param maps
     */
    public void multiSet(Map<String, String> maps) {
        redisTemplate.opsForValue().multiSet(maps);
    }

    /**
     * 同时设置一个或多个 key-value 对，当且仅当所有给定 key 都不存在
     * 
     * @param maps
     * @return 之前已经存在返回false,不存在返回true
     */
    public boolean multiSetIfAbsent(Map<String, String> maps) {
        return redisTemplate.opsForValue().multiSetIfAbsent(maps);
    }

    /**
     * 增加(自增长), 负数则为自减
     * 
     * @param key
     * @return
     */
    public Long incrBy(String key, long increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 
     * @param key
     * @return
     */
    public Double incrByFloat(String key, double increment) {
        return redisTemplate.opsForValue().increment(key, increment);
    }

    /**
     * 追加到末尾
     * 
     * @param key
     * @param value
     * @return
     */
    public Integer append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }

    /** -------------------hash相关操作------------------------- */

    /**
     * 获取存储在哈希表中指定字段的值
     * 
     * @param key
     * @param field
     * @return
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有给定字段的值
     * 
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 获取所有给定字段的值
     * 
     * @param key
     * @param fields
     * @return
     */
    public List<Object> hMultiGet(String key, Collection<Object> fields) {
        return redisTemplate.opsForHash().multiGet(key, fields);
    }

    public void hPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

    /**
     * 仅当hashKey不存在时才设置
     * 
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public Boolean hPutIfAbsent(String key, String hashKey, String value) {
        return redisTemplate.opsForHash().putIfAbsent(key, hashKey, value);
    }

    /**
     * 删除一个或多个哈希表字段
     * 
     * @param key
     * @param fields
     * @return
     */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 查看哈希表 key 中，指定的字段是否存在
     * 
     * @param key
     * @param field
     * @return
     */
    public boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * 
     * @param key
     * @param field
     * @param increment
     * @return
     */
    public Long hIncrBy(String key, Object field, long increment) {
        return redisTemplate.opsForHash().increment(key, field, increment);
    }

    /**
     * 为哈希表 key 中的指定字段的整数值加上增量 increment
     * 
     * @param key
     * @param field
     * @param delta
     * @return
     */
    public Double hIncrByFloat(String key, Object field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 获取所有哈希表中的字段
     * 
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表中字段的数量
     * 
     * @param key
     * @return
     */
    public Long hSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    /**
     * 获取哈希表中所有值
     * 
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    /**
     * 迭代哈希表中的键值对
     * 
     * @param key
     * @param options
     * @return
     */
    public Cursor<Entry<Object, Object>> hScan(String key, ScanOptions options) {
        return redisTemplate.opsForHash().scan(key, options);
    }

    /** ------------------------list相关操作---------------------------- */

    /**
     * 通过索引获取列表中的元素
     * 
     * @param key
     * @param index
     * @return
     */
    public String lIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取列表指定范围内的元素
     * 
     * @param key
     * @param start
     *            开始位置, 0是开始位置
     * @param end
     *            结束位置, -1返回所有
     * @return
     */
    public List<String> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 存储在list头部
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, String... value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().leftPushAll(key, value);
    }

    /**
     * 当list存在的时候才加入
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lLeftPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().leftPushIfPresent(key, value);
    }

    /**
     * 如果pivot存在,再pivot前面添加
     * 
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lLeftPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().leftPush(key, pivot, value);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lRightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, String... value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushAll(String key, Collection<String> value) {
        return redisTemplate.opsForList().rightPushAll(key, value);
    }

    /**
     * 为已存在的列表添加值
     * 
     * @param key
     * @param value
     * @return
     */
    public Long lRightPushIfPresent(String key, String value) {
        return redisTemplate.opsForList().rightPushIfPresent(key, value);
    }

    /**
     * 在pivot元素的右边添加值
     * 
     * @param key
     * @param pivot
     * @param value
     * @return
     */
    public Long lRightPush(String key, String pivot, String value) {
        return redisTemplate.opsForList().rightPush(key, pivot, value);
    }

    /**
     * 通过索引设置列表元素的值
     * 
     * @param key
     * @param index
     *            位置
     * @param value
     */
    public void lSet(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 移出并获取列表的第一个元素
     * 
     * @param key
     * @return 删除的元素
     */
    public String lLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * 
     * @param key
     * @param timeout
     *            等待时间
     * @param unit
     *            时间单位
     * @return
     */
    public String lBLeftPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(key, timeout, unit);
    }

    /**
     * 移除并获取列表最后一个元素
     * 
     * @param key
     * @return 删除的元素
     */
    public String lRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * 
     * @param key
     * @param timeout
     *            等待时间
     * @param unit
     *            时间单位
     * @return
     */
    public String lBRightPop(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPop(key, timeout, unit);
    }

    /**
     * 移除列表的最后一个元素，并将该元素添加到另一个列表并返回
     * 
     * @param sourceKey
     * @param destinationKey
     * @return
     */
    public String lRightPopAndLeftPush(String sourceKey, String destinationKey) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey);
    }

    /**
     * 从列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
     * 
     * @param sourceKey
     * @param destinationKey
     * @param timeout
     * @param unit
     * @return
     */
    public String lBRightPopAndLeftPush(String sourceKey, String destinationKey,
            long timeout, TimeUnit unit) {
        return redisTemplate.opsForList().rightPopAndLeftPush(sourceKey,
                destinationKey, timeout, unit);
    }

    /**
     * 删除集合中值等于value得元素
     * 
     * @param key
     * @param index
     *            index=0, 删除所有值等于value的元素; index>0, 从头部开始删除第一个值等于value的元素;
     *            index<0, 从尾部开始删除第一个值等于value的元素;
     * @param value
     * @return
     */
    public Long lRemove(String key, long index, String value) {
        return redisTemplate.opsForList().remove(key, index, value);
    }

    /**
     * 裁剪list
     * 
     * @param key
     * @param start
     * @param end
     */
    public void lTrim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * 获取列表长度
     * 
     * @param key
     * @return
     */
    public Long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /** --------------------set相关操作-------------------------- */

    /**
     * set添加元素
     * 
     * @param key
     * @param values
     * @return
     */
    public Long sAdd(String key, String... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * set移除元素
     * 
     * @param key
     * @param values
     * @return
     */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 移除并返回集合的一个随机元素
     * 
     * @param key
     * @return
     */
    public String sPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    /**
     * 将元素value从一个集合移到另一个集合
     * 
     * @param key
     * @param value
     * @param destKey
     * @return
     */
    public Boolean sMove(String key, String value, String destKey) {
        return redisTemplate.opsForSet().move(key, value, destKey);
    }

    /**
     * 获取集合的大小
     * 
     * @param key
     * @return
     */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * 判断集合是否包含value
     * 
     * @param key
     * @param value
     * @return
     */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取两个集合的交集
     * 
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sIntersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的交集
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sIntersect(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的交集存储到destKey集合中
     * 
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的交集存储到destKey集合中
     * 
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sIntersectAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取两个集合的并集
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, String otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * 获取key集合与多个集合的并集
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sUnion(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的并集存储到destKey中
     * 
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * key集合与多个集合的并集存储到destKey中
     * 
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sUnionAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 获取两个集合的差集
     * 
     * @param key
     * @param otherKey
     * @return
     */
    public Set<String> sDifference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(key, otherKey);
    }

    /**
     * 获取key集合与多个集合的差集
     * 
     * @param key
     * @param otherKeys
     * @return
     */
    public Set<String> sDifference(String key, Collection<String> otherKeys) {
        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    /**
     * key集合与otherKey集合的差集存储到destKey中
     * 
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long sDifference(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKey,
                destKey);
    }

    /**
     * key集合与多个集合的差集存储到destKey中
     * 
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long sDifference(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForSet().differenceAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 获取集合所有元素
     * 
     * @param key
     * @return
     */
    public Set<String> setMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 随机获取集合中的一个元素
     * 
     * @param key
     * @return
     */
    public String sRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    /**
     * 随机获取集合中count个元素
     * 
     * @param key
     * @param count
     * @return
     */
    public List<String> sRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取集合中count个元素并且去除重复的
     * 
     * @param key
     * @param count
     * @return
     */
    public Set<String> sDistinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }

    /**
     * 
     * @param key
     * @param options
     * @return
     */
    public Cursor<String> sScan(String key, ScanOptions options) {
        return redisTemplate.opsForSet().scan(key, options);
    }

    /**------------------zSet相关操作--------------------------------*/
    
    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     * 
     * @param key
     * @param value
     * @param score
     * @return
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 
     * @param key
     * @param values
     * @return
     */
    public Long zAdd(String key, Set<TypedTuple<String>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * 
     * @param key
     * @param values
     * @return
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     * 
     * @param key
     * @param value
     * @param delta
     * @return
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    /**
     * 返回元素在集合的排名,有序集合是按照元素的score值由小到大排列
     * 
     * @param key
     * @param value
     * @return 0表示第一位
     */
    public Long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 返回元素在集合的排名,按元素的score值由大到小排列
     * 
     * @param key
     * @param value
     * @return
     */
    public Long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 获取集合的元素, 从小到大排序
     * 
     * @param key
     * @param start
     *            开始位置
     * @param end
     *            结束位置, -1查询所有
     * @return
     */
    public Set<String> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取集合元素, 并且把score值也获取
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zRangeWithScores(String key, long start,
            long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * 根据Score值查询集合元素
     * 
     * @param key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    public Set<String> zRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从小到大排序
     * 
     * @param key
     * @param min
     *            最小值
     * @param max
     *            最大值
     * @return
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key,
            double min, double max) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
    }

    /**
     * 
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zRangeByScoreWithScores(String key,
            double min, double max, long start, long end) {
        return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max,
                start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Set<TypedTuple<String>> zReverseRangeWithScores(String key,
            long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start,
                end);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, double min,
            double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }

    /**
     * 根据Score值查询集合元素, 从大到小排序
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Set<TypedTuple<String>> zReverseRangeByScoreWithScores(
            String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key,
                min, max);
    }

    /**
     * 
     * @param key
     * @param min
     * @param max
     * @param start
     * @param end
     * @return
     */
    public Set<String> zReverseRangeByScore(String key, double min,
            double max, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max,
                start, end);
    }

    /**
     * 根据score值获取集合元素数量
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zCount(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }

    /**
     * 获取集合大小
     * 
     * @param key
     * @return
     */
    public Long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 获取集合大小
     * 
     * @param key
     * @return
     */
    public Long zZCard(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 获取集合中value元素的score值
     * 
     * @param key
     * @param value
     * @return
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 移除指定索引位置的成员
     * 
     * @param key
     * @param start
     * @param end
     * @return
     */
    public Long zRemoveRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().removeRange(key, start, end);
    }

    /**
     * 根据指定的score值的范围来移除成员
     * 
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Long zRemoveRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取key和otherKey的并集并存储在destKey中
     * 
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }

    /**
     * 
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zUnionAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForZSet()
                .unionAndStore(key, otherKeys, destKey);
    }

    /**
     * 交集
     * 
     * @param key
     * @param otherKey
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, String otherKey,
            String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey,
                destKey);
    }

    /**
     * 交集
     * 
     * @param key
     * @param otherKeys
     * @param destKey
     * @return
     */
    public Long zIntersectAndStore(String key, Collection<String> otherKeys,
            String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKeys,
                destKey);
    }

    /**
     * 
     * @param key
     * @param options
     * @return
     */
    public Cursor<TypedTuple<String>> zScan(String key, ScanOptions options) {
        return redisTemplate.opsForZSet().scan(key, options);
    }
}
```

## 15. Redis配置

1.配置文件unit单位对大小写不敏感

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70.png)

2.可以包含(配置)别的配置文件

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-16433569579652.png)

3.绑定IP

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-16433569630324.png)

4.是否开启保护模式(Redis防火墙)与端口号

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-16433569705556.png)

5.后台启动,默认为No
6.如果设置为后台启动,需要指定pid文件

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210213204157824.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70)

7.日志
8.输出的日志文件名

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335707136812.png)

9.默认数据库数量16
10.是否显示log 默认yes

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335707610914.png)

11.RDB持久化相关配置

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335708565716.png)

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335709269618.png)

12.主从复制

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335709955120.png)

13.安全相关

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335710503722.png)

```bash
#设置redis的密码可以通过命令行进行设置
config set requirepass 123456
auth 123456 #登录
config get requirepass
```

14.客户端相关
设置最大连接Redis客户端数量

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335715909424.png)

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335716455426.png)


redis 中的默认的过期策略是 volatile-lru

```bash
maxmemory-policy 六种方式
1、volatile-lru：只对设置了过期时间的key进行LRU（默认值）
2、allkeys-lru ： 删除lru算法的key
3、volatile-random：随机删除即将过期key
4、allkeys-random：随机删除
5、volatile-ttl ： 删除即将过期的
6、noeviction ： 永不过期，返回错误
使用命令设置内存淘汰策略:
config set maxmemory-policy noeviction
```

15.AOF持久化相关配置

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335719842628.png)

选不同步时,是由操作系统自己同步数据,速度最快

## 16. 持久化

**RDB(Redis DataBase)**

RDB其实就是把数据以快照的形式保存在磁盘上。

RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘。也是默认的持久化方式，这种方式是就是将内存中数据以快照的方式写入到二进制文件中,默认的文件名为dump.rdb

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164335974109730.png)

Redis会单独创建( fork )一个子进程来进行持久化，会先将数据写入到一个临时文件中，待持久化过程都结束了，再用这个临时文件替换上次持久化好的文件。整个过程中，主进程是不进行任何IO操作的。这就确保了极高的性能。如果需要进行大规模数据的恢复，且对于数据恢复的完整性不是非常敏感，那RDB方式要比AOF方式更加的高效。RDB的缺点是最后一次持久化后的数据可能丢失。

触发机制

1. save的规则满足的条件下,会触发RDB规则
2. 执行flushall命令,会触发RDB规则
3. redis关机时,会触发RDB规则

恢复机制
将rdb文件保存到,redis启动目录下就可以了,当redis启动时会去检查dump.rdb文件并恢复其中的数据

```bash
#通过命令查看rdb文件保存路径
config get dir

# 可以通过save或者bgsave主动保存
save # 阻塞方式
bgsave # 非阻塞方式
```

优点
大规模存储数据时,效率非常高

缺点

1. 需要一定的时间间隔进行保存操作
2. 如果redis意外宕机,最后一次修改的数据会丢失
3. fork进程会占用一定的内存空间

**AOF(Append Only File)**

AOF会将每一个收到的写命令都通过write函数追加到文件中

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336073237032.png)

以日志的形式来记录每个写操作，将Redis执行过的所有指令记录下来(读操作不记录），只许追加文件但不可以改写文件，redis启动之初会读取该文件重新构建数据，换言之，redis重启的话就根据日志文件的内容将写指令从前到后执行一次以完成数据的恢复工作
默认文件名:appendonly.aof

启动AOF持久化
在Redis的配置文件中AOF是默认不启用的,如果需要使用AOF持久化需要在redis.conf配置文件中修改

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210214170834411.png)

测试AOF
启动redis执行写的操作后会在当前目录下生成appendonly.aof文件

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336074712235.png)

vim打开appendonly.aof文件,会发现里面就是进行了一些操作的记录

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336075488437.png)

如果AOF文件有问题,会导致Redis启动出错

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210214172022613.png)

解决:
使用官方提供的AOF文件修复工具redis-check-aof

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210214172114842.png)

```java 
redis-check-aof --fix appendonly.aof #执行命令修复aof文件
```

重写规则说明
aof模式是对文件无限追加,会导致文件越来越大
在redis.conf配置文件中可以配置重写规则

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336081269541.png)

如果文件大于64m,会fork一个新进程来将我们的文件重写

优点
每一次修改都同步,保证了数据的完整性
默认情况下AOF会每隔1秒，通过一个后台线程执行一次fsync操作，最多丢失1秒钟的数据
缺点
对于同一份数据来说，AOF日志文件通常比RDB数据快照文件更大
AOF运行效率也比RDB慢

**扩展**
一、RDB持久化方式能够在指定的时间间隔内对你的数据进行快照存储

二、AOF持久化方式记录每次对服务器写的操作，当服务器重启的时候会重新执行这些命令来恢复原始的数据，AOF命令以Redis协议追加保存每次写的操作到文件末尾，Redis还能对AOF文件进行后台重写，使得AOF文件的体积不至于过大。

三、只做缓存，如果你只希望你的数据在服务器运行的时候存在，你也可以不使用任何持久化

四、同时开启两种持久化方式

在这种情况下，当redis重启的时候会优先载入AOF文件来恢复原始的数据，因为在通常情况下AOF文件保存的数据集要比RDB文件保存的数据集要完整

RDB的数据不实时，同时使用两者时服务器重启也只会找AOF文件，那要不要只使用AOF呢?作者建议不要，因为RDB更适合用于备份数据库（AOF在不断变化不好备份），快速重启，而且不会有AOF可能潜在的Bug，留着作为一个万一的手段。

五、性能建议

因为RDB文件只用作后备用途，建议只在Slave(从机)上持久化RDB文件，而且只要15分钟备份一次就够了，只保留save 900 1这条规则。

如果Enable AOF，好处是在最恶劣情况下也只会丢失不超过两秒数据，启动脚本较简单只load自己的AOF文件就可以了，代价一是带来了持续的I0，二是AOF rewrite 的最后将rewrite 过程中产生的新数据写到新文件造成的阻塞几乎是不可避免的。只要硬盘许可，应该尽量减少AOF rewrite的频率，AOF重写的基础大小默认值64M太小了，可以设到5G以上，默认超过原大小100%大小重写可以改到适当的数值。

如果不Enable AOF，仅靠Master-Slave Repllcation(主从复制)实现高可用性也可以，能省掉一大笔IO，也减少了rewrite时带来的系统波动。代价是如果Master/Slave同时倒掉，会丢失十几分钟的数据，启动脚本也要比较两个Master/Slave 中的RDB文件，载入较新的那个，微博就是这种架构。

## 17. 发布订阅

Redis 发布订阅(publsub)是一种消息通信模式︰发送者(pub)发送消息，订阅者(sub)接收消息。
Redis 客户端可以订阅任意数量的频道。
![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336179485743.png)
发布订阅命令:
![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336179485844.png)
原理:
![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336179485845.png)
使用场景:

1. 实时消息系统
2. 多人聊天室

## 18. 主从复制

概念
 主从复制，是指将一台Redis服务器的数据，复制到其他的Redis服务器。前者称为主节点(Master/Leader),后者称为从节点(Slave/Follower),数据的复制是单向的！只能由主节点复制到从节点(主节点以写为主、从节点以读为主)。
默认情况下，每台Redis服务器都是主节点，一个主节点可以有0个或者多个从节点，但每个从节点只能由一个主节点。

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NjY4NDA5OQ==,size_16,color_FFFFFF,t_70-164336267937149.png)

作用
数据冗余：主从复制实现了数据的热备份，是持久化之外的一种数据冗余的方式。
故障恢复：当主节点故障时，从节点可以暂时替代主节点提供服务，是一种服务冗余的方式
负载均衡：在主从复制的基础上，配合读写分离，由主节点进行写操作，从节点进行读操作，分担服务器的负载；尤其是在多读少写的场景下，通过多个从节点分担负载，提高并发量。
高可用(集群)基石：主从复制还是哨兵和集群能够实施的基础。
环境配置
1.只配置从库,不用配置主库
通过命令查看当前库的信息

```bash
info replication
```

![saddafgsgdf](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/saddafgsgdf.png)

2.复制三个配置文件

```bash
cp redis.conf redis80.conf
```

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210216230949174.png)

3.修改配置文件

```bash
vim redis80.conf
port 6380 #修改端口号
pidfile /var/run/redis_6380.pid #修改指定pid文件
logfile "6380.log" #修改输出日志文件名
dbfilename dump6380.rdb #修改持久化文件名
```

同样的手法修改三个配置文件

4.启动测试

```bash
redis-server tuYoooConfig/redis.conf 
redis-server tuYoooConfig/redis80.conf 
redis-server tuYoooConfig/redis81.conf 
```

![202102162320dfds0354](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/202102162320dfds0354.png)

**一主二从**

默认情况下,每一台Redis服务器都是主节点,我们一般只用配置从机

如果主机有密码，需要给从机设置设置masterauth，用命令配置或者在配置文件中配置

```bash
进入redis_cli 客户端，相应执行以下命令
1.config set masterauth   密码
2.config set requirepass   密码
3.重新连接客户端，这是就需要将密码带上 auth 密码
4.config rewrite 可以将config set持久化到Redis配置文件中
```

命令:

```bash
redis-cli -p 6380 #登录从机客户端
slaveof 127.0.0.1 6379 #设置6379为主机
```

同样的手法设置81Redis服务器
![thytreddfdgrfd](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/thytreddfdgrfd.png)

通过命令设置的主从节点具有暂时性,从机重启以后会恢复成主机

永久设置从机参考本文>>Redis.conf配置文件详解>>主从复制

**细节**

1.从机不能写操作,只能读取操作
2.主机设置的值,会被从机自动保存
3.如果主机宕机,从机依旧在连接主机只是没有写操作了
4.从机宕机重启以后,依旧可以看到主机设置的全部数据

如果主机宕机,手动选举一个从机当主机

```bash
slaveof no one #从机恢复成主机
```

**主从数据复制原理**

Slave启动成功连接到master后会发送一个sync同步命令
Master接到命令，启动后台的存盘进程，同时收集所有接收到的用于修改数据集命令，在后台进程执行完毕之后，master将传送整个数据文件到slave，并完成一次完全同步。
全量复制 : 而slave服务在接收到数据库文件数据后，将其存盘并加载到内存中。
增量复制 : Master继续将新的所有收集到的修改命令依次传给slave，完成同步但是只要是重新连接master，一次完全同步(全量复制）将被自动执行

## 19. 哨兵模式

**概述**
在主从复制中,主机宕机以后主从关系依旧维持原来的状态

主从切换技术的方法是︰当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费事费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，我们优先考虑哨兵模式。Redis从2.8开始正式提供了
Sentinel (哨兵）架构来解决这个问题。

哨兵能够后台监控主机是否故障，如果故障了根据投票数自动将从库转换为主库。

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是一个独立的进程，作为进程，它会独立运行。其原理是哨兵通过发送命令，等待Redis服务器响应，从而监控运行的多个Redis实例。

![asdgaergrdf](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/asdgaergrdf.png)

**作用**
通过发送命令，让Redis服务器返回监控其运行状态，包括主服务器和从服务器。

当哨兵监测到master宕机，会自动将slave切换成master，然后通过发布订阅模式通知其他的从服务器，修改配置文件，让它们切换主机.

然而一个哨兵进程对Redis服务器进行监控，可能会出现问题，为此，我们可以使用多个哨兵进行监控。各个哨兵之间还会进行监控，这样就形成了多哨兵模式。

**多哨兵模式**

![asgdadfbfgsfhhthhrh](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/asgdadfbfgsfhhthhrh.png)

假设主服务器宕机，哨兵1先检测到这个结果，系统并不会马上进行failover过程，仅仅是哨兵1主观的认为主服务器不可用，这个现象成为主观下线。当后面的哨兵也检测到主服务器不可用，并且数量达到一定值时，那么哨兵之间就会进行一次投票，投票的结果由一个哨兵发起，进行failover[故障转移]操作。切换成功后，就会通过发布订阅模式，让各个哨兵把自己监控的从服务器实现切换主机，这个过程称为客观下线。

**哨兵模式的基础配置**

目前的状态是一主二从

1.配置sentinel.conf配置文件

```bash
cd /usr/local/bin/tuYoooConfig
vim sentinel.conf # 创建哨兵配置文件
sentinel monitor myredis 127.0.0.1 6379 1 # myredis是被监控名称
```

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210217110954162.png)

2.启动哨兵

集群启动多个哨兵

```bash
cd /usr/local/bin
redis-sentinel tuYoooConfig/sentinel.conf # 启动哨兵
```

![assdfasdfasdfasd](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/assdfasdfasdfasd.png)

![在这里插入图片描述](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/20210217111653126.png)

**哨兵模式的优缺点**
优点:

哨兵集群，基于主从复制模式，所有的主从配置优点，它全有
主从可以切换，故障可以转移，系统的可用性就会更好
哨兵模式就是主从模式的升级，手动到自动，更加健壮!
缺点∶

Redis 不好啊在线扩容的，集群容量一旦到达上限，在线扩容就十分麻烦!
实现哨兵模式的配置其实是很麻烦的，里面有很多选择!

**哨兵配置**

```bash
# Example sentinel.conf
 
# 哨兵sentinel实例运行的端口 默认26379
port 26379
 
# 哨兵sentinel的工作目录
dir /tmp
 
# 哨兵sentinel监控的redis主节点的 ip port 
# master-name  可以自己命名的主节点名字 只能由字母A-z、数字0-9 、这三个字符".-_"组成。
# quorum 当这些quorum个数sentinel哨兵认为master主节点失联 那么这时 客观上认为主节点失联了
# sentinel monitor <master-name> <ip> <redis-port> <quorum>
sentinel monitor mymaster 127.0.0.1 6379 1
 
# 当在Redis实例中开启了requirepass foobared 授权密码 这样所有连接Redis实例的客户端都要提供密码
# 设置哨兵sentinel 连接主从的密码 注意必须为主从设置一样的验证密码
# sentinel auth-pass <master-name> <password>
sentinel auth-pass mymaster MySUPER--secret-0123passw0rd
 
 
# 指定多少毫秒之后 主节点没有应答哨兵sentinel 此时 哨兵主观上认为主节点下线 默认30秒
# sentinel down-after-milliseconds <master-name> <milliseconds>
sentinel down-after-milliseconds mymaster 30000
 
# 这个配置项指定了在发生failover主备切换时最多可以有多少个slave同时对新的master进行 同步，
#这个数字越小，完成failover所需的时间就越长，
#但是如果这个数字越大，就意味着越 多的slave因为replication而不可用。
#可以通过将这个值设为 1 来保证每次只有一个slave 处于不能处理命令请求的状态。
# sentinel parallel-syncs <master-name> <numslaves>
sentinel parallel-syncs mymaster 1
 
 
 
# 故障转移的超时时间 failover-timeout 可以用在以下这些方面： 
#1. 同一个sentinel对同一个master两次failover之间的间隔时间。
#2. 当一个slave从一个错误的master那里同步数据开始计算时间。直到slave被纠正为向正确的master那里同步数据时。
#3.当想要取消一个正在进行的failover所需要的时间。  
#4.当进行failover时，配置所有slaves指向新的master所需的最大时间。不过，即使过了这个超时，slaves依然会被正确配置为指向master，但是就不按parallel-syncs所配置的规则来了
# 默认三分钟
# sentinel failover-timeout <master-name> <milliseconds>
sentinel failover-timeout mymaster 180000
 
# SCRIPTS EXECUTION
 
#配置当某一事件发生时所需要执行的脚本，可以通过脚本来通知管理员，例如当系统运行不正常时发邮件通知相关人员。
#对于脚本的运行结果有以下规则：
#若脚本执行后返回1，那么该脚本稍后将会被再次执行，重复次数目前默认为10
#若脚本执行后返回2，或者比2更高的一个返回值，脚本将不会重复执行。
#如果脚本在执行过程中由于收到系统中断信号被终止了，则同返回值为1时的行为相同。
#一个脚本的最大执行时间为60s，如果超过这个时间，脚本将会被一个SIGKILL信号终止，之后重新执行。
 
#通知型脚本:当sentinel有任何警告级别的事件发生时（比如说redis实例的主观失效和客观失效等等），将会去调用这个脚本，
#这时这个脚本应该通过邮件，SMS等方式去通知系统管理员关于系统不正常运行的信息。调用该脚本时，将传给脚本两个参数，
#一个是事件的类型，
#一个是事件的描述。
#如果sentinel.conf配置文件中配置了这个脚本路径，那么必须保证这个脚本存在于这个路径，并且是可执行的，否则sentinel无法正常启动成功。
#通知脚本
# sentinel notification-script <master-name> <script-path>
sentinel notification-script mymaster /var/redis/notify.sh
 
# 客户端重新配置主节点参数脚本
# 当一个master由于failover而发生改变时，这个脚本将会被调用，通知相关的客户端关于master地址已经发生改变的信息。
# 以下参数将会在调用脚本时传给脚本:
# <master-name> <role> <state> <from-ip> <from-port> <to-ip> <to-port>
# 目前<state>总是“failover”,
# <role>是“leader”或者“observer”中的一个。 
# 参数 from-ip, from-port, to-ip, to-port是用来和旧的master和新的master(即旧的slave)通信的
# 这个脚本应该是通用的，能被多次调用，不是针对性的。
# sentinel client-reconfig-script <master-name> <script-path>
sentinel client-reconfig-script mymaster /var/redis/reconfig.sh
```

## 20. 缓存穿透与雪崩

**缓存穿透**
概念
缓存穿透的概念很简单，用户想要查询一个数据，发现redis内存数据库没有，也就是缓存没有命中，于是向持久层数据库查询。发现也没有，于是本次查询失败。当用户很多的时候，缓存都没有命中，于是都去请求了持久层数据库。这会给持久层数据库造成很大的压力，这时候就相当于出现了缓存穿透。

解决方案
布隆过滤器
布隆过滤器是一种数据结构，对所有可能查询的参数以hash形式存储，在控制层先进行校验，不符合则丢弃，从而避免了对底层存储系统的查询压力

![Cvxcvzxvzxvfd](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/Cvxcvzxvzxvfd.png)

缓存空对象
当存储层不命中后，即使返回的空对象也将其缓存起来，同时会设置一个过期时间，之后再访问这个数据将会从缓存中获取，保护了后端数据源

![adfajkhlsdhfiayhf](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/adfajkhlsdhfiayhf.png)

但是这种方法会存在两个问题:

如果空值能够被缓存起来，这就意味着缓存需要更多的空间存储更多的键，因为这当中可能会有很多的空值的键;
即使对空值设置了过期时间，还是会存在缓存层和存储层的数据会有一段时间窗口的不一致，这对于需要保持一致性的业务会有影响。

**缓存击穿**
概念
这里需要注意和缓存击穿的区别，缓存击穿，是指一个key非常热点，在不停的扛着大并发，大并发集中对这一个点进行访问，当这个key在失效的瞬间，持续的大并发就穿破缓存，直接请求数据库，就像在一个屏障上凿开了一个洞。
当某个key在过期的瞬间，有大量的请求并发访问，这类数据一般是热点数据，由于缓存过期，会同时访问数据库来查询最新数据，并且回写缓存，会导使数据库瞬间压力过大。

解决方案
设置热点数据永不过期
从缓存层面来看，没有设置过期时间，所以不会出现热点key过期后产生的问题。

加互斥锁setnx
分布式锁:使用分布式锁，保证对于每个key同时只有一个线程去查询后端服务，其他线程没有获得分布式锁的权限，因此只需要等待即可。这种方式将高并发的压力转移到了分布式锁，因此对分布式锁的考验很大。

**缓存雪崩**
概念
缓存雪崩，是指在某一个时间段，缓存集中过期失效或Redis宕机!
产生雪崩的原因之一，比如马上就要到双十二零点，很快就会迎来一波抢购，这波商品时间比较集中的放入了缓存，假设缓存一个小时。那么到了凌晨一点钟的时候，这批商品的缓存就都过期了。而对这批商品的访问查询，都落到了数据库上，对于数据库而言，就会产生周期性的压力波峰。于是所有的请求都会达到存储层，存储层的调用量会暴增，造成存储层也会挂掉的情况。

![qwerskfkghd](https://ytc-picgo.oss-cn-shanghai.aliyuncs.com/qwerskfkghd.png)其实集中过期，倒不是非常致命，比较致命的缓存雪崩，是缓存服务器某个节点宕机或断网。因为自然形成的缓存雪崩，一定是在某个时间段集中创建缓存，这个时候，数据库也是可以顶住压力的。无非就是对数据库产生周期性的压力而已。而缓存服务节点的宕机，对数据库服务器造成的压力是不可预知的，很有可能瞬间就把数据库压垮。

解决方案
redis高可用
这个思想的含义是，既然redis有可能挂掉，那我多增设几台redis，这样一台挂掉之后其他的还可以继续工作，其实就是搭建的集群。（异地多活!)

限流降级
这个解决方案的思想是，在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。

数据预热
数据加热的含义就是在正式部署之前，我先把可能的数据先预先访问一遍，这样部分可能大量访问的数据就会加载到缓存中。在即将发生大并发访问前手动触发加载缓存不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀。
