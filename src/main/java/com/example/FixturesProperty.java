package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component // 외부 프로퍼티 파일을 Injection 로드하기 위해서는 Spring의 컴포넌트가 되어야하기 때문에 @Component 어노테이션 추가
@ConfigurationProperties(locations = {"classpath:fixtures.yml"}, prefix = "fixtures") // 프로퍼티 파일을 로드하기 위한 클래스라는 것을 정의하기 위해 @ConfigurationProperties 어노테이션 추가
public class FixturesProperty {
  // FixtureProperty는 Spring의 @Component로 만들었기 때문에 스캐닝되고,
  // @ConfigurationProperty 때문에 YAML 파일을 오브젝트에 매핑되어 반환하게 될 것이다.

  @NestedConfigurationProperty
  private List<Article> articles = new ArrayList<>();

  public List<Article> getArticles() {
    return articles;
  }
}
