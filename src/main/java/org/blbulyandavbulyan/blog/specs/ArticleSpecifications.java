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
            articleSpecification = switch (entries.getKey()) {
                case "maxDate" -> articleSpecification.and(untilDate(entries.getValue()));
                case "minDate" -> articleSpecification.and(afterDate(entries.getValue()));
                case "author" -> articleSpecification.and(authorLike(entries.getValue()));
                case "title" -> articleSpecification.and(titleLike(entries.getValue()));
                default -> articleSpecification;
            };
        }
    }
    public static Specification<Article> afterDate(String date) {
        return (root, query, criteriaBuilder)-> criteriaBuilder.greaterThanOrEqualTo(root.get("publishDate"), ZonedDateTime.parse(date,   DateTimeFormatter.ISO_DATE_TIME));
    }

    public static Specification<Article> titleLike(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%%%s%%".formatted(title).toUpperCase());
    }

    public static Specification<Article> authorLike(String author){
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("publisher").get("name")), "%%%s%%".formatted(author).toUpperCase());
    }
    public static Specification<Article> untilDate(String date){
        return (root, query, criteriaBuilder)-> criteriaBuilder.lessThanOrEqualTo(root.get("publishDate"), ZonedDateTime.parse(date,   DateTimeFormatter.ISO_DATE_TIME));
    }
}
