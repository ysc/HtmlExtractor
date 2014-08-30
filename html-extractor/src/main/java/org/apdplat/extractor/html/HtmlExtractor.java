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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apdplat.extractor.html.model.CssPath;
import org.apdplat.extractor.html.model.ExtractFailLog;
import org.apdplat.extractor.html.model.ExtractFunction;
import org.apdplat.extractor.html.model.ExtractResult;
import org.apdplat.extractor.html.model.ExtractResultItem;
import org.apdplat.extractor.html.model.HtmlTemplate;
import org.apdplat.extractor.html.model.UrlPattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 网页抽取工具
 * 根据URL模式、页面模板、CSS路径、抽取函数，抽取HTML页面
 *
 * @author 杨尚川
 *
 */
public class HtmlExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlExtractor.class);
    private static HtmlExtractor htmlExtractor;
    private ExtractRegular extractRegular;

    private HtmlExtractor() {
    }
    /**
     * 获取实例
     * @param extractRegular URL抽取规则
     * @return 
     */
    public static HtmlExtractor getInstance(ExtractRegular extractRegular) {
        if (htmlExtractor != null) {
            return htmlExtractor;
        }
        synchronized (HtmlExtractor.class) {
            if (htmlExtractor == null) {
                htmlExtractor = new HtmlExtractor();
                htmlExtractor.extractRegular = extractRegular;
            }
        }
        return htmlExtractor;
    }
    /**
     * 获取实例
     * @param allExtractRegularUrl 获取抽取规则的WEB服务器地址
     * @param redisHost REDIS主机
     * @param redisPort REDIS端口
     * @return 
     */
    public static HtmlExtractor getInstance(String allExtractRegularUrl, String redisHost, int redisPort) {
        if (htmlExtractor != null) {
            return htmlExtractor;
        }
        synchronized (HtmlExtractor.class) {
            if (htmlExtractor == null) {
                ExtractRegular extractRegular = ExtractRegular.getInstance(allExtractRegularUrl, redisHost, redisPort);
                htmlExtractor = new HtmlExtractor();
                htmlExtractor.extractRegular = extractRegular;
            }
        }
        return htmlExtractor;
    }
    /**
     * 抽取信息
     * @param url html页面路径
     * @param encoding 页面编码
     * @return 抽取结果
     */
    public List<ExtractResult> extract(String url, String encoding) {
        InputStream in = null;
        try {
            in = new URL(url).openConnection().getInputStream();
        } catch (Exception e) {
            LOGGER.error("获取URL输入流失败：" + url, e);
        }
        return extract(url, in, encoding);
    }
    /**
     * 抽取信息
     * @param url html页面路径
     * @param encoding 页面编码
     * @param content html页面内容
     * @return 
     */
    public List<ExtractResult> extract(String url, byte[] content, String encoding) {
        InputStream in = new ByteArrayInputStream(content);
        return extract(url, in, encoding);
    }
    /**
     * 抽取信息
     * @param url html页面路径
     * @param encoding 页面编码
     * @param in html页面内容输入流
     * @return 
     */
    public List<ExtractResult> extract(String url, InputStream in, String encoding) {
        List<ExtractResult> extractResults = new ArrayList<>();
        if (!Charset.isSupported(encoding)) {
            LOGGER.error("不支持的网页字符编码：" + encoding + " ，URL：" + url);
            return extractResults;
        }
        //同一个URL可能会有多个页面模板
        List<HtmlTemplate> htmlTemplates = extractRegular.getHtmlTemplate(url);
        if (htmlTemplates.isEmpty()) {
            return extractResults;
        }
        try {
            byte[] content = readAll(in);
            Document doc = Jsoup.parse(new ByteArrayInputStream(content), encoding, url);
            Elements metas = doc.select("meta");
            String keywords = "";
            String description = "";
            for (Element meta : metas) {
                String name = meta.attr("name");
                if ("keywords".equals(name)) {
                    keywords = meta.attr("content");
                }
                if ("description".equals(name)) {
                    description = meta.attr("content");
                }
            }
            Set<String> tableNames = new HashSet<>();
            for (HtmlTemplate htmlTemplate : htmlTemplates) {
                if (tableNames.contains(htmlTemplate.getTableName())) {
                    LOGGER.debug("多个模板定义的tableName重复，这有可能会导致数据丢失，检查UrlPattern下定义的模板：" + htmlTemplate.getUrlPattern().getUrlPattern());
                    LOGGER.debug(htmlTemplates.toString());
                }
                tableNames.add(htmlTemplate.getTableName());
                try {
                    //按页面模板的定义对网页进行抽取
                    ExtractResult extractResult = extractHtmlTemplate(url, htmlTemplate, doc);
                    extractResult.setContent(content);
                    extractResult.setEncoding(encoding);
                    extractResult.setKeywords(keywords);
                    extractResult.setDescription(description);
                    extractResults.add(extractResult);
                } catch (Exception e) {
                    LOGGER.error("页面模板抽取失败：" + htmlTemplate.getTemplateName(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("抽取网页出错: " + url, e);
        }
        return extractResults;
    }

    /**
     * 从输入流中读取所有字节
     *
     * @param in 输入流
     * @return 字节数组
     */
    public static byte[] readAll(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            for (int n; (n = in.read(buffer)) > 0;) {
                out.write(buffer, 0, n);
            }
        } catch (IOException ex) {
            LOGGER.error("读取文件失败", ex);
        }
        return out.toByteArray();
    }
    /**
     * 根据模板的定义抽取信息
     * @param url html页面路径
     * @param htmlTemplate html页面模板
     * @param doc jsoup文档
     * @return 抽取结果
     */
    private ExtractResult extractHtmlTemplate(String url, HtmlTemplate htmlTemplate, Document doc) {
        //一个页面模板对应一个抽取结果
        ExtractResult extractResult = new ExtractResult();
        extractResult.setUrl(url);
        extractResult.setTableName(htmlTemplate.getTableName());
        List<CssPath> cssPaths = htmlTemplate.getCssPaths();
	//页面模板中定义的所有CSS路径和抽取表达式全部抽取成功，才算抽取成功
        //只要有一个CSS路径或抽取表达式失败，就是抽取失败
        for (CssPath cssPath : cssPaths) {
            // 抽取一条CSS PATH
            Elements elements = doc.select(cssPath.getCssPath());
            // 如果CSS路径匹配多个元素，则抽取字段为多值
            for (Element element : elements) {
                String text = null;
                if(StringUtils.isBlank(cssPath.getAttr())){
                    //提取文本
                    text = element.text();
                }else{
                    //提取属性
                    text = element.attr(cssPath.getAttr());
                }
                if (StringUtils.isNotBlank(text)) {
                    // 成功提取文本
                    if (cssPath.hasExtractFunction()) {
                        //使用CSS路径下的抽取函数做进一步抽取
                        for (ExtractFunction pf : cssPath.getExtractFunctions()) {
                            text = ExtractFunctionExecutor.execute(text, doc, cssPath, pf.getExtractExpression());
                            if (text != null) {
                                ExtractResultItem extractResultItem = new ExtractResultItem();
                                extractResultItem.setField(pf.getFieldName());
                                extractResultItem.setValue(text);
                                extractResult.addExtractResultItem(extractResultItem);
                            } else {
                                ExtractFailLog extractFailLog = new ExtractFailLog();
                                extractFailLog.setUrl(url);
                                extractFailLog.setUrlPattern(htmlTemplate.getUrlPattern().getUrlPattern());
                                extractFailLog.setTemplateName(htmlTemplate.getTemplateName());
                                extractFailLog.setCssPath(cssPath.getCssPath());
                                extractFailLog.setExtractExpression(pf.getExtractExpression());
                                extractFailLog.setTableName(htmlTemplate.getTableName());
                                extractFailLog.setFieldName(pf.getFieldName());
                                extractFailLog.setFieldDescription(pf.getFieldDescription());
                                extractResult.addExtractFailLog(extractFailLog);
                                //未抽取到结果，保存抽取失败日志并停止抽取，抽取失败
                                //快速失败模式
                                //如果要记录所有失败日志，则去除下面一行返回的代码
                                return extractResult;
                            }
                        }
                    } else {
                        //使用CSS路径抽取的结果
                        ExtractResultItem extractResultItem = new ExtractResultItem();
                        extractResultItem.setField(cssPath.getFieldName());
                        extractResultItem.setValue(text);
                        extractResult.addExtractResultItem(extractResultItem);
                    }
                } else {
                    //未抽取到结果，保存抽取失败日志并停止抽取，抽取失败
                    ExtractFailLog extractFailLog = new ExtractFailLog();
                    extractFailLog.setUrl(url);
                    extractFailLog.setUrlPattern(htmlTemplate.getUrlPattern().getUrlPattern());
                    extractFailLog.setTemplateName(htmlTemplate.getTemplateName());
                    extractFailLog.setCssPath(cssPath.getCssPath());
                    extractFailLog.setExtractExpression("");
                    extractFailLog.setTableName(htmlTemplate.getTableName());
                    extractFailLog.setFieldName(cssPath.getFieldName());
                    extractFailLog.setFieldDescription(cssPath.getFieldDescription());
                    extractResult.addExtractFailLog(extractFailLog);
                    //未抽取到结果，保存抽取失败日志并停止抽取，抽取失败
                    //快速失败模式
                    //如果要记录所有失败日志，则去除下面一行返回的代码
                    return extractResult;
                }
            }
        }
        return extractResult;
    }
    private static void usage2(){
        String allExtractRegularUrl = "http://localhost:8080/html-extractor-web/api/all_extract_regular.jsp";
        String redisHost = "localhost";
        int redisPort = 6379;
        
        HtmlExtractor htmlExtractor = HtmlExtractor.getInstance(allExtractRegularUrl, redisHost, redisPort);

        String url = "http://money.163.com/08/1219/16/4THR2TMP002533QK.html";
        List<ExtractResult> extractResults = htmlExtractor.extract(url, "gb2312");

        int i = 1;
        for (ExtractResult extractResult : extractResults) {
            System.out.println((i++) + "、网页 " + extractResult.getUrl() + " 的抽取结果");
            Map<String, List<ExtractResultItem>> extractResultItems = extractResult.getExtractResultItems();
            for(String field : extractResultItems.keySet()){
                List<ExtractResultItem> values = extractResultItems.get(field);
                if(values.size() > 1){
                    int j=1;
                    System.out.println("\t多值字段:"+field);
                    for(ExtractResultItem item : values){
                        System.out.println("\t\t"+(j++)+"、"+field+" = "+item.getValue());   
                    }
                }else{
                    System.out.println("\t"+field+" = "+values.get(0).getValue());     
                }
            }
            System.out.println("\tdescription = "+extractResult.getDescription());
            System.out.println("\tkeywords = "+extractResult.getKeywords());
        }
    }
    private static void usage1(){
        //1、构造抽取规则
        List<UrlPattern> urlPatterns = new ArrayList<>();
        //1.1、构造URL模式
        UrlPattern urlPattern = new UrlPattern();
        urlPattern.setUrlPattern("http://money.163.com/\\d{2}/\\d{4}/\\d{2}/[0-9A-Z]{16}.html");
        //1.2、构造HTML模板
        HtmlTemplate htmlTemplate = new HtmlTemplate();
        htmlTemplate.setTemplateName("网易财经频道");
        htmlTemplate.setTableName("finance");
        //1.3、将URL模式和HTML模板建立关联
        urlPattern.addHtmlTemplate(htmlTemplate);
        //1.4、构造CSS路径
        CssPath cssPath = new CssPath();
        cssPath.setCssPath("h1");
        cssPath.setFieldName("title");
        cssPath.setFieldDescription("标题");
        //1.5、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //1.6、构造CSS路径
        cssPath = new CssPath();
        cssPath.setCssPath("div#endText");
        cssPath.setFieldName("content");
        cssPath.setFieldDescription("正文");
        //1.7、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //可象上面那样构造多个URLURL模式
        urlPatterns.add(urlPattern);
        //2、获取抽取规则对象
        ExtractRegular extractRegular = ExtractRegular.getInstance(urlPatterns);
        //注意：可通过如下3个方法动态地改变抽取规则
        //extractRegular.addUrlPatterns(urlPatterns);
        //extractRegular.addUrlPattern(urlPattern);
        //extractRegular.removeUrlPattern(urlPattern.getUrlPattern());
        //3、获取HTML抽取工具
        HtmlExtractor htmlExtractor = HtmlExtractor.getInstance(extractRegular);
        //4、抽取网页
        String url = "http://money.163.com/08/1219/16/4THR2TMP002533QK.html";
        List<ExtractResult> extractResults = htmlExtractor.extract(url, "gb2312");
        //5、输出结果
        int i = 1;
        for (ExtractResult extractResult : extractResults) {
            System.out.println((i++) + "、网页 " + extractResult.getUrl() + " 的抽取结果");
            Map<String, List<ExtractResultItem>> extractResultItems = extractResult.getExtractResultItems();
            for(String field : extractResultItems.keySet()){
                List<ExtractResultItem> values = extractResultItems.get(field);
                if(values.size() > 1){
                    int j=1;
                    System.out.println("\t多值字段:"+field);
                    for(ExtractResultItem item : values){
                        System.out.println("\t\t"+(j++)+"、"+field+" = "+item.getValue());   
                    }
                }else{
                    System.out.println("\t"+field+" = "+values.get(0).getValue());     
                }
            }
            System.out.println("\tdescription = "+extractResult.getDescription());
            System.out.println("\tkeywords = "+extractResult.getKeywords());
        }
    }
    private static void usage3(){
        //1、构造抽取规则
        List<UrlPattern> urlPatterns = new ArrayList<>();
        //1.1、构造URL模式
        UrlPattern urlPattern = new UrlPattern();
        urlPattern.setUrlPattern("http://list.jd.com/list.html\\?cat=([\\d,]+)");
        //1.2、构造HTML模板
        HtmlTemplate htmlTemplate = new HtmlTemplate();
        htmlTemplate.setTemplateName("京东商品");
        htmlTemplate.setTableName("jd_goods");
        //1.3、将URL模式和HTML模板建立关联
        urlPattern.addHtmlTemplate(htmlTemplate);
        //1.4、构造CSS路径
        CssPath cssPath = new CssPath();
        cssPath.setCssPath("html body div div div ul li div div.p-name");
        cssPath.setFieldName("name");
        cssPath.setFieldDescription("名称");
        //1.5、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //1.6、构造CSS路径
        cssPath = new CssPath();
        cssPath.setCssPath("html body div div div ul li div div.p-name a");
        cssPath.setAttr("href");
        cssPath.setFieldName("link");
        cssPath.setFieldDescription("链接");
        //1.7、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //1.8、构造CSS路径
        cssPath = new CssPath();
        cssPath.setCssPath("html body div div div ul li div div.p-price strong");
        cssPath.setFieldName("price");
        cssPath.setFieldDescription("价格");
        //1.9、将CSS路径和模板建立关联
        htmlTemplate.addCssPath(cssPath);
        //可象上面那样构造多个URLURL模式
        urlPatterns.add(urlPattern);
        //2、获取抽取规则对象
        ExtractRegular extractRegular = ExtractRegular.getInstance(urlPatterns);
        //注意：可通过如下3个方法动态地改变抽取规则
        //extractRegular.addUrlPatterns(urlPatterns);
        //extractRegular.addUrlPattern(urlPattern);
        //extractRegular.removeUrlPattern(urlPattern.getUrlPattern());
        //3、获取HTML抽取工具
        HtmlExtractor htmlExtractor = HtmlExtractor.getInstance(extractRegular);
        //4、抽取网页
        String url = "http://list.jd.com/list.html?cat=9987,653,655";
        List<ExtractResult> extractResults = htmlExtractor.extract(url, "utf-8");
        //5、输出结果
        int i = 1;
        for (ExtractResult extractResult : extractResults) {
            System.out.println((i++) + "、网页 " + extractResult.getUrl() + " 的抽取结果");
            Map<String, List<ExtractResultItem>> extractResultItems = extractResult.getExtractResultItems();
            for(String field : extractResultItems.keySet()){
                List<ExtractResultItem> values = extractResultItems.get(field);
                if(values.size() > 1){
                    int j=1;
                    System.out.println("\t多值字段:"+field);
                    for(ExtractResultItem item : values){
                        System.out.println("\t\t"+(j++)+"、"+field+" = "+item.getValue());   
                    }
                }else{
                    System.out.println("\t"+field+" = "+values.get(0).getValue());     
                }
            }
            System.out.println("\tdescription = "+extractResult.getDescription());
            System.out.println("\tkeywords = "+extractResult.getKeywords());
        }
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        //下面的三种方法代表了3种不同的使用模式，只能单独使用
        //usage1();
        //usage2();
        usage3();
    }
}
