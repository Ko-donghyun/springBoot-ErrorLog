package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST 컨트롤러가 데이터를 처리하는 ArticlesService 객체
 */

@Service
public class ArticlesService {
  @Autowired
  FixturesProperty fixturesProperty;

  public List<Article> getArticles() {
    List<Article> articles = new ArrayList<>(fixturesProperty.getArticles());
    return articles;
  }

  // getArticle()과 deleteArticle() 메소드 안에 리스트를 탐색하여 처리하는 작업은 Java 8의 lamda 표현식을 사용하여 구현
  public Article getArticle(long id) {
    List<Article> articles = this.getArticles();
    Article article = articles.stream()
      .filter(a -> a.getId() == id)
      .collect(Collectors.toList()).get(0);
    return article;
  }

  public List<Article> deleteArticle(long id) {
    List<Article> articles = this.getArticles();
    articles.removeIf(p -> p.getId() == id);
    return articles;
  }
}
