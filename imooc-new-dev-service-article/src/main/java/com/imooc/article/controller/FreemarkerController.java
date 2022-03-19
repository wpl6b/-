package com.imooc.article.controller;

import freemarker.template.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

@Controller
@RequestMapping("free")
public class FreemarkerController {

    @Value("${freemarker.html.target}")
    private String htmlTarget;

    @GetMapping("/createHTML")
    @ResponseBody
    public String createHTML(Model model) throws Exception {

        // 0. 配置freemarker基本环境
        Configuration cfg = new Configuration(Configuration.getVersion());
        // 声明freemarker模板所需要加载的目录的位置
        String classpath = this.getClass().getResource("/").getPath();

        cfg.setDirectoryForTemplateLoading(new File(classpath + "templates"));

        System.out.println(htmlTarget);
        System.out.println(classpath + "templates");

//        // 1. 获得现有的模板ftl文件
//        Template template = cfg.getTemplate("stu.ftl", "utf-8");
//
//        // 2. 获得动态数据
//        String stranger = "慕课网 imooc.com";
//        model.addAttribute("there", stranger);
////        model = makeModel(model);
//
//        // 3. 融合动态数据和ftl，生成html
//        File tempDic = new File(htmlTarget);
//        if (!tempDic.exists()) {
//            tempDic.mkdirs();
//        }
//
//        Writer out = new FileWriter(htmlTarget + File.separator + "10010" + ".html");
//        template.process(model, out);
//        out.close();

        return "ok";
    }





}
