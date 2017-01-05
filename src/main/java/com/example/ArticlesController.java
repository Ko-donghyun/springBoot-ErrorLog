package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class ArticlesController {
  @Autowired
  ArticlesService articlesService;

  // 글 목록을 가져오는 /api/articles 처리를 위한 메소드
  @RequestMapping(value = "/api/articles", method = RequestMethod.GET)
  @ResponseBody
  // @RequestMapping 으로 메소드 이름과 달리 URL 이 들어오는 패턴을 메소드와 매핑 시킨다.
  // @ResponseBody 는 컨트롤러에서 데이터를 응답을 줄 때 객체를 HttpMessageConverter 를 사용하여 ResponseBody 에 자동으로 JSON 형태의 컨텐츠로 변환하여 반환한다
  public List<Article> index() {
    // 우리는 단지 객체를 리턴하기만하면 클라이언트에서 JSON 으로 받을 수 있다.
    return articlesService.getArticles();
  }

  @RequestMapping(value = "/api/articles/{id}", method = RequestMethod.GET)
  @ResponseBody
  public Article show(@PathVariable(value = "id") long id) {
    // 하나의 리소스의 아이템을 지정하기 위해서 @PathVariable 을 인자값으로 받아 들이고 있다는 것이다.
    return articlesService.getArticle(id);
  }

  // REST 에서 POST 메소드를 지원하는 것은 대부분 write 기능을 서비스하는 것이다.
  // 여기에는 중요한 보안 이슈가 있기 때문에 OAuth2와 같은 인증을 같이 처리하는 것이 좋다.

  // POST 로 받은 객체를 POJO 에 매팽하고 그것을 다시 @ResponseBody 로 응답
  @RequestMapping(value = "/api/articles", method = RequestMethod.POST)
  @ResponseBody
  public Article create(@RequestBody Article article) {
    // @RequestBody 는 @ResponseBody 와 동일한 형태의 어노테이션을 가지고 있고
    // HttpMessageConverter 를 사용하여 JSON 을 처리한다.
    // 클라이언트에서 이 REST URL 로 Article 을 저장하기 위해서 새로운 Article 을 전송할 때 Http Request 의 Body 에 JSON 타입으로 데이터가 넘어오게 되는 것이고,
    // 컨트롤러에서 @RequestBody 를 사용하여 JSON 을 객체로 매핑하게 되는 것이다.

    // 컨트롤러에서 JSON 타입의 Http Request 가 요청이 들어올 대 @RequestBody 를 사용하여 객체로 바로 매핑을 되는 것을 확인할 수 있다.
    /** Request JSON */
    /*
     * {
     *   "title": "aasfd",
     *   "content": "누구보다 빠르게 난 남들과는 다르게",
     *   "comments": [
     *     {
     *       "content": "test comment1"
     *     },
     *     {
     *       "content": "test comment1"
     *     }
     *   ]
     * }
     */
    System.out.println(article.getContent());

    return article;

    /** Return JSON */
    /*
     * {
     *   "id": 0,
     *   "title": "aasfd",
     *   "content": "누구보다 빠르게 난 남들과는 다르게",
     *   "comments": [
     *     {
     *       "id": 0,
     *       "content": "test comment1",
     *       "articleId": 0
     *     },
     *     {
     *       "id": 0,
     *       "content": "test comment1",
     *       "articleId": 0
     *     }
     *   ]
     * }
     */
  }

  // 이전에는 REST 서비스에서 리소스 업데이트 요청을 하기 위해서 PUT 메소드를 사용하였는데, 부분 업데이트 개념으로 PATCH 를 사용한다.
  // PUT 은 전체 리소스를 변경할 때 사용하는 것이고 PATCH 는 부분 변경을 사용할 때 사용

  @RequestMapping(value = "/api/articles/{id}", method = RequestMethod.PATCH)
  @ResponseBody
  public Article patch(@PathVariable(value = "id") long id,  @RequestBody Article article) {
    return article;
  }

  @RequestMapping(value = "/api/articles/{id}", method = RequestMethod.PUT)
  @ResponseBody
  public Article update(@PathVariable(value = "id") long id,  @RequestBody Article article) {
    return article;
  }

  @RequestMapping(value = "/api/articles/{id}", method = RequestMethod.DELETE)
  @ResponseBody
  public List<Article> destroy(@PathVariable(value = "id") long id) {
    return articlesService.deleteArticle(id);
  }
}
