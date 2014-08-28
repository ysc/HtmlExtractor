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

package org.apdplat.extractor.html.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 网页模板 
 * 一个URL模式会有一到多个网页模板
 * 一套网页模板指定了如何精准地抽取网页信息
 *
 * @author 杨尚川
 *
 */
public class HtmlTemplate {
    /**
     * 网页模板名称，仅仅注释作用
     */
    private String templateName;
    /**
     * 网页提取出的文本存储到哪个表
     */
    private String tableName;
    /**
     * URL模式
     */
    private UrlPattern urlPattern;
    /**
     * 多个CSS路径
     */
    private List<CssPath> cssPaths = new ArrayList<>();

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public UrlPattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(UrlPattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public List<CssPath> getCssPaths() {
        return cssPaths;
    }

    public void setCssPaths(List<CssPath> cssPaths) {
        this.cssPaths = cssPaths;
        for (CssPath cssPath : this.cssPaths) {
            cssPath.setPageTemplate(this);
        }
    }

    public boolean hasCssPath() {
        return !cssPaths.isEmpty();
    }

    public void addCssPath(CssPath cssPath) {
        cssPaths.add(cssPath);
        cssPath.setPageTemplate(this);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("网页模板：").append(this.templateName).append("，存储表：").append(this.tableName).append("\n\n");
        int i = 1;
        for (CssPath cssPath : cssPaths) {
            str.append(i++).append("、").append(cssPath.toString()).append("\n");
        }
        return str.toString();
    }
}
