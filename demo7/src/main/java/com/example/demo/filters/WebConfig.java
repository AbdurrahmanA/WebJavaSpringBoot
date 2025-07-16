package com.example.demo.filters;

import com.example.demo.filters.RememberMeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RememberMeFilter rememberMeFilter;

    @Bean
    public FilterRegistrationBean<RememberMeFilter> loggingFilter(){
        FilterRegistrationBean<RememberMeFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(rememberMeFilter);
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
