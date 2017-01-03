package com.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class ArticleController {
  @Resource
  private FixturesProperty fixturesProperty;

  @RequestMapping(value = "/article", method = RequestMethod.GET)
  public String getArticleData(Model model) {

    List<Article> articles = fixturesProperty.getArticles();

    System.out.println(articles);
    model.addAttribute("articles", articles);                                       // View 업데이트를 위한 Model에 POJO 객체 저장
    return "article";
  }
}
