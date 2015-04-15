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

package org.apdplat.extractor.html.impl;

import org.apdplat.extractor.html.HtmlFetcher;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
/**
 *
 * 使用JSoup获取网页内容
 * @author 杨尚川
 */
public class JSoupHtmlFetcher implements HtmlFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSoupHtmlFetcher.class);

    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.10; rv:36.0) Gecko/20100101 Firefox/36.0";

    @Override
    public String fetch(String url) {
        try {
            LOGGER.debug("url:"+url);
            String host = new URL(url).getHost();
            Connection conn = Jsoup.connect(url)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("Referer", "http://"+host)
                    .header("Host", host)
                    .header("User-Agent", USER_AGENT)
                    .ignoreContentType(true);
            String html = conn.get().html();
            LOGGER.debug("html:"+html);
            return html;
        }catch (Exception e){
            LOGGER.error("获取URL："+url+"页面出错", e);
        }
        return "";
    }
}
