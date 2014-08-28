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

package org.apdplat.extractor.html;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apdplat.extractor.html.model.CssPath;
import org.apdplat.extractor.html.model.ExtractFunction;
import org.apdplat.extractor.html.model.HtmlTemplate;
import org.apdplat.extractor.html.model.UrlPattern;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * URL抽取规则 
 * 订阅Redis服务器Channel：pr，当规则改变的时候会收到通知消息CHANGE并重新初始化规则集合 
 * 初始化：
 * 1、从配置管理web服务器获取完整的规则集合 
 * 2、抽取规则 
 * 3、构造规则查找结构
 *
 * @author 杨尚川
 *
 */
public class ExtractRegular {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractRegular.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static ExtractRegular extractRegular = null;
    private volatile Map<String, List<UrlPattern>> urlPatternMap = null;

    /**
     * 私有构造函数
     */
    private ExtractRegular() {
    }
    /**
     * 获取抽取规则实例
     * @param urlPatterns url模式列表
     * @return 抽取规则实例
     */
    public static ExtractRegular getInstance(List<UrlPattern> urlPatterns){
        if (extractRegular != null) {
            return extractRegular;
        }
        synchronized (ExtractRegular.class) {
            if (extractRegular == null) {
                extractRegular = new ExtractRegular();
                //初始化抽取规则
                extractRegular.init(urlPatterns);
            }
        }
        return extractRegular;
    }
    /**
     * 获取抽取规则实例
     *
     * @param serverUrl 配置管理WEB服务器的抽取规则下载地址
     * @param redisHost Redis服务器主机
     * @param redisPort Redis服务器端口
     * @return 抽取规则实例
     */
    public static ExtractRegular getInstance(String serverUrl, String redisHost, int redisPort) {
        if (extractRegular != null) {
            return extractRegular;
        }
        synchronized (ExtractRegular.class) {
            if (extractRegular == null) {
                extractRegular = new ExtractRegular();
                //订阅Redis服务器Channel：pr，当规则改变的时候会收到通知消息CHANGE并重新初始化规则集合
                extractRegular.subscribeRedis(redisHost, redisPort, serverUrl);
                //初始化抽取规则
                extractRegular.init(serverUrl);
            }
        }
        return extractRegular;
    }

    /**
     * 初始化： 
     * 1、从配置管理web服务器获取完整的抽取规则的json表示 
     * 2、抽取json，构造对应的java对象结构 
     * 
     * @param serverUrl 配置管理WEB服务器的抽取规则下载地址
     */
    private synchronized void init(String serverUrl) {
        LOGGER.info("开始下载URL抽取规则");
        LOGGER.info("serverUrl: " + serverUrl);
        //从配置管理web服务器获取完整的抽取规则
        String json = downJson(serverUrl);
        LOGGER.info("完成下载URL抽取规则");
        //抽取规则
        LOGGER.info("开始解析URL抽取规则");
        List<UrlPattern> urlPatterns = parseJson(json);
        LOGGER.info("完成解析URL抽取规则");
        init(urlPatterns);
    }
    /**
     * 初始化：
     * 构造抽取规则查找结构
     * 
     * @param urlPatterns url模式列表
     */
    private synchronized void init(List<UrlPattern> urlPatterns) {
        LOGGER.info("开始初始化URL抽取规则");
        //构造抽取规则查找结构
        Map<String, List<UrlPattern>> newUrlPatterns = toMap(urlPatterns);
        if (!newUrlPatterns.isEmpty()) {
            Map<String, List<UrlPattern>> oldUrlPatterns = urlPatternMap;
            urlPatternMap = newUrlPatterns;
            //清空之前的抽取规则查找结构（如果有）
            if (oldUrlPatterns != null) {
                for (List<UrlPattern> list : oldUrlPatterns.values()) {
                    list.clear();
                }
                oldUrlPatterns.clear();
            }
        }
        LOGGER.info("完成初始化URL抽取规则");
    }

    /**
     * 订阅Redis服务器Channel：pr，当规则改变的时候会收到通知消息CHANGE并重新初始化规则集合
     */
    private void subscribeRedis(final String redisHost, final int redisPort, final String serverUrl) {
        if (null == redisHost || redisPort < 1) {
            LOGGER.error("没有指定redis服务器配置!");
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String channel = "pr";
                LOGGER.info("redis服务器配置信息 host:" + redisHost + ",port:" + redisPort + ",channel:" + channel);
                while (true) {
                    try {
                        JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), redisHost, redisPort);
                        Jedis jedis = jedisPool.getResource();
                        LOGGER.info("redis守护线程启动");
                        jedis.subscribe(new ExtractRegularChangeRedisListener(serverUrl), new String[]{channel});
                        jedisPool.returnResource(jedis);
                        LOGGER.info("redis守护线程结束");
                        break;
                    } catch (Exception e) {
                        LOGGER.info("redis未启动，暂停一分钟后重新连接");
                        try {
                            Thread.sleep(600000);
                        } catch (InterruptedException ex) {
                            LOGGER.error(ex.getMessage(), ex);
                        }
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("redis守护线程，用于动态加载抽取规则");
        thread.start();
    }

    /**
     * Redis监听器，监听抽取规则的变化
     *
     * @author 杨尚川
     *
     */
    private class ExtractRegularChangeRedisListener extends JedisPubSub {
        private final String serverUrl;

        public ExtractRegularChangeRedisListener(String serverUrl) {
            this.serverUrl = serverUrl;
        }

        @Override
        public void onMessage(String channel, String message) {
            LOGGER.debug("onMessage channel:" + channel + " and message:" + message);
            if ("pr".equals(channel) && "CHANGE".equals(message)) {
                synchronized (ExtractRegularChangeRedisListener.class) {
                    init(serverUrl);
                }
            }
        }

        @Override
        public void onPMessage(String pattern, String channel, String message) {
            LOGGER.debug("pattern:" + pattern + " and channel:" + channel + " and message:" + message);
            onMessage(channel, message);
        }

        @Override
        public void onPSubscribe(String pattern, int subscribedChannels) {
            LOGGER.debug("psubscribe pattern:" + pattern + " and subscribedChannels:" + subscribedChannels);
        }

        @Override
        public void onPUnsubscribe(String pattern, int subscribedChannels) {
            LOGGER.debug("punsubscribe pattern:" + pattern + " and subscribedChannels:" + subscribedChannels);
        }

        @Override
        public void onSubscribe(String channel, int subscribedChannels) {
            LOGGER.debug("subscribe channel:" + channel + " and subscribedChannels:" + subscribedChannels);
        }

        @Override
        public void onUnsubscribe(String channel, int subscribedChannels) {
            LOGGER.debug("unsubscribe channel:" + channel + " and subscribedChannels:" + subscribedChannels);
        }
    }

    /**
     * 从配置管理WEB服务器下载规则（json表示）
     *
     * @param url 配置管理WEB服务器下载规则的地址
     * @return json字符串
     */
    private String downJson(String url) {
        // 构造HttpClient的实例
        HttpClient httpClient = new HttpClient();
        // 创建GET方法的实例
        GetMethod method = new GetMethod(url);
        try {
            // 执行GetMethod
            int statusCode = httpClient.executeMethod(method);
            LOGGER.info("响应代码：" + statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("请求失败: " + method.getStatusLine());
            }
            // 读取内容
            String responseBody = new String(method.getResponseBody(), "utf-8");
            return responseBody;
        } catch (IOException e) {
            LOGGER.error("检查请求的路径：" + url, e);
        } finally {
            // 释放连接
            method.releaseConnection();
        }
        return "";
    }

    /**
     * 将json格式的URL模式转换为JAVA对象表示
     *
     * @param json URL模式的JSON表示
     * @return URL模式的JAVA对象表示
     */
    private List<UrlPattern> parseJson(String json) {
        List<UrlPattern> urlPatterns = new ArrayList<>();
        try {
            List<Map<String, Object>> ups = MAPPER.readValue(json, List.class);
            for (Map<String, Object> up : ups) {
                try {
                    UrlPattern urlPattern = new UrlPattern();
                    urlPatterns.add(urlPattern);
                    urlPattern.setUrlPattern(up.get("urlPattern").toString());
                    List<Map<String, Object>> pageTemplates = (List<Map<String, Object>>) up.get("pageTemplates");
                    for (Map<String, Object> pt : pageTemplates) {
                        try {
                            HtmlTemplate htmlTemplate = new HtmlTemplate();
                            urlPattern.addHtmlTemplate(htmlTemplate);
                            htmlTemplate.setTemplateName(pt.get("templateName").toString());
                            htmlTemplate.setTableName(pt.get("tableName").toString());
                            List<Map<String, Object>> cssPaths = (List<Map<String, Object>>) pt.get("cssPaths");
                            for (Map<String, Object> cp : cssPaths) {
                                try {
                                    CssPath cssPath = new CssPath();
                                    htmlTemplate.addCssPath(cssPath);
                                    cssPath.setCssPath(cp.get("cssPath").toString());
                                    cssPath.setFieldName(cp.get("fieldName").toString());
                                    cssPath.setFieldDescription(cp.get("fieldDescription").toString());
                                    List<Map<String, Object>> extractFunctions = (List<Map<String, Object>>) cp.get("extractFunctions");
                                    for (Map<String, Object> pf : extractFunctions) {
                                        try {
                                            ExtractFunction extractFunction = new ExtractFunction();
                                            cssPath.addExtractFunction(extractFunction);
                                            extractFunction.setExtractExpression(pf.get("extractExpression").toString());
                                            extractFunction.setFieldName(pf.get("fieldName").toString());
                                            extractFunction.setFieldDescription(pf.get("fieldDescription").toString());
                                        } catch (Exception e) {
                                            LOGGER.error("JSON抽取失败", e);
                                        }
                                    }
                                } catch (Exception e) {
                                    LOGGER.error("JSON抽取失败", e);
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("JSON抽取失败", e);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("JSON抽取失败", e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("JSON抽取失败", e);
        }
        return urlPatterns;
    }

    /**
     * 多个url模式可能会有相同的url前缀 
     * map结构+url前缀定位 
     * 用于快速查找一个url需要匹配的模式
     *
     * @param urlPatterns url模式列表
     * @return 以url前缀为key的map结构
     */
    private Map<String, List<UrlPattern>> toMap(List<UrlPattern> urlPatterns) {
        Map<String, List<UrlPattern>> map = new ConcurrentHashMap<>();
        for (UrlPattern urlPattern : urlPatterns) {
            try {
                URL url = new URL(urlPattern.getUrlPattern());
                String key = urlPrefix(url);
                List<UrlPattern> value = map.get(key);
                if (value == null) {
                    value = new ArrayList<>();
                    map.put(key, value);
                }
                value.add(urlPattern);
            } catch (Exception e) {
                LOGGER.error("URL规则初始化失败：" + urlPattern.getUrlPattern(), e);
            }
        }
        return map;
    }
    /**
     * 动态增加URL模式
     * @param urlPatterns URL模式列表
     */
    public void addUrlPatterns(List<UrlPattern> urlPatterns){
        for(UrlPattern urlPattern : urlPatterns){
            addUrlPattern(urlPattern);
        }
    }
    /**
     * 动态增加URL模式
     * @param urlPattern URL模式
     */
    public void addUrlPattern(UrlPattern urlPattern){
        try {
            URL url = new URL(urlPattern.getUrlPattern());
            String key = urlPrefix(url);
            List<UrlPattern> value = urlPatternMap.get(key);
            if (value == null) {
                value = new ArrayList<>();
                urlPatternMap.put(key, value);
            }
            value.add(urlPattern);
        } catch (Exception e) {
            LOGGER.error("URL规则添加失败：" + urlPattern.getUrlPattern(), e);
        }
    }
    public void removeUrlPattern(String urlPattern){
        try{
            URL url = new URL(urlPattern);
            String key = urlPrefix(url);
            urlPatternMap.remove(key);
        } catch (Exception e) {
            LOGGER.error("URL规则删除失败：" + urlPattern, e);
        }
    }
    /**
     * 获取一个url的前缀表示，用于快速定位URL模式 
     * 规则为： 
     * 协议+域名（去掉.)+端口（可选）
     *
     * @param url
     * @return
     */
    private String urlPrefix(URL url) {
        StringBuilder result = new StringBuilder();
        result.append(url.getProtocol());
        String[] splits = StringUtils.split(url.getHost(), '.');
        if (splits.length > 0) {
            for (String split : splits) {
                result.append(split);
            }
        }
        if (url.getPort() > -1) {
            result.append(Integer.toString(url.getPort()));
        }
        return result.toString();
    }

    /**
     * 获取一个可以用来抽取特定URL的页面模板集合
     *
     * @param urlString url
     * @return 页面模板集合
     */
    public List<HtmlTemplate> getHtmlTemplate(String urlString) {
        List<HtmlTemplate> pageTemplates = new ArrayList<>();
        if (urlPatternMap != null) {
            try {
                URL url = new URL(urlString);
                String key = urlPrefix(url);
                List<UrlPattern> patterns = urlPatternMap.get(key);
                for (UrlPattern urlPattern : patterns) {
                    Matcher matcher = urlPattern.getRegexPattern().matcher(urlString);
                    if (matcher.find()) {
                        //匹配成功
                        pageTemplates.addAll(urlPattern.getHtmlTemplates());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("获取URL抽取规则失败：" + urlString, e);
            }
        }
        return pageTemplates;
    }

    public static void main(String[] args) throws Exception {
        ExtractRegular extractRegular = ExtractRegular.getInstance("http://localhost:8080/HtmlExtractorServer/api/all_extract_regular.jsp", null, -1);

        List<HtmlTemplate> pageTemplates = extractRegular.getHtmlTemplate("http://money.163.com/14/0529/19/9TEGPK5T00252G50.html");
        for (HtmlTemplate pageTemplate : pageTemplates) {
            System.out.println(pageTemplate);
        }

        pageTemplates = extractRegular.getHtmlTemplate("http://finance.qq.com/a/20140530/004254.htm");
        for (HtmlTemplate pageTemplate : pageTemplates) {
            System.out.println(pageTemplate);
        }
    }
}
