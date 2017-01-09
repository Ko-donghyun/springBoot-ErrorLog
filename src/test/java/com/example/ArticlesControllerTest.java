package com.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * 컨트롤러를 테스트하는 테스트 클래스
 * Spring 에서 다른 테스트와 달리 컨트롤러 테스트는 몇가지 설정을 해야한다.
 * 컨트롤러는 사용자의 HTTP request를 처리하고 HTTP response를 반환하는 객체이기 때문에 이를 테스트하기 위해서는 웹 서버가 동작해야하고
 * 요청과 반환을 담당하는 HttpServletRequest / HttpServletResponse 를 직접 구현해야한다.
 *
 * Spring Boot 에서는 MockMvc를 이용하여 간단하게 테스트 할 수 있다.
 * MockMvc를 이용하면 URL 요청을 GET,POST,PUT,PATCH,DELETE 와 같은 REST 형태로 요청을 테스트할 수 있다
 *
 * 한가지 테스트에서 중요한 점은 기존의 REST 서비스를 위해 뷰 템플릿 없는 컨트롤러 테스트를 진행할 때는 테스트 환경을 standaloneSetup() 메소드로 컨트롤러의 MockMvc를 만들어서 테스트를 진행하였다.
 * 하지만 뷰 템플릿까지 모두 테스트를 진행하기 위해서는 컨트롤러 객체만 필요한 것이 아니라 Web Application 전체의 자원이 필요하기 때문에
 * webAppContextSetup() 메소드로 MockMvc를 만들어서 테스트를 진행해야한다.
 *
 * 또한 테스트에서 Web Application 모든 설정을 가져오기 위해서 테스트 클래스 레벨에 @WebAppConfiguration 어노테이션을 추가해야하고
 * 이것을 autowired 할 WebApplicationContext 변수를 추가 해야한다.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringBootProjectApplication.class)
@WebAppConfiguration
public class ArticlesControllerTest {

  private Logger logger = Logger.getLogger(this.getClass());

  private MockMvc mockMvc;

  @Autowired
  private ArticlesController articlesController;

  @Autowired
  WebApplicationContext wac;

  @Autowired
  private ArticlesService articlesService;

  @Before
  public void setUp() throws Exception {
//    mockMvc = standaloneSetup(articlesController).build();

//    MockitoAnnotations.initMocks(this);
    mockMvc = webAppContextSetup(wac).build();
  }

  private String jsonStringFromObject(Object object) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }

  @Test
  public void testNewArticle() throws Exception {
    MvcResult result = mockMvc.perform(get("/articles/new"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
      .andExpect(xpath("//input[@name='title']").exists())
      .andReturn();

    assertThat(result.getResponse().getContentAsString(), containsString("New Article"));

    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testFileUpload() throws Exception {
    MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());

    MvcResult result = mockMvc.perform(
      fileUpload("/articles").file(file)
        .param("title", "unittest title")
        .param("content", "unittest content"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andReturn();

    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testIndex() throws Exception {
    List<Article> articles = articlesService.getArticles();
    String jsonString = this.jsonStringFromObject(articles);

    // Spring 어플리케이션에 /api/articles Http 요청을 한 것과 동일한 테스트를 진행하게 된다.
    // 만약 정상적으로 컨트롤러가 요청을 받아서 처리하고 다시 Http 응답을 돌려준다면 status().isOk()가 나올 것이다.
    // 또한 response의 컨텐트 타입은 컨트롤러에서 @ResponseBody를 사용하여 만들어진 JSON 타입으로 응답이 온다.
    // 컨텐츠 내용을 확인할 때는 JSON 문자열로 결과가 올 것이기 때문에 JSON Mapper를 사용하여 객체를 JSON 문자열로 만들어서 response의 컨텐츠 문자열과 비교한다.
    MvcResult result = mockMvc.perform(get("/api/articles"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(content().string(equalTo(jsonString)))
      .andReturn();

    // response의 문자열을 로깅하고 싶을 경우는 MvcResult를 사용하여 로깅
    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testShow() throws Exception {
    long id = 1;
    Article article = articlesService.getArticle(id);
    String jsonString = this.jsonStringFromObject(article);

    mockMvc.perform(get("/api/articles/{id}", id))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
      .andExpect(content().string(equalTo(jsonString)));
  }

  @Test
  public void testCreate() throws Exception {
    Article article = new Article();
    article.setTitle("testing create article");
    article.setContent("test content");

    Comment comment = new Comment();
    comment.setContent("test comment1");
    List<Comment> comments = new ArrayList<>();
    comments.add(comment);

    article.setComments(comments);

    String jsonString = this.jsonStringFromObject(article);

    MvcResult result = mockMvc.perform(post("/api/articles")
      .contentType(MediaType.APPLICATION_JSON)
      .content(jsonString))
      .andExpect(status().isOk())
      .andExpect(content().string(equalTo(jsonString))).andReturn();

    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testPatch() throws Exception {
    long id = 1;
    Article article = articlesService.getArticle(id);
    article.setTitle("testing create article");
    article.setContent("test content");

    String jsonString = this.jsonStringFromObject(article);

    MvcResult result = mockMvc.perform(patch("/api/articles/{id}", id)
      .contentType(MediaType.APPLICATION_JSON)
      .content(jsonString))
      .andExpect(status().isOk())
      .andExpect(content().string(equalTo(jsonString))).andReturn();

    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testUpdate() throws Exception {
    long id = 1;
    Article article = articlesService.getArticle(id);
    article.setTitle("testing create article");
    article.setContent("test content");

    String jsonString = this.jsonStringFromObject(article);

    MvcResult result = mockMvc.perform(put("/api/articles/{id}", id)
      .contentType(MediaType.APPLICATION_JSON)
      .content(jsonString))
      .andExpect(status().isOk())
      .andExpect(content().string(equalTo(jsonString))).andReturn();

    logger.info(result.getResponse().getContentAsString());
  }

  @Test
  public void testDestroy() throws Exception {
    long id = 1;
    List<Article> articles = articlesService.deleteArticle(id);
    String jsonString = this.jsonStringFromObject(articles);

    mockMvc.perform(delete("/api/articles/{id}", id)
      .content(jsonString))
      .andExpect(status().isOk())
      .andExpect(content().string(equalTo(jsonString)));
  }
}