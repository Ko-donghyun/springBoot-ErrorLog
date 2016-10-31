package com.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller
@EnableAutoConfiguration
public class PostController {
  @RequestMapping(value = "/posts/new", method = RequestMethod.GET)
  public String newPost(Model model) {
    model.addAttribute("post", new Post());
    return "new";
  }

  @RequestMapping(value = "/posts", method = RequestMethod.POST)
  public String createPost(@ModelAttribute Post post, Model model) {
//    model.addAttribute("post", post);

    RestTemplate restTemplate = new RestTemplate();                            // 내부적으로 새로운 서버에 REST API 요청을 하기 위한 Rest Template 도구
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    String url = "https://rest-api-ko-donghyun.c9users.io/posts.json";         // 새로운 서버의 URL 변경
    Post postObj = restTemplate.postForObject(url, post, Post.class);          // 새로운 서버의 JSON 결과를 POJO로 매핑
    model.addAttribute("post", postObj);                                       // View 업데이트를 위한 Model에 POJO 객체 저장

    System.out.println(postObj.getTitle());
    System.out.println(postObj.getContent());

    return "show";
  }
}
