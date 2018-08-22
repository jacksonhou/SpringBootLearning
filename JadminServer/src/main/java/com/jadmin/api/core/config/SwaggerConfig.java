package com.jadmin.api.core.config;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.ant;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.collect.Lists.newArrayList;
import static springfox.documentation.builders.PathSelectors.ant;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.OAuthBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationCodeGrant;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.GrantType;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.TokenEndpoint;
import springfox.documentation.service.TokenRequestEndpoint;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * SwaggerConfig
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig
{

    @Bean
    public Docket createRestApi()
    {

        return new Docket(DocumentationType.SWAGGER_2).
                useDefaultResponseMessages(false).select().apis(RequestHandlerSelectors.any()).paths(PathSelectors.regex("^(?!role).*$")).build()
                .apiInfo(productApiInfo()).securitySchemes(securitySchemes()).securityContexts(securityContexts());

    }

    private List<ApiKey> securitySchemes()
    {
        return newArrayList(new ApiKey("Authorization", "Authorization", "header"));
    }

    private List<SecurityContext> securityContexts()
    {
        return newArrayList(SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("^(?!role).*$")).build());
    }

    List<SecurityReference> defaultAuth()
    {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(new SecurityReference("Authorization", authorizationScopes));
    }

    private ApiInfo productApiInfo()
    {
        return new ApiInfoBuilder().title("使用Swagger2构建RESTful APIs").description("物华天宝 , 龙光射牛斗之墟 \r" + "人杰地灵 , 徐孺下陈蕃之榻")
                .termsOfServiceUrl("www.baidu.com").contact(new Contact("Q_先生", "www.baidu.com", "邮箱")).version("1.0").build();
    }
}