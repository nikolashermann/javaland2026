package com.breuninger.nhermann.javaland2026.demoapiclient;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class ArticlesController {

  private static final String API_BASE_URL = "http://api-server:8080";
  private final RestClient restClient;

  public ArticlesController(RestClient restClient) {
    this.restClient = restClient;
  }

  @GetMapping({"", "/"})
  public String getList(Model model) {
    var uri =
        UriComponentsBuilder.fromUriString(API_BASE_URL).path("/api/articles").build().toUri();

    var articles = restClient.get().uri(uri).retrieve().body(Article[].class);
    model.addAttribute("articles", articles);

    return "list";
  }

  @GetMapping({"/article/{id}"})
  public String getArticle(@PathVariable String id, Model model) {
    var uri =
        UriComponentsBuilder.fromUriString(API_BASE_URL)
            .path("/api/articles/{id}")
            .buildAndExpand(id)
            .toUri();

    var article = restClient.get().uri(uri).retrieve().body(Article.class);
    model.addAttribute("article", article);

    return "details";
  }

  record Article(
      String id, String title, String image, String description, Double price, Long stock) {}
}
