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

package org.apdplat.extractor.html.demo;

import org.apache.commons.lang3.StringUtils;
import org.apdplat.extractor.html.impl.JSoupHtmlFetcher;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * VOA 资源下载
 * Created by ysc on 06/28/16.
 */
public class VOA {
    public static void main(String[] args) throws Exception{
        AtomicInteger i = new AtomicInteger();
        download("/Users/ysc/百度云同步盘/VOA/English in a Minute", "/Users/ysc/workspace/HtmlExtractor/html-extractor/src/main/resources/EnglishInAMinute.txt").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));
        i.set(0);
        download("/Users/ysc/百度云同步盘/VOA/English @ the Movies", "/Users/ysc/workspace/HtmlExtractor/html-extractor/src/main/resources/EnglishAtTheMovies.txt").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));
    }

    public static Set<String> download(String target, String sources) throws Exception{
        Set<String> set = new HashSet<>();
        AtomicInteger i = new AtomicInteger();
        Files.readAllLines(Paths.get(sources))
                .parallelStream()
                .forEach(line->{
                    String[] attr = line.trim().split("=");
                    String url = attr[0];
                    String name = attr[1].replace("?", "");
                    int times=0;
                    boolean success = false;
                    while (!success && (times++) < 3) {
                        try {
                            JSoupHtmlFetcher fetcher = new JSoupHtmlFetcher();
                            String html = fetcher.fetch(url);
                            String href = findMp4(html);
                            if(StringUtils.isBlank(href)){
                                System.err.println(i.incrementAndGet() + ". not find resource, name: " + name + ", url: " + url);
                                set.add(name+" "+url);
                                return;
                            }
                            System.out.println(i.incrementAndGet() + ". download resource, name: " + name + ", url: " + href);
                            download(href, target, name + ".mp4");
                            success = true;
                        } catch (Exception e) {
                            System.out.println(e.getMessage() + " retry...");
                        }
                    }
                });
        return set;
    }

    private static String findMp4(String html){
        Document document = Jsoup.parse(html);
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 720P
            if (href.endsWith("_hq.mp4?download=1")) {
                return href;
            }
        }
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 360P
            if (href.endsWith(".mp4?download=1") && !href.endsWith("_mobile.mp4?download=1")) {
                return href;
            }
        }
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 270P
            if (href.endsWith(".mp4?download=1")) {
                return href;
            }
        }
        return "";
    }

    public static void download(String url, String fileDir, String fileName) {
        int timeout = 300000;
        int times=0;
        boolean success = false;
        while (!success && (times++) < 3) {
            try {
                //确保本地路径存储
                Path dir = Paths.get(fileDir);
                if (!Files.exists(dir)) {
                    //不存在则新建
                    dir.toFile().mkdirs();
                }
                //保存文件的完整本地路径
                Path out = Paths.get(fileDir, fileName);
                //如果文件存在则表示之前已经下载过，本次不用下载
                //因为VOA的访问不稳定，所以可能需要执行程序多次才能完整下载完毕，所以这里要处理已存在文件的问题
                if (!Files.exists(out)) {
                    //下载文件
                    Connection.Response response = Jsoup.connect(url).maxBodySize(0).ignoreContentType(true).timeout(timeout).execute();
                    //将文件保存到本地
                    Files.write(out, response.bodyAsBytes());
                    System.out.println("save resource to: " + out);
                } else {
                    System.out.println("resource exist, don't need to download");
                }
                success = true;
            } catch (Exception e) {
                System.out.println(e.getMessage()+" retry...");
            }
        }
    }
}
