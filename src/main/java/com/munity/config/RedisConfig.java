package com.munity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * spring-data-redis 的 RedisTemplate<K, V>模板类 在操作redis时默认使用JdkSerializationRedisSerializer 来进行序列化。
 * spring操作redis是在jedis客户端基础上进行的，而jedis客户端与redis交互的时候协议中定义是用byte类型交互，看到spring-data-redis中RedisTemplate<K, V>在操作的时候k，v是泛型对象，而不是byte[]类型的，
 * 这样导致的一个问题就是，如果不对RedisTemplate进行设置，spring会默认采用defaultSerializer = new JdkSerializationRedisSerializer();这个方法来对key、value进行序列化操作，JdkSerializationRedisSerializer它使用的编码是ISO-8859-1
 JDK自带的ObjectOutPutStream将我们的String对象序列化成了byte[]
 * redis 序列化踩坑，当写了reids config文件之后 序列化没有按指定的方式生成，说明该配置类没有生效，应该是springboot自动装配出现了问题
 * 看源码发现@ConditionalOnMissingBean(name = {"redisTemplate"})这里做了判断的，如果spring容器里面存在就不执行下面的代码，由于自定义是我们方法名不符合，不是redisTemplate,所以在自动配置的时候，发现容器里面没有，就会在下面代码中创建一个RedisTemplate，就导致了我们自定义的没有作用
 *
 * 把我们的方法名改为redisTemplate就可以了
 *
 * @ConditionalOnBean:如果容器里面存在这个Bean，则执行
 *
 * @ConditionalOnMissingBean:如果容器里面不存在这个Bean，则执行（自动配置常用）
 *
 *@Configuration(
 *     proxyBeanMethods = false
 * )
 * @ConditionalOnClass({RedisOperations.class})
 * @EnableConfigurationProperties({RedisProperties.class})
 * @Import({LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class})
 * public class RedisAutoConfiguration {
 *     public RedisAutoConfiguration() {
 *     }
 *
 *     @Bean
 *     @ConditionalOnMissingBean(
 *         name = {"redisTemplate"}
 *     )
 *     @ConditionalOnSingleCandidate(RedisConnectionFactory.class)
 *     public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
 *         RedisTemplate<Object, Object> template = new RedisTemplate();
 *         template.setConnectionFactory(redisConnectionFactory);
 *         return template;
 *     }
 * }
 */


@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnMissingBean(
            name = {"redisTemplate"}
    )
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 设置key的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // 设置value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // 设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }

}
