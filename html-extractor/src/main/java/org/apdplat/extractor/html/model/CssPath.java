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
 * CSS路径
 * CSS路径是页面模板的一级元素
 * CSS路径抽取到的内容如果还不满足要求
 * 需要使用二级元素即抽取函数来做进一步控制
 * 
 * @author 杨尚川
 *
 */
public class CssPath {
    /**
     * CSS路径对应的网页模板
     */
    private HtmlTemplate pageTemplate;
    /**
     * CSS路径
     */
    private String cssPath;
    /**
     * 提取属性，如果不指定属性，则提取文本
     */
    private String attr;
    /**
     * CSS路径对应的抽取函数列表
     */
    private List<ExtractFunction> extractFunctions = new ArrayList<>();
    /**
     * CSS路径提取出的文本存储到哪个字段
     */
    private String fieldName;
    /**
     * CSS路径提取出的字段的中文含义，仅仅起注释作用，利于理解
     */
    private String fieldDescription;

    public HtmlTemplate getPageTemplate() {
        return pageTemplate;
    }

    public void setPageTemplate(HtmlTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    public String getCssPath() {
        return cssPath;
    }

    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public List<ExtractFunction> getExtractFunctions() {
        return extractFunctions;
    }

    public void setExtractFunctions(List<ExtractFunction> extractFunctions) {
        this.extractFunctions = extractFunctions;
        for (ExtractFunction extractFunction : this.extractFunctions) {
            extractFunction.setCssPath(this);
        }
    }

    public boolean hasExtractFunction() {
        return !extractFunctions.isEmpty();
    }

    public void addExtractFunction(ExtractFunction extractFunction) {
        extractFunctions.add(extractFunction);
        extractFunction.setCssPath(this);
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
        StringBuilder str = new StringBuilder();
        str.append("CSS路径：").append(this.cssPath).append("\n");
        str.append("字段名：").append(this.fieldName).append("\n");
        str.append("字段含义：").append(this.fieldDescription).append("\n");
        for (ExtractFunction ef : this.extractFunctions) {
            str.append("\t").append("抽取函数：").append(ef.getExtractExpression()).append("\n");
            str.append("\t").append("字段名：").append(ef.getFieldName()).append("\n");
            str.append("\t").append("字段含义：").append(ef.getFieldDescription()).append("\n");
        }
        return str.toString();
    }
}
