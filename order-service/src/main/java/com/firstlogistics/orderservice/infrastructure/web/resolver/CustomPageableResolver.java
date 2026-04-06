package com.firstlogistics.orderservice.infrastructure.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Set;

@Component
public class CustomPageableResolver extends PageableHandlerMethodArgumentResolver {

    private static final Set<Integer> ALLOWED_SIZES = Set.of(10, 30, 50);
    private static final int DEFAULT_SIZE = 10;

    @Override
    public Pageable resolveArgument(
            MethodParameter methodParameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        int pageSize = pageable.getPageSize();

        if (!ALLOWED_SIZES.contains(pageSize)) {
            pageSize = DEFAULT_SIZE;
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageSize,
                pageable.getSort()
        );
    }
}
