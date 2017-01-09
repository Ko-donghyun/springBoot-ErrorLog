package com.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

public class Article implements Serializable {
  private long id;
  private String title;
  private String content;
  private List<Comment> comments;

  @JsonIgnore
  private MultipartFile file;
  // 파일 명을 받고 싶을면 아래의 주석을 제거, 아래와 같이 file이라는 결과를 받을 수 있다.
  // {"id":0,"title":"제목","content":"아아아","comments":null,"file":"겨울 배경화면5.jpg"}

//  @JsonProperty("file")
//  private String fileName;
//
//  public String getFileName() {
//    return this.file.getOriginalFilename();
//  }

  public MultipartFile getFile() {
    return file;
  }

  public void setFile(MultipartFile file) {
    this.file = file;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }
}
