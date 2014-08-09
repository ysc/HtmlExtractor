HtmlExtractor是一个Java实现的基于模板的网页结构化信息精准抽取组件，本身并不包含爬虫功能，但可被爬虫或其他程序调用以便更精准地对网页结构化信息进行抽取。

HtmlExtractor是为大规模分布式环境设计的，采用主从架构，主节点负责维护抽取规则，从节点向主节点请求抽取规则，当抽取规则发生变化，主节点主动通知从节点，从而能实现抽取规则变化之后的实时动态生效。

HtmlExtractor项目打成Jar包后运行在从节点上，而运行在主节点上的War包则是另外一个项目：[HtmlExtractorServer](https://github.com/ysc/HtmlExtractorServer)

如何使用：

    1、运行主节点，负责维护抽取规则：

    将项目https://github.com/ysc/HtmlExtractorServer打成War包然后部署到Tomcat

    2、获取一个HtmlExtractor的实例（从节点），示例代码如下：

    String allExtractRegularUrl = "http://localhost:8080/HtmlExtractorServer/api/all_extract_regular.jsp";
    String redisHost = "localhost";
    int redisPort = 6379;
    HtmlExtractor htmlExtractor = HtmlExtractor.getInstance(allExtractRegularUrl, redisHost, redisPort);

    3、抽取信息，示例代码如下：

    String url = "http://money.163.com/08/1219/16/4THR2TMP002533QK.html";
    List<ExtractResult> extractResults = htmlExtractor.extract(url, "gb2312");

    4、使用信息，示例代码如下：

    int i = 1;
    for (ExtractResult extractResult : extractResults) {
        System.out.println((i++) + "、网页 " + extractResult.getUrl() + " 的抽取结果");
        for(ExtractResultItem extractResultItem : extractResult.getExtractResultItems()){
            System.out.print("\t"+extractResultItem.getField()+" = "+extractResultItem.getValue());              
        }
        System.out.println("\tdescription = "+extractResult.getDescription());
        System.out.println("\tkeywords = "+extractResult.getKeywords());
    }