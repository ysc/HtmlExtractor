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
 * 抽取函数
 * 抽取函数是页面模板的二级元素
 * 可以精准地控制抽取的内容
 *
 * @author 杨尚川
 *
 */
public class ExtractFunction {
    /**
     * 抽取函数对应的CSS路径
     */
    private CssPath cssPath;
    /**
     * 抽取函数（只能使用系统内置支持的函数）
     */
    private String extractExpression;
    /**
     * 抽取函数提取出的文本存储到哪个字段
     */
    private String fieldName;
    /**
     * 抽取函数提取出的字段的中文含义，仅仅起注释作用，利于理解
     */
    private String fieldDescription;

    public CssPath getCssPath() {
        return cssPath;
    }

    public void setCssPath(CssPath cssPath) {
        this.cssPath = cssPath;
    }

    public String getExtractExpression() {
        return extractExpression;
    }

    public void setExtractExpression(String extractExpression) {
        this.extractExpression = extractExpression;
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
        str.append(this.extractExpression).append("\n");
        str.append(this.fieldName).append("\n");
        str.append(this.fieldDescription).append("\n");
        return str.toString();
    }
}
