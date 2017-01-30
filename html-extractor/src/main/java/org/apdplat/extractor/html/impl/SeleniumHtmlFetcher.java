/*
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.apdplat.extractor.html.impl;

import org.apdplat.extractor.html.HtmlFetcher;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 安装geckodriver:
 * brew install geckodriver
 *
 * 使用selenium执行JS动态渲染网页获取页面内容
 *
 * @author 杨尚川
 */
public class SeleniumHtmlFetcher implements HtmlFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumHtmlFetcher.class);

    //火狐浏览器
    private static final WebDriver WEB_DRIVER = new FirefoxDriver();

    /**
     * 使用HtmlUnit获取页面内容，HtmlUnit能执行JS，动态渲染网页，但不是所有JS都能渲染，需要测试
     * @param url html页面路径
     * @return
     */
    @Override
    public String fetch(String url) {
        try{
            LOGGER.debug("url:"+url);
            WEB_DRIVER.get(url);
            String html = WEB_DRIVER.getPageSource();
            LOGGER.debug("html:"+html);
            return html;
        }catch (Exception e) {
            LOGGER.error("获取URL："+url+"页面出错", e);
        }
        return "";
    }

    public static void main(String[] args) {
        HtmlFetcher htmlFetcher = new SeleniumHtmlFetcher();
        String html = htmlFetcher.fetch("http://apdplat.org");
        System.out.println(html);
    }
}
