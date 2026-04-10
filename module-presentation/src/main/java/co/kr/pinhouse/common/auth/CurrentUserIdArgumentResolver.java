package co.kr.pinhouse.common.auth;

import java.util.UUID;

import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import co.kr.pinhouse.common.exception.code.CommonErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.security.principal.AuthenticatedUser;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUserId.class)
			&& UUID.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
		ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest,
		WebDataBinderFactory binderFactory) {
		CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null
			|| !authentication.isAuthenticated()
			|| authentication instanceof AnonymousAuthenticationToken) {
			return handleMissingPrincipal(annotation);
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof AuthenticatedUser authenticatedUser) {
			return authenticatedUser.getId();
		}

		return handleMissingPrincipal(annotation);
	}

	private Object handleMissingPrincipal(CurrentUserId annotation) {
		if (annotation != null && annotation.required()) {
			throw new CustomException(CommonErrorCode.UNAUTHORIZED);
		}
		return null;
	}
}
