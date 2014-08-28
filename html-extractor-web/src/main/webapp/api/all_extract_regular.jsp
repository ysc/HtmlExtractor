<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>  
[
    {
        "urlPattern": "http://money.163.com/\\d{2}/\\d{4}/\\d{2}/[0-9A-Z]{16}.html",
        "regexPattern": "/\\d{2}/\\d{4}/\\d{2}/[0-9A-Z]{16}.html",
        "pageTemplates": [
            {
                "templateName": "网易财经频道1",
                "tableName": "finance",
                "cssPaths": [
                    {
                        "fieldName": "title",
                        "cssPath": "h1",
                        "fieldDescription": "标题",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "content",
                        "cssPath": "div#endText",
                        "fieldDescription": "正文",
                        "extractFunctions": []
                    }
                ]
            },
            {
                "templateName": "网易财经频道2",
                "tableName": "finance",
                "cssPaths": [
                    {
                        "fieldName": "title",
                        "cssPath": "h1",
                        "fieldDescription": "标题",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "publishTime",
                        "cssPath": "html body div#js-epContent.ep-content div.ep-content-bg div#epContentLeft.ep-content-main div.ep-main-bg div.clearfix div.ep-info div.left",
                        "fieldDescription": "发表时间",
                        "extractFunctions": [
                            {
                                "fieldName": "publishTime",
                                "fieldDescription": "发表时间",
                                "extractExpression": "substring(0,19)"
                            }
                        ]
                    },
                    {
                        "fieldName": "content",
                        "cssPath": "div#endText",
                        "fieldDescription": "正文",
                        "extractFunctions": []
                    }
                ]
            },
            {
                "templateName": "网易财经栏目",
                "tableName": "finance",
                "cssPaths": [
                    {
                        "fieldName": "title",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l h1",
                        "fieldDescription": "标题",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "content",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l div.w_text",
                        "fieldDescription": "正文",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "author",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l div.author span.name",
                        "fieldDescription": "作者",
                        "extractFunctions": [
                            {
                                "fieldName": "author",
                                "fieldDescription": "作者",
                                "extractExpression": "removeText(作者：)"
                            }
                        ]
                    },
                    {
                        "fieldName": "introduction",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l div.introduction p",
                        "fieldDescription": "导语",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "followers",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l div.words_bbs div#tieArea.tie-area div#tiePostBox.tie-post div.tie-titlebar span.tie-info a.js-bactCount",
                        "fieldDescription": "跟贴人数",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "tieTotalCount",
                        "cssPath": "html body div#money_wrap.money_wrap div.common_wrap div.area div.w_main div.col_l div.author a.discuss span.tieTotalCount tieTotalCount",
                        "fieldDescription": "参与讨论人数",
                        "extractFunctions": []
                    }
                ]
            }
        ]
    },
    {
        "urlPattern": "http://finance.qq.com/a/\\d{8}/\\d{6}.htm",
        "regexPattern": "/a/\\d{8}/\\d{6}.htm",
        "pageTemplates": [
            {
                "templateName": "腾讯财经频道",
                "tableName": "finance",
                "cssPaths": [
                    {
                        "fieldName": "title",
                        "cssPath": "div#C-Main-Article-QQ div.hd h1",
                        "fieldDescription": "标题",
                        "extractFunctions": []
                    },
                    {
                        "fieldName": "content",
                        "cssPath": "div#Cnt-Main-Article-QQ",
                        "fieldDescription": "正文",
                        "extractFunctions": [
                            {
                                "fieldName": "content",
                                "fieldDescription": "正文",
                                "extractExpression": "deleteChild(div.ep-source)"
                            }
                        ]
                    }
                ]
            }
        ]
    }
]