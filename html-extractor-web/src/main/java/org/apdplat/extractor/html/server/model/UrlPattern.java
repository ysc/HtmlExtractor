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

package org.apdplat.extractor.html.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * URL模式（使用正则表达式实现）
 * 用正则表达式的方式来指定一组有共同页面布局的网页
 * 这样就可以对这组页面指定一套模板来抽取信息
 *
 * @author 杨尚川
 *
 */
public class UrlPattern {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlPattern.class);
    /**
     * URL模式（使用正则表达式实现）
     */
    private String urlPattern;
    /**
     * URL模式（编译好的正则表达式）
     */
    private Pattern regexPattern;
    /**
     * 多个网页模板
     */
    private List<HtmlTemplate> htmlTemplates = new ArrayList<>();

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
        try {
            regexPattern = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
            LOGGER.error("编译正则表达式["+urlPattern+"]失败：", e);
        }
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    public List<HtmlTemplate> getHtmlTemplates() {
        return htmlTemplates;
    }

    public void setHtmlTemplates(List<HtmlTemplate> htmlTemplates) {
        this.htmlTemplates = htmlTemplates;
        for (HtmlTemplate htmlTemplate : this.htmlTemplates) {
            htmlTemplate.setUrlPattern(this);
        }
    }

    public boolean hasHtmlTemplate() {
        return !htmlTemplates.isEmpty();
    }

    public void addHtmlTemplate(HtmlTemplate htmlTemplate) {
        htmlTemplates.add(htmlTemplate);
        htmlTemplate.setUrlPattern(this);
    }
}
