package com.breuninger.nhermann.javaland2026.demoapiserver;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepository {
  private static final Map<String, Article> ARTICLES =
      Map.of(
          "1",
          new Article(
              "1",
              "T-Shirt",
              "https://cms.brnstc.de/product_images/2244x3072p/cpro/media/images/product/22/7/100135836814000_0_1657637860945.webp",
              "Das ist ein sehr schönes T-Shirt. Du möchtest das kaufen. Am besten gleich zweimal.",
              29.99,
              6L),
          "2",
          new Article(
              "2",
              "Hoodie",
              "https://cms.brnstc.de/product_images/2244x3072p/cpro/media/images/product/22/6/100135836618500_0_1655910392280.webp",
              "Ich bin auch schön! Und ich habe eine Kapuze! Kauf mich!",
              49.99,
              3L));

  Collection<Article> findAll() {
    return ARTICLES.values().stream().sorted(Comparator.comparing(Article::id)).toList();
  }

  Article findById(String id) {
    return ARTICLES.get(id);
  }
}
