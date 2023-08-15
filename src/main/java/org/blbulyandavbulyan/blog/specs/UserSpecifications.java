package org.blbulyandavbulyan.blog.specs;

import org.blbulyandavbulyan.blog.entities.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
public class UserSpecifications {
    private Specification<User> userSpecification;
    public UserSpecifications(MultiValueMap<String, String> filterParams) {
        userSpecification = Specification.where(null);
        for(var entries : filterParams.entrySet()){
            userSpecification = switch (entries.getKey()) {
                case "name" -> userSpecification.and(userNameLike(entries.getValue().get(0)));
                case "roles" -> {
                    Specification<User> rolesSpecification = Specification.where(null);
                    for(String roleName : entries.getValue()){
                        rolesSpecification = rolesSpecification.or(hasRole(roleName));
                    }
                    yield userSpecification.and(rolesSpecification);
                }
                default -> userSpecification;
            };
        }
    }

    private Specification<User> userNameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%%%s%%".formatted(name).toUpperCase());
    }
    private Specification<User> hasRole(String roleName){
        return (root, query, criteriaBuilder) -> criteriaBuilder.in(root.get("roles").get("name")).value(roleName);
    }
    public Specification<User> getSpecification(){
        return userSpecification;
    }
}
