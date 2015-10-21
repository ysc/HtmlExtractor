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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by ysc on 10/21/15.
 * 下载BBC Learning English在线课程
 */
public class BBC {
    public static void main(String[] args) {
        String path = "/Users/apple/百度云同步盘/BBC/";
        download("lower-intermediate", 30, path);
        download("intermediate", 25, path);
        download("emw", 15, path);
    }

    /**
     * BBC Learning English在线课程类型：
     * 1、lower-intermediate http://www.bbc.co.uk/learningenglish/english/course/lower-intermediate
     * 2、intermediate http://www.bbc.co.uk/learningenglish/english/course/intermediate
     * 3、emw http://www.bbc.co.uk/learningenglish/english/course/emw
     * @param type 课程类型
     * @param count 课数
     * @param path 保存到本地的路径
     */
    public static void download(String type, int count, String path) {
        int timeout = 300000;
        for(int i=1; i<=count; i++) {
            try {
                String url = "http://www.bbc.co.uk/learningenglish/english/course/" + type + "/unit-" + i + "/downloads";
                System.out.println("connect " + url);
                Document doc = Jsoup.connect(url).timeout(timeout).get();
                Elements elements = doc.select("a");
                for (Element element : elements) {
                    try {
                        String href = element.attr("href");
                        //只下载mp3和pdf文件
                        if (href.endsWith(".mp3") || href.endsWith(".pdf")) {
                            //提取文件名称
                            String[] attr = href.split("/");
                            String fileName = attr[attr.length - 1];
                            System.out.println("unit " + i + "、find resource: " + href);
                            //确保本地路径存储
                            Path dir = Paths.get(path, type);
                            if(!Files.exists(dir)){
                                //不存在则新建
                                dir.toFile().mkdirs();
                            }
                            //保存文件的完整本地路径
                            Path out = Paths.get(path, type, fileName);
                            //如果文件存在则表示之前已经下载过，本次不用下载
                            //因为BBC的访问不稳定，所以可能需要执行程序多次才能完整下载完毕，所以这里要处理已存在文件的问题
                            if (!Files.exists(out)) {
                                //下载文件
                                Connection.Response response = Jsoup.connect(href).ignoreContentType(true).timeout(timeout).execute();
                                //将文件保存到本地
                                Files.write(out, response.bodyAsBytes());
                                System.out.println("unit " + i + "、save resource to: " + out);
                            } else {
                                System.out.println("unit " + i + "、resource exist, don't need to download");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
