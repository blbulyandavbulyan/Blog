package org.blbulyandavbulyan.blog.specs;

import lombok.Getter;
import org.blbulyandavbulyan.blog.entities.Article;
import org.springframework.data.jpa.domain.Specification;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Getter
public class ArticleSpecifications {
    private Specification<Article> articleSpecification;
    public ArticleSpecifications(Map<String, String> filterParams){
        articleSpecification = Specification.where(null);
        for(var entries : filterParams.entrySet()){
            if(entries.getKey().equals("maxDate")){
                articleSpecification = articleSpecification.and(untilDate(entries.getValue()));
            }
            else if(entries.getKey().equals("author")){
                articleSpecification = articleSpecification.and(authorLike(entries.getValue()));
            }
            else if(entries.getKey().equals("title")){
                articleSpecification = articleSpecification.and(titleLike(entries.getValue()));
            }
        }
    }

    public static Specification<Article> titleLike(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%%%s%%".formatted(title));
    }

    public static Specification<Article> authorLike(String author){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("publisher").get("name"), "%%%s%%".formatted(author));
    }
    public static Specification<Article> untilDate(String date){
        return (root, query, criteriaBuilder)-> criteriaBuilder.lessThanOrEqualTo(root.get("publishDate"), ZonedDateTime.parse(date,   DateTimeFormatter.ISO_DATE_TIME));
    }
}
