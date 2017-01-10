package com.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
/**
 * 컨트롤러를 만들고 단위 테스트를 할 때 MockMvc를 사용했다.
 * MockMvc는 말 그대로 가짜 웹 서버와 Http request 만들어서 테스트하는 것이다.
 *
 * @WebIntegrationTest는 웹 서비스를 다르게 테스트할 수 있는 방법을 제시하고 있다.
 * @WebIntegrationTest는 @WebAppConfiguration 과 @IntegrationTest 가 통합되어 만들어진 것으로
 * Web Application 을 설정과 실제 웹 서버를 동작하여 테스트를 하는 것과 같이 테스트를 할 수 있는 방법을 제공하고 있다.
 *
 * @WebIntegrationTest(“server.port=0”) : 테스트를 위해서 동작하는 웹 서버 포트 번호를 지정할 수 있는데 이 값이 0이면 랜덤으로 테스트를 할 때 지정하여 동작하게 된다.
 * 이 때 지정된 포트번호는 @Value("{local.server.port}") int port; 형태로 injection으로 값을 가져올 수 있다.
 * WebIntegrationTest 방법으로 테스트를 진행할 때는 실제 테스트를 위한 웹 서버가 동작하는 것이기 때문에 서버에 접근할 수 있는 URL이 필요하다.
 * 우리는 포트번호를 랜덤하게 정하였기 때문에 기본적으로 URL을 만들기 위해서 String baseUrl변수를 만들었고
 * 이것은 테스트가 진행할 때 @before 테스트 시작 전에 포트번호를 가지고 URL의 앞부분을 만들 것이다.
 * 예를 들면 http://localhost:81268 와 같은 식으로 만들어지는 것이다. 그리고 우리는 웹 서버에 접근하여 Http Request를 요청하는 것을 RestTemplate으로 사용할 것이기 때문에 테스트 전에 객체를 생성하도록 하였다.
 *
 * WebIntegrationTest 및 IntegrationTest 모두 deprecated 됨
 * 따라서 SpringBootTest 를 이용하였다.
 * 포트 번호를 랜덤으로 생성하고, 실제 웹 서버를 동작하는 것과 동일하게 테스트를 진행
 *
 * application.properties 에서 포트를 설정할 수 있다
 * SpringBootTest.WebEnvironment.RANDOM_PORT 로 하게 되면 설정된 포트를 무시하고 랜덤 포트를 사용한다.
 * SpringBootTest.WebEnvironment.DEFINED_PORT 로 하게 되면 설정된 포트를 사용한다.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {SpringBootProjectApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestTemplateTest {

  Logger logger = Logger.getLogger(this.getClass());

  @LocalServerPort
  int port;

  @Autowired
  ArticlesService articlesService;

  private String baseUrl;
  RestTemplate restTemplate;

  @Before
  public void setUp() {
    restTemplate = new RestTemplate();
    baseUrl = "http://localhost:" +  String.valueOf(port);
    System.out.println(baseUrl);
  }

  private String jsonStringFromObject(Object object) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }


  /**
   * 이제 RestTemplate 을 사용하여 실제 웹 서버로 GET 요청을 해보자. RestTemplate 으로 Http GET 요청을 하는 방법는 여러가지 존재
   *
   * restTemplate.getForObject() : 기본 Http Header 를 사용하며 결과를 객체로 반환 받는다.
   * restTemplate.getForEntity() : 기본 Http Header 를 사용하며 결과를 Http ResponseEntity 로 반환 받는다.
   * restTemplate.exchange()     : Http Header 를 수정할 수 있고 결과를 Http ResponseEntity 로 반환 받는다.
   * restTemplate.execute()      : Request / Response 콜백을 수정할 수 있다.
   *
   * http://localhost:{port}/api/articles 로 RestTemplate 을 사용하여 HttpMethod.GET 요청을 하는 테스트
   * 이 때 결과 반환값을 JSON 문자열로 받고 싶어서 결과 반환 값을 String.class 로 지정하였다.
   *
   * restTemplate.getForObject(uri, 반환될 객체 타입) 으로 보면 된다.
   * RestTemplate 의 HttpMethod.GET 의 결과를 확인하기 위해서 로깅을 해보았다.
   *
   * 만약 RestTemplate 가 웹 서버에 정상적인 요청을 했다면 Article s의 List 타입이 JSON 으로 만들어져 보일것이다.
   * 컨트롤러를 요청한 결과과 맞는지 확인하기 위해서 ArticlesService.getArticles()로 가져오는 결과와 비교했다.
   */
  @Test
  public void testIndex() throws Exception {

    URI uri = URI.create(baseUrl+ "/api/articles");
    String responseString = restTemplate.getForObject(uri, String.class);

    // 컨트롤러 결과를 로깅
    logger.info(responseString);

    // 컨트롤러 결과를 확인하기 위한 데이터 가져오기
    List<Article> articles = articlesService.getArticles();
    String jsonString = jsonStringFromObject(articles);

    // 컨트롤러의 결과와 JSON 문자열로 비교
    assertThat(responseString, is(equalTo(jsonString)));
  }

  @Test
  public void testIndex2() throws Exception {
    URI uri = URI.create(baseUrl+ "/api/articles");

    // restTemplate.getObjectFor()에 반환되는 객체의 타입을 지정하면
    // JSON을 자동으로 반한되는 객체로 매핑해주는 것을 확인할 수 있다.
    List<Article> resultArticles = Arrays.asList(restTemplate.getForObject(uri, Article[].class));

    logger.info(resultArticles.size());
    logger.info(resultArticles.get(0).getId());

    // 컨트롤러 결과를 확인하기 위한 데이터 가져오기
    List<Article> articles = articlesService.getArticles();

    // 컨트롤러의 결과와 JSON 문자열로 비교
    assertThat(resultArticles.size(), is(equalTo(articles.size())));
    assertThat(resultArticles.get(0).getId(), is(equalTo(articles.get(0).getId())));
  }

  /**
   * 이제 RestTemplate 으로 실제 웹 서비스 형태로 테스트를 해보자. RestTemplate 에서 POST 를 요청하는 방법는 위에서 GET 을 요청하는 방법과 비슷하다.
   * 다만 getFor 로 시작하는 것을 postFor 로 바꿔주면 된다. 나머지는 동일하다.
   *
   * restTemplate.postForObject()
   * restTemplate.postForEntity()
   * restTemplate.exchange()
   * restTemplate.execute()
   *
   * MockMvc 에서 가짜로 테스트하는 것과 달리 RestTemplate 를 사용하여 실제 서버로 객체를 POST 로 보낼 때는 Article 의 객체를 그대로 넘겨주면 된다.
   */
  @Test
  public void testCreate() throws Exception {
    URI uri = URI.create(baseUrl + "/api/articles");

    Article article = new Article();
    article.setTitle("testing create article");
    article.setContent("test content");

    Comment comment = new Comment();
    comment.setContent("test comment1");
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);

    article.setComments(comments);

    Article resultArticle = restTemplate.postForObject(uri, article, Article.class);

    assertThat(resultArticle.getTitle(), is(equalTo(article.getTitle())));
  }
  @Test
  public void testCreate2() throws Exception {
    URI uri = URI.create(baseUrl + "/api/articles");

    Article article = new Article();
    article.setTitle("testing create article2222");
    article.setContent("test content2222");

    Comment comment = new Comment();
    comment.setContent("test comment2222");
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);

    article.setComments(comments);

    String responseString = restTemplate.postForObject(uri, article, String.class);
    String jsonString = jsonStringFromObject(article);

    assertThat(responseString, is(equalTo(jsonString)));
    logger.info(responseString);
  }

  /**
   * 다음은 HttpMethod.DELETE의 경우 RestTemplate에서 처리하는 방법을 살펴보자.
   * RestTemplate 에서 DELETE 와 PUT 에 관한 요청은 반환값을 가지지 않을 뿐만 아니라 파라미터 전송도 없다.
   * 컨트롤러에서 반환값을 갖기 위해서는 template.exchange()를 사용하면 된다. 만약 반환값에 상관없이 단순하게 DELETE 요청을 할 때는 template.delete()을 사용하여 요청하면 된다.
   *
   * restTemplate.delete()
   * restTemplate.exchange()
   * restTemplate.execute()
   *
   * relateTemplate을 사용하여 HttpMethod.DELETE 요청을 처리하는 방법은 다음과 같다.
   * 주석이 되어 있는 부분은 요청 후 반환값이 없을 때 간단하게 사용할 수 있는 방법이다.
   *
   * 만약 DELETE 요청 후 반환값이 필요하면 restTemplate.exchange()로 요청하면 되는데 이것은 앞에서 restTemplate 을 사용하는 방법과 달리
   * HttpHeaders 와 HttpEntity 를 사용하여 요청을 보내는 것을 확인할 수 있다. 그리고 exchange() 메소드에서 HttpMethod.DELETE 를 보낸다고 method 의 타입을 지정하는 것도 알 수 있다.
   * 이유는 exchange()는 말 그대로 사용자가 직접 전달하는 것을 정의하여서 보내는 것이기 때문에 모둔 HttpMethod 에서 동일하게 사용할 수 있는 방법이다.
   */
  @Test
  public void testDelete() throws Exception {
    long id = 1;
    URI uri = URI.create(baseUrl + "/api/articles/" + id);

    // 요청 후 반환값이 없을 때
//    Article article = articlesService.getArticle(id);
//    restTemplate.delete(uri);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity entity = new HttpEntity(headers);

    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);

    String jsonString = jsonStringFromObject(articlesService.deleteArticle(id));

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), is(equalTo(jsonString)));

    logger.info(responseEntity.getBody());
  }

  /**
   * HttpMethod.PUT 의 요청은 데이터를 업데이트하기 위한 요청을 하기 때문에 객체를 함께 보내야 한다.
   * 이 때 주의할 점은 객체를 보낼 때 HttpEntity 에 header 와 함께 보내는 것을 주의한다.
   */
  @Test
  public void testPut() throws Exception {
    long id = 1;
    URI uri = URI.create(baseUrl + "/api/articles/" +id);

    Article article = articlesService.getArticle(id);
    article.setTitle("testing create article");
    article.setContent("test content");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Article> entity = new HttpEntity(article, headers);

    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);

    String jsonString = jsonStringFromObject(article);

    assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    assertThat(responseEntity.getBody(), is(equalTo(jsonString)));
  }
}
