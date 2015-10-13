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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.List;
import java.util.Random;

/**
 * 如何抓取Js动态生成数据且以滚动页面方式分页的网页
 * 以抓取今日头条为例说明：http://toutiao.com/
 * Created by ysc on 10/13/15.
 */
public class Toutiao {
    public static void main(String[] args) throws Exception{

        //等待数据加载的时间
        //为了防止服务器封锁，这里的时间要模拟人的行为，随机且不能太短
        long waitLoadBaseTime = 3000;
        int waitLoadRandomTime = 3000;
        Random random = new Random(System.currentTimeMillis());

        //火狐浏览器
        WebDriver driver = new FirefoxDriver();
        //要抓取的网页
        driver.get("http://toutiao.com/");

        //等待页面动态加载完毕
        Thread.sleep(waitLoadBaseTime+random.nextInt(waitLoadRandomTime));

        //要加载多少页数据
        int pages=5;
        for(int i=0; i<pages; i++) {
            //滚动加载下一页
            driver.findElement(By.className("loadmore")).click();
            //等待页面动态加载完毕
            Thread.sleep(waitLoadBaseTime+random.nextInt(waitLoadRandomTime));
        }

        //输出内容
        //找到标题元素
        List<WebElement> elements = driver.findElements(By.className("title"));
        int j=1;
        for(int i=0;i<elements.size();i++) {
            try {
                WebElement element = elements.get(i).findElement(By.tagName("a"));
                //输出标题
                System.out.println((j++) + "、" + element.getText() + " " + element.getAttribute("href"));
            }catch (Exception e){
                System.out.println("ignore "+elements.get(i).getText()+" because "+e.getMessage());
            }
        }

        //关闭浏览器
        driver.close();
    }
}
