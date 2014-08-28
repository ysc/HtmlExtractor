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

package org.apdplat.extractor.html.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apdplat.extractor.html.server.model.CssPath;
import org.apdplat.extractor.html.server.model.ExtractFunction;
import org.apdplat.extractor.html.server.model.HtmlTemplate;
import org.apdplat.extractor.html.server.model.UrlPattern;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.LoggerFactory;

/**
 * JSON生成器
 * @author 杨尚川
 */
public class JsonGenerator {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JsonGenerator.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private JsonGenerator(){}
    
    public static String generateExtractRegular(List<UrlPattern> urlPatterns){
        try {
            String value = MAPPER.writeValueAsString(urlPatterns);
            return value;
        } catch (IOException ex) {
            LOGGER.error("将抽取规则转换为JSON出错", ex);
        }
        return "[]";
    }
    public static void main(String[] args) {
        List<UrlPattern> urlPatterns = new ArrayList<>();

        UrlPattern urlPattern = new UrlPattern();
        urlPattern.setUrlPattern("http://money.163.com/\\d{2}/\\d{4}/\\d{2}/[0-9A-Z]{16}.html");

        urlPatterns.add(urlPattern);

        HtmlTemplate htmlTemplate = new HtmlTemplate();
        htmlTemplate.setTemplateName("网易财经频道");
        htmlTemplate.setTableName("finance");

        urlPattern.addHtmlTemplate(htmlTemplate);

        CssPath cssPath = new CssPath();
        cssPath.setCssPath("h1#h1title");
        cssPath.setFieldName("title");
        cssPath.setFieldDescription("标题");

        htmlTemplate.addCssPath(cssPath);

        cssPath = new CssPath();
        cssPath.setCssPath("div#endText");
        cssPath.setFieldName("content");
        cssPath.setFieldDescription("正文");

        htmlTemplate.addCssPath(cssPath);

        urlPattern = new UrlPattern();
        urlPattern.setUrlPattern("http://finance.qq.com/a/\\d{8}/\\d{6}.htm");

        urlPatterns.add(urlPattern);

        htmlTemplate = new HtmlTemplate();
        htmlTemplate.setTemplateName("腾讯财经频道");
        htmlTemplate.setTableName("finance");

        urlPattern.addHtmlTemplate(htmlTemplate);

        cssPath = new CssPath();
        cssPath.setCssPath("div#C-Main-Article-QQ div.hd h1");
        cssPath.setFieldName("title");
        cssPath.setFieldDescription("标题");

        htmlTemplate.addCssPath(cssPath);

        cssPath = new CssPath();
        cssPath.setCssPath("div#Cnt-Main-Article-QQ");
        cssPath.setFieldName("content");
        cssPath.setFieldDescription("正文");

        htmlTemplate.addCssPath(cssPath);

        ExtractFunction extractFunction = new ExtractFunction();
        extractFunction.setFieldName("content");
        extractFunction.setFieldDescription("正文");
        extractFunction.setExtractExpression("deleteChild(“div.ep-source”)");

        cssPath.addExtractFunction(extractFunction);

        System.out.println(generateExtractRegular(urlPatterns));

    }
}
