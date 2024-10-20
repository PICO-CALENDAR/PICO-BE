package com.pico.server.security.config;

import com.pico.server.security.config.userid.LoginUserArgumentResolver;
import com.pico.server.security.interceptor.LoginInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
            .excludePathPatterns("/api/v1/dev/**")
            .excludePathPatterns("/api/v1/auth/**")
            .excludePathPatterns("/api/v1/users/register")
            .excludePathPatterns("/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs", "/error");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
