package com.breuninger.nhermann.javaland2026.demoapiserver;

import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

  private final ArticleRepository articleRepository;

  public ApiController(ArticleRepository articleRepository) {
    this.articleRepository = articleRepository;
  }

  @GetMapping("/articles")
  public Collection<Article> getArticles() {
    return articleRepository.findAll();
  }

  @GetMapping("/articles/{id}")
  public Article getArticle(@PathVariable String id) {
    return articleRepository.findById(id);
  }
}
