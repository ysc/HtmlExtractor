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

/**
 * 网页结构化信息抽取失败日志
 *
 * @author 杨尚川
 *
 */
public class ExtractFailLog {
    /**
     * 网页结构化信息抽取结果
     */
    private ExtractResult extractResult;
    /**
     * 网页的URL
     */
    private String url;
    /**
     * 网页的URL模式
     */
    private String urlPattern;
    /**
     * 网页模板
     */
    private String templateName;
    /**
     * CSS路径
     */
    private String cssPath;
    /**
     * CSS路径下的抽取函数
     */
    private String extractExpression;
    /**
     * 抽取出的内容保存到的表的名称
     */
    private String tableName;
    /**
     * 抽取出的内容保存到的字段名称
     */
    private String fieldName;
    /**
     * 抽取出的内容保存到的字段描述，仅作注释使用
     */
    private String fieldDescription;

    public ExtractResult getExtractResult() {
        return extractResult;
    }

    public void setExtractResult(ExtractResult extractResult) {
        this.extractResult = extractResult;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCssPath() {
        return cssPath;
    }

    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }

    public String getExtractExpression() {
        return extractExpression;
    }

    public void setExtractExpression(String extractExpression) {
        this.extractExpression = extractExpression;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDescription() {
        return fieldDescription;
    }

    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    @Override
    public String toString() {
        return "ExtractFailLog [\nurl=" + url + ", \nurlPattern=" + urlPattern
                + ", \ntemplateName=" + templateName + ", \ncssPath=" + cssPath
                + ", \nextractExpression=" + extractExpression + ", \ntableName="
                + tableName + ", \nfieldName=" + fieldName
                + ", \nfieldDescription=" + fieldDescription + "]";
    }
}
