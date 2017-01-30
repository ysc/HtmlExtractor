package org.apdplat.extractor.html.impl;

import org.apdplat.extractor.html.HtmlFetcher;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by ysc on 30/01/2017.
 */
public class SeleniumHtmlFetcherTest {

    @Test
    public void fetch() throws Exception {

        HtmlFetcher htmlFetcher = new SeleniumHtmlFetcher();

        String html = htmlFetcher.fetch("http://apdplat.org");

        assertEquals(true, html.contains("APDPlat的主要特性如下"));
        assertEquals(true, html.contains("1、jsearch：高性能的全文检索工具包"));
        assertEquals(true, html.contains("2、Java分布式中文分词组件 - word分词"));
        assertEquals(true, html.contains("3、Java实现的人机问答系统：QuestionAnsweringSystem"));
        assertEquals(true, html.contains("4、Java开源项目cws_evaluation：中文分词器分词效果评估"));
        assertEquals(true, html.contains("5、Java实现的基于模板的网页结构化信息精准抽取组件：HtmlExtractor"));
        assertEquals(true, html.contains("6、搜索引擎收录排名seo工具：rank"));
        assertEquals(true, html.contains("7、superword - Java实现的英文单词分析软件"));
        assertEquals(true, html.contains("8、大数据的对象持久化：borm"));
        assertEquals(true, html.contains("9、元搜索引擎：search"));
        assertEquals(true, html.contains("10、word_web - 通过web服务器对word分词的资源进行集中统一管理"));
        assertEquals(true, html.contains("11、high-availability：保障服务的持续高可用、高性能及负载均衡"));
        assertEquals(true, html.contains("12、short-text-search：自定制的精准短文本搜索服务"));
        assertEquals(true, html.contains("13、counter：分布式环境下的原子计数器和API每天调用次数限制"));
        assertEquals(true, html.contains("14、APDPlat：应用级产品开发平台"));

        assertEquals(true, html.contains("https://github.com/ysc/jsearch"));
        assertEquals(true, html.contains("https://github.com/ysc/word"));
        assertEquals(true, html.contains("https://github.com/ysc/QuestionAnsweringSystem"));
        assertEquals(true, html.contains("https://github.com/ysc/cws_evaluation"));
        assertEquals(true, html.contains("https://github.com/ysc/HtmlExtractor"));
        assertEquals(true, html.contains("https://github.com/ysc/rank"));
        assertEquals(true, html.contains("https://github.com/ysc/superword"));
        assertEquals(true, html.contains("https://github.com/ysc/borm"));
        assertEquals(true, html.contains("https://github.com/ysc/search"));
        assertEquals(true, html.contains("https://github.com/ysc/word_web"));
        assertEquals(true, html.contains("https://github.com/ysc/high-availability"));
        assertEquals(true, html.contains("https://github.com/ysc/short-text-search"));
        assertEquals(true, html.contains("https://github.com/ysc/counter"));
        assertEquals(true, html.contains("https://github.com/ysc/APDPlat"));
    }

}