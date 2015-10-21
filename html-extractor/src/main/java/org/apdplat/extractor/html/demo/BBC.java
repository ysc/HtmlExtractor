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
    public static void main(String[] args) throws Exception{
        String path = "/Users/apple/百度云同步盘/BBC/";
        download("lower-intermediate", 30, path);
        download("intermediate", 25, path);
        download("emw", 15, path);
    }
    public static void download(String type, int count, String path) throws Exception{
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
                        if (href.endsWith(".mp3") || href.endsWith(".pdf")) {
                            String[] attr = href.split("/");
                            String fileName = attr[attr.length - 1];
                            System.out.println("unit " + i + "、find resource: " + href);
                            Path dir = Paths.get(path, type);
                            if(!Files.exists(dir)){
                                dir.toFile().mkdirs();
                            }
                            Path out = Paths.get(path, type, fileName);
                            if (!Files.exists(out)) {
                                Connection.Response response = Jsoup.connect(href).ignoreContentType(true).timeout(timeout).execute();
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
