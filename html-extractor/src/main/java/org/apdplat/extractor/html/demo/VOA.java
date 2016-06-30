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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * VOA 资源下载
 * Created by ysc on 06/28/16.
 */
public class VOA {
    public static void main(String[] args) throws Exception{
        String sourceDir = "/Users/ysc/workspace/HtmlExtractor/html-extractor/src/main/resources/voa/";
        String targetDir = "/Users/ysc/百度云同步盘/VOA/";

        AtomicInteger i = new AtomicInteger();
        download(sourceDir+"EnglishInAMinute.txt", targetDir+"English in a Minute").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"EnglishAtTheMovies.txt", targetDir+"English @ the Movies").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"EverydayGrammarTV.txt", targetDir+"Everyday Grammar TV").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"LearningEnglishTV.txt", targetDir+"Learning English TV").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"NewsWords.txt", targetDir+"News Words").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"PeopleInAmerica.txt", targetDir+"People In America").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));



        i.set(0);
        download(sourceDir+"Let'sLearnEnglish.txt", targetDir+"Let's Learn English").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));



        i.set(0);
        download(sourceDir+"What'sTrendingToday.txt", targetDir+"What's Trending Today").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"WordsandTheirStories.txt", targetDir+"Words and Their Stories").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));



        i.set(0);
        download(sourceDir+"PersonalTechnology.txt", targetDir+"Personal Technology").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"ScienceintheNews.txt", targetDir+"Science in the News").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"TheMakingofaNation.txt", targetDir+"The Making of a Nation").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"ThisIsAmerica.txt", targetDir+"This Is America").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));



        i.set(0);
        download(sourceDir+"Education.txt", targetDir+"Education").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"EverydayGrammar.txt", targetDir+"Everyday Grammar").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"HealthLifestyle.txt", targetDir+"Health & Lifestyle").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"IntheNews.txt", targetDir+"In the News").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));



        i.set(0);
        download(sourceDir+"America'sNationalParks.txt", targetDir+"America's National Parks").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"AmericanMosaic.txt", targetDir+"American Mosaic").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"AmericanStories.txt", targetDir+"American Stories").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));

        i.set(0);
        download(sourceDir+"AsItIs.txt", targetDir+"As It Is").stream().sorted().forEach(item->System.out.println(i.incrementAndGet()+". "+item));
    }

    public static Set<String> download(String sources, String target) throws Exception{
        Set<String> set = new HashSet<>();
        AtomicInteger i = new AtomicInteger();
        List<String> lines = Files.readAllLines(Paths.get(sources));
        lines.parallelStream()
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
                                href = findMp3(html);
                            }
                            if(StringUtils.isBlank(href)){
                                System.err.println(i.incrementAndGet() + ". not find resource, name: " + name + ", url: " + url);
                                set.add(line);
                                return;
                            }
                            System.out.println(i.incrementAndGet() + ". download resource, name: " + name + ", url: " + href);
                            String type = href.contains(".mp4") ? ".mp4" : (href.contains(".mp3") ? ".mp3" : (href.contains(".wav") ? ".wav" : ".unknow") );
                            download(href, target, name + type);
                            success = true;
                        } catch (Exception e) {
                            System.out.println(e.getMessage() + " retry...");
                        }
                    }
                });
        StringBuilder index = new StringBuilder();
        AtomicInteger k = new AtomicInteger();
        lines.forEach(line->index.append(k.incrementAndGet()).append(". <a target=\"_blank\" href=\"").append(line.trim().split("=")[0]).append("\">").append(line.trim().split("=")[1]).append("</a><br/>\n"));
        index.append("<br/><br/>can't be downloaded resources:<br/>");
        k.set(0);
        set.forEach(line->index.append(k.incrementAndGet()).append(". <a target=\"_blank\" href=\"").append(line.trim().split("=")[0]).append("\">").append(line.trim().split("=")[1]).append("</a><br/>\n"));
        Files.write(Paths.get(target+"/index.html"), Arrays.asList(index.toString().split("\n")));
        return set;
    }

    private static String findMp4(String html){
        Document document = Jsoup.parse(html);
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 1080P
            if (href.endsWith("_fullhq.mp4?download=1")) {
                return href;
            }
        }
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

    private static String findMp3(String html){
        Document document = Jsoup.parse(html);
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 384 kbps
            if (href.endsWith("_original.wav?download=1")) {
                return href;
            }
        }
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 128 kbps
            if (href.endsWith("_hq.mp3?download=1")) {
                return href;
            }
        }
        for(Element a : document.select("a")){
            String href = a.attr("href");
            // 64 kbps
            if (href.endsWith(".mp3?download=1")) {
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
