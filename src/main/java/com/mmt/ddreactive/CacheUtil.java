package com.mmt.ddreactive;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisClusterException;

/**
 * @author MMT5387
 */
@Component
public class CacheUtil {

  @Value("#{'${jedis.cluster.host}'}")
  private List<String> jedisHost;

  private JedisCluster jedisCluster;

  @PostConstruct
  public void init() {
    Set<HostAndPort> hostAndPortNodeSet = new HashSet<HostAndPort>(jedisHost.size());
    for (int i = 0; i < jedisHost.size(); i++) {
      hostAndPortNodeSet.add(new HostAndPort(jedisHost.get(i).split(":")[0],
                                             Integer.parseInt(jedisHost.get(i).split(":")[1])));
    }
    jedisCluster = new JedisCluster(hostAndPortNodeSet);
  }

  public void writeInCache(String key, String value) {
    jedisCluster.set(key, value);
  }

  public void writeInCacheWithTTL(String key, String value, final int ttl) {
    jedisCluster.setex(key, ttl, value);
  }

  public void writeInCacheWithTTL(byte[] key, byte[] value, final int ttl) {
    jedisCluster.setex(key, ttl, value);
  }

  public void writeHMapSet(String hkey, Map<String, String> fieldKeyVal) {
    jedisCluster.hmset(hkey, fieldKeyVal);
  }

  public boolean keyExists(String key) {
    return jedisCluster.exists(key);
  }

  public String get(String key) {
    return jedisCluster.get(key);
  }

  public byte[] get(byte[] key) {
    return jedisCluster.get(key);
  }

  public List<String> getHMap(String hkey, String[] fields) {
    return jedisCluster.hmget(hkey, fields);
  }

  public List<byte[]> getHMap(byte[] hkey, byte[]... fields) {
    return jedisCluster.hmget(hkey, fields);
  }

  public List<String> mGet(String[] keys) {
    return jedisCluster.mget(keys);
  }

  public List<byte[]> mget(byte[][] keys){
    return keys.length > 0 ? jedisCluster.mget(keys) : null;
  }

  public void updateTTL(String key, int ttl) {
    jedisCluster.expire(key, ttl);
  }

  public void delKeysFromRedis(Set<String> keys) {
    try {
      jedisCluster.del(keys.toArray(new String[keys.size()]));
    } catch (JedisClusterException ex) {
      for (String key : keys) {
        jedisCluster.del(key);
      }
    }
  }

  public Set<String> scankeys(String pattern) {
    Set<String> keys = new HashSet<>();
    Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
    for (String k : clusterNodes.keySet()) {
      JedisPool jp = clusterNodes.get(k);
      Jedis connection = jp.getResource();
      try {
        keys.addAll(connection.keys(pattern));
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        connection.close();
      }
    }
    return keys;
  }

  public Map<String, String> hgetAll(String key) {
    return jedisCluster.hgetAll(key);
  }

}
