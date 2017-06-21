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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ysc on 10/21/15.
 * 下载BBC Learning English在线课程
 */
public class BBC {
    private static int count = 0;
    public static void main(String[] args) {
        String path = "/Users/ysc/百度云同步盘/BBC/";

        //***
        //Courses
        //***
        download("english-you-need", 30, path);
        download("towards-advanced", 30, path);
        download("lower-intermediate", 30, path);
        download("intermediate", 30, path);
        download("upper-intermediate", 30, path);
        download("shakespeare", 30, path);
        download("emw", 17, path);

        //***
        //Features
        //***
        download("http://www.bbc.co.uk/learningenglish/english/features/news-report",
                "News Report",
                path);
        download("http://www.bbc.co.uk/learningenglish/english/features/the-english-we-speak",
                "The English We Speak",
                path);
        download("http://www.bbc.co.uk/learningenglish/english/features/lingohack",
                "Lingohack",
                path);
        download("http://www.bbc.co.uk/learningenglish/english/features/6-minute-english",
                "6 Minute English",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/grammar/pron/sounds/",
                "The sounds of English",
                path,
                true,
                true);
        download("http://www.bbc.co.uk/learningenglish/english/features/drama",
                "Dramas from BBC Learning English",
                path);
        download("http://www.bbc.co.uk/learningenglish/english/features/witn",
                "Words in the News",
                path);

        //***
        //archived version
        //***
        //General & Business English
        download("http://www.bbc.co.uk/worldservice/learningenglish/general/sixminute/",
                "6 Minute English Archived",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/general/englishatwork/",
                "English at Work",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/general/expressenglish/",
                "Express English",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/general/talkaboutenglish/",
                "Talk about English",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/business/talkingbusiness/",
                "Talking Business",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/business/wab/",
                "Working Abroad",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/work/handy/",
                "Handy Guide",
                path);
        //Grammar, Vocabulary & Pronunciation
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/wordsinthenews/",
                "Words in the News Archived",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/theenglishwespeak/",
                "The English We Speak Archived",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/theteacher/",
                "The Teacher",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/newsextra/",
                "News English Extra",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/newsaboutbritain/",
                "News about Britain",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/askaboutenglish/",
                "Ask about English",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/uptodate/",
                "Keep your English up to date",
                path);
        download("http://www.bbc.co.uk/worldservice/learningenglish/language/faceup/",
                "Face Up to Phrasals",
                path);
        //Talking Sport
        download("http://www.bbc.co.uk/worldservice/learningenglish/talkingsport/",
                "Talking Sport",
                path);
        //Specials
        download("http://www.bbc.co.uk/worldservice/learningenglish/specials/",
                "Specials",
                path);

        System.out.println("total file count: " + count);
    }

    public static void download(String entranceURL, String type, String path){
        download(entranceURL, type, path, false, false);
    }
    /**
     * 下载课程
     * @param entranceURL 课程入口页面
     * @param type 课程类型
     * @param path 保存到本地的路径
     * @param containEntrance 是否下载入口页面上的课程
     * @param justOriginalName 是否使用原来的文件名称保存文件
     */
    public static void download(String entranceURL, String type, String path, boolean containEntrance, boolean justOriginalName){
        int timeout = 300000;
        if(!entranceURL.endsWith("/")){
            entranceURL += "/";
        }
        Set<String> urls = new HashSet<>();
        boolean ok = false;
        int limit=0;
        while (!ok && (limit++) < 3) {
            try {
                System.out.println("【"+type+"】*** connect " + entranceURL);
                for (Element element : Jsoup.connect(entranceURL).timeout(timeout).get().select("a")) {
                    String href = element.attr("href").trim();
                    if (!href.startsWith("http")) {
                        if(href.startsWith("/")){
                            href = "http://www.bbc.co.uk" + href;
                        }else{
                            href = entranceURL + href;
                        }
                    }
                    if (href.startsWith(entranceURL) && (!href.equals(entranceURL) || containEntrance) ) {
                        urls.add(href);
                    }
                }
                ok = true;
            } catch (Exception e) {
                System.out.println(e.getMessage() + " retry...");
            }
        }
        AtomicInteger i = new AtomicInteger(1);
        Set<String> resources = new HashSet<>();
        urls.stream().sorted().forEach(url -> {
            boolean success = false;
            int times=0;
            while (!success && (times++) < 3) {
                try {
                    System.out.println(i.get() + "、connect " + url);
                    for (Element element : Jsoup.connect(url).timeout(timeout).get().select("a")) {
                        String href = element.attr("href").trim();
                        //只下载mp3、mp4、wav和pdf文件
                        if (href.endsWith(".mp3") || href.endsWith(".wav") || href.endsWith(".mp4") || href.endsWith(".pdf")) {
                            if (!href.startsWith("http")) {
                                if(href.startsWith("/")){
                                    href = "http://www.bbc.co.uk" + href;
                                }else{
                                    String[] attr = url.split("/");
                                    href = url.substring(0, url.length()-attr[attr.length-1].length()) + href;
                                }
                            }
                            resources.add(href);
                        }
                    }
                    i.incrementAndGet();
                    success = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage() + " retry...");
                }
            }
        });
        AtomicInteger j = new AtomicInteger(1);
        count += resources.size();
        resources.stream().sorted().forEach(resource -> {
            boolean success = false;
            int times=0;
            while (!success && (times++) < 3) {
                try {
                    //提取文件名称
                    String[] attr = resource.split("/");
                    String fileName = attr[attr.length - 2] + "_" + attr[attr.length - 1].replace(attr[attr.length - 2], "");
                    if(attr[attr.length - 1].endsWith(attr[attr.length - 2])){
                        fileName = attr[attr.length - 1];
                    }
                    fileName = fileName.replace("_download", "");
                    if(justOriginalName){
                        fileName = attr[attr.length - 1];
                    }
                    System.out.println(resources.size() + "/" + j.get() + "、find resource: " + resource);
                    //确保本地路径存储
                    Path dir = Paths.get(path, type);
                    if (!Files.exists(dir)) {
                        //不存在则新建
                        dir.toFile().mkdirs();
                    }
                    //保存文件的完整本地路径
                    Path out = Paths.get(path, type, fileName);
                    //如果文件存在则表示之前已经下载过，本次不用下载
                    //因为BBC的访问不稳定，所以可能需要执行程序多次才能完整下载完毕，所以这里要处理已存在文件的问题
                    if (!Files.exists(out)) {
                        //下载文件
                        Connection.Response response = Jsoup.connect(resource).maxBodySize(0).ignoreContentType(true).timeout(timeout).execute();
                        //将文件保存到本地
                        Files.write(out, response.bodyAsBytes());
                        System.out.println(resources.size() + "/" + j.get() + "、save resource to: " + out);
                    } else {
                        System.out.println(resources.size() + "/" + j.get() + "、resource exist, don't need to download");
                    }
                    j.incrementAndGet();
                    success = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage() + " retry...");
                }
            }
        });
    }

    /**
     * BBC Learning English在线课程类型：
     * 1、lower-intermediate http://www.bbc.co.uk/learningenglish/english/course/lower-intermediate
     * 2、intermediate http://www.bbc.co.uk/learningenglish/english/course/intermediate
     * 3、emw http://www.bbc.co.uk/learningenglish/english/course/emw
     * @param type 课程类型
     * @param unitCount 课数
     * @param path 保存到本地的路径
     */
    public static void download(String type, int unitCount, String path) {
        int timeout = 300000;
        Set<String> hrefs = new HashSet<>();
        System.out.println("【"+type+"】*** starting... ");
        for(int i=1; i<=unitCount; i++) {
            int times=0;
            boolean success = false;
            while (!success && (times++) < 3) {
                try {
                    String url = "http://www.bbc.co.uk/learningenglish/english/course/" + type + "/unit-" + i + "/downloads";
                    System.out.println("unit " + i + "、connect " + url);
                    for (Element element : Jsoup.connect(url).timeout(timeout).get().select("a")) {
                        String href = element.attr("href").trim();
                        //只下载mp3、mp4、wav和pdf文件
                        if (href.endsWith(".mp3") || href.endsWith(".wav") || href.endsWith(".mp4") || href.endsWith(".pdf")) {
                            hrefs.add(href);
                        }
                    }
                    success = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage()+" retry...");
                }
            }
        }
        AtomicInteger i = new AtomicInteger(1);
        count += hrefs.size();
        hrefs.stream().sorted().forEach(href -> {
            int times=0;
            boolean success = false;
            while (!success && (times++) < 3) {
                try {
                    //提取文件名称
                    String[] attr = href.split("/");
                    String fileName = attr[attr.length - 2] + "_" + attr[attr.length - 1].replace(attr[attr.length - 2], "");
                    if(attr[attr.length - 1].endsWith(attr[attr.length - 2])){
                        fileName = attr[attr.length - 1];
                    }
                    fileName = fileName.replace("_download", "");
                    System.out.println(hrefs.size() + "/" + i.get() + "、find resource: " + href);
                    //确保本地路径存储
                    Path dir = Paths.get(path, type);
                    if (!Files.exists(dir)) {
                        //不存在则新建
                        dir.toFile().mkdirs();
                    }
                    //保存文件的完整本地路径
                    Path out = Paths.get(path, type, fileName);
                    //如果文件存在则表示之前已经下载过，本次不用下载
                    //因为BBC的访问不稳定，所以可能需要执行程序多次才能完整下载完毕，所以这里要处理已存在文件的问题
                    if (!Files.exists(out)) {
                        //下载文件
                        Connection.Response response = Jsoup.connect(href).maxBodySize(0).ignoreContentType(true).timeout(timeout).execute();
                        //将文件保存到本地
                        Files.write(out, response.bodyAsBytes());
                        System.out.println(hrefs.size() + "/" + i.get() + "、save resource to: " + out);
                    } else {
                        System.out.println(hrefs.size() + "/" + i.get() + "、resource exist, don't need to download");
                    }
                    i.incrementAndGet();
                    success = true;
                } catch (Exception e) {
                    System.out.println(e.getMessage()+" retry...");
                }
            }
        });
    }
}
