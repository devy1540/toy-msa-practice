package com.practice.devy.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.practice.devy.utils.TypeCaster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@Slf4j
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    public JwtAuthenticationConverter() {
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                this.extractRoles(jwt).stream()
        ).collect(Collectors.toSet());

        JwtAuthenticationToken jwtAuthenticationToken = new JwtAuthenticationToken(jwt, authorities, this.getPrincipalClaimName(jwt));

        log.info(jwtAuthenticationToken.toString());

        return jwtAuthenticationToken;
    }

    private String getPrincipalClaimName(Jwt jwt) {
        return jwt.getClaim(JwtClaimNames.SUB);
    }

    /**
     * realm의 role과 resource의 모든 role을 가져와 하나의 GrantedAuthority로 생성
     * */
    private Collection<? extends GrantedAuthority> extractRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        Collection<String> realmRoles;
        Collection<String> resourceRoles;

        //realm role 검사
//        if(Objects.isNull(realmAccess)
//                || Objects.isNull((realmRoles = (Collection<String>) realmAccess.get("roles")))
//        ) {
//            realmRoles = Set.of();
//        }

        if(Objects.isNull(realmAccess)
                || Objects.isNull((realmRoles = TypeCaster.cast(realmAccess.get("roles"), new TypeReference<>() {})))
        ) {
            realmRoles = Set.of();
        }


        //resource role 검사
//        if(Objects.nonNull(resourceAccess)) {
//            resourceRoles = resourceAccess.values()
//                    .stream()
//                    .map(o -> ((Map<String, Object>) o).get("roles"))
//                    .map(o -> (Collection<String>) o)
//                    .flatMap(Collection::stream)
//                    .collect(Collectors.toSet());
//        } else {
//            resourceRoles = Set.of();
//        }

        if(Objects.nonNull(resourceAccess)) {
            resourceRoles = resourceAccess.values()
                    .stream()
                    .map(o -> TypeCaster.cast(o, new TypeReference<Map<String, Object>>() {}).get("roles"))
                    .map(o -> TypeCaster.cast(o, new TypeReference<Collection<String>>() {}))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
        } else {
            resourceRoles = Set.of();
        }

        return Stream.concat(realmRoles.stream(), resourceRoles.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
