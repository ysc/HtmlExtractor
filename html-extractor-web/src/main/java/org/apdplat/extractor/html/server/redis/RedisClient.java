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

import static org.apdplat.extractor.html.server.redis.RedisListener.jedisPool;

import redis.clients.jedis.Jedis;

/**
 * 通知从节点抽取规则发生变化
 *
 * @author 杨尚川
 */
public class RedisClient {
    /**
     * 当抽取规则发生变化的时候
     * 向Redis服务器Channel：pr发送消息CHANGE
     * 从节点就会重新初始化抽取规则
     */
    public void extractRegularChange() {
        String message = "CHANGE";
        Jedis jedis = jedisPool.getResource();
        jedis.publish("pr", message);
        jedisPool.returnResource(jedis);
    }
}
