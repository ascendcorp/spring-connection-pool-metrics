package com.truemoney.api.springconnectionpoolmetrics.configuration;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.springframework.boot.actuate.metrics.web.servlet.DefaultWebMvcTagsProvider;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
class CustomMetricConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public WebMvcTagsProvider webMvcTagsProvider() {
        return new DefaultWebMvcTagsProvider() {
            @Override
            public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler, Throwable exception) {
                return Tags.of(super.getTags(request, response, handler, exception)).and(getRemovedTrailingSlashURI(request));
            }
        };
    }

    private Tag getRemovedTrailingSlashURI(HttpServletRequest request) {
        String uri = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (uri == null) {
            uri = request.getPathInfo();
        }
        if (!StringUtils.hasText(uri)) {
            uri = "/";
        }
        uri = uri.replaceAll("//+", "/").replaceAll("/$", "");
        return Tag.of("uri", uri.isEmpty() ? "root" : uri);
    }

}
