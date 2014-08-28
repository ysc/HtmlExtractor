/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.extractor.html.server.redis;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis监听器
 *
 * @author 杨尚川
 */
public class RedisListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisListener.class);
    public static JedisPool jedisPool;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String redisHost = sc.getInitParameter("redis.host");
        String redisPort = sc.getInitParameter("redis.port");
        LOGGER.info("redis.host: " + redisHost);
        LOGGER.info("redis.port: " + redisPort);
        LOGGER.info("开始初始化JedisPool");
        try {
            JedisPoolConfig jedispool_config = new JedisPoolConfig();
            jedisPool = new JedisPool(jedispool_config, redisHost, Integer.parseInt(redisPort));
            LOGGER.info("初始化JedisPool成功");
        } catch (Exception e) {
            LOGGER.error("初始化JedisPool失败", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        jedisPool.destroy();
        LOGGER.info("关闭JedisPool");
    }
}
