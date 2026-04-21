package co.kr.pinhouse.common.util;

import java.lang.reflect.Array;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class LogSanitizer {

	private static final Pattern URL_PATTERN = Pattern.compile("https?://\\S+");
	private static final Pattern NAME_FIELD_PATTERN = Pattern.compile(
		"((?:(?:name|username|nickname|user_name)|이름|닉네임)\\s*[:=]\\s*)(\"[^\"]*\"|'[^']*'|[^,\\]\\)}]+)",
		Pattern.CASE_INSENSITIVE);
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"(?i)(?<![A-Z0-9._%+-])[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}(?![A-Z0-9._%+-])");
	private static final Pattern IPV4_PATTERN = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b");
	private static final Pattern JWT_PATTERN = Pattern.compile(
		"\\b[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\.[A-Za-z0-9_-]{10,}\\b");
	private static final Pattern LONG_SECRET_PATTERN = Pattern.compile("\\b[A-Za-z0-9_]{24,}\\b");

	private LogSanitizer() {
	}

	public static String sanitizeMessage(String message) {
		if (message == null) {
			return null;
		}

		String sanitized = sanitizePlainText(message);
		sanitized = NAME_FIELD_PATTERN.matcher(sanitized)
			.replaceAll(result -> result.group(1) + sanitizeNamedFieldValue(result.group(2)));
		return sanitized;
	}

	public static Object sanitize(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof UUID uuid) {
			return uuid.toString();
		}
		if (value instanceof CharSequence charSequence) {
			return sanitizeMessage(charSequence.toString());
		}
		if (value instanceof Optional<?> optional) {
			return optional.map(LogSanitizer::sanitize).orElse(null);
		}
		if (value instanceof Iterable<?> iterable) {
			return sanitizeIterable(iterable);
		}
		if (value instanceof Map<?, ?> map) {
			return sanitizeMap(map);
		}
		if (value.getClass().isArray()) {
			return sanitizeArray(value);
		}
		return value;
	}

	public static String sanitizeName(String value) {
		if (value == null) {
			return null;
		}

		String sanitized = sanitizePlainText(value).trim();
		if (sanitized.isBlank()) {
			return sanitized;
		}
		return maskName(sanitized);
	}

	private static Iterable<?> sanitizeIterable(Iterable<?> values) {
		var sanitizedValues = new ArrayList<>();
		for (Object value : values) {
			sanitizedValues.add(sanitize(value));
		}
		return sanitizedValues;
	}

	private static Map<Object, Object> sanitizeMap(Map<?, ?> values) {
		Map<Object, Object> sanitizedValues = new LinkedHashMap<>();
		for (Map.Entry<?, ?> entry : values.entrySet()) {
			sanitizedValues.put(sanitize(entry.getKey()), sanitize(entry.getValue()));
		}
		return sanitizedValues;
	}

	private static Iterable<?> sanitizeArray(Object array) {
		int length = Array.getLength(array);
		var sanitizedValues = new ArrayList<>(length);
		for (int index = 0; index < length; index++) {
			sanitizedValues.add(sanitize(Array.get(array, index)));
		}
		return sanitizedValues;
	}

	private static String sanitizeUrl(String rawUrl) {
		try {
			URI uri = new URI(rawUrl);
			StringBuilder builder = new StringBuilder();

			if (uri.getScheme() != null) {
				builder.append(uri.getScheme()).append("://");
			}
			if (uri.getHost() != null) {
				builder.append(uri.getHost());
			} else if (uri.getAuthority() != null) {
				builder.append(uri.getAuthority());
			}
			if (uri.getPort() != -1) {
				builder.append(':').append(uri.getPort());
			}
			if (uri.getRawPath() != null) {
				builder.append(sanitizePath(uri.getRawPath()));
			}
			if (uri.getRawQuery() != null) {
				builder.append('?').append(sanitizeQuery(uri.getRawQuery()));
			}
			if (uri.getRawFragment() != null) {
				builder.append("#***");
			}
			return builder.toString();
		} catch (URISyntaxException exception) {
			return maskMiddle(normalizeWhitespace(rawUrl), 6, 4);
		}
	}

	private static String sanitizePlainText(String value) {
		String sanitized = normalizeWhitespace(value);
		sanitized = URL_PATTERN.matcher(sanitized).replaceAll(result -> sanitizeUrl(result.group()));
		sanitized = EMAIL_PATTERN.matcher(sanitized).replaceAll(result -> maskEmail(result.group()));
		sanitized = IPV4_PATTERN.matcher(sanitized).replaceAll(result -> maskIpv4(result.group()));
		sanitized = JWT_PATTERN.matcher(sanitized).replaceAll(result -> maskMiddle(result.group(), 6, 4));
		sanitized = LONG_SECRET_PATTERN.matcher(sanitized).replaceAll(result -> maskMiddle(result.group(), 6, 4));
		return sanitized;
	}

	private static String sanitizeNamedFieldValue(String value) {
		String trimmedValue = value.trim();
		if (trimmedValue.length() >= 2
			&& ((trimmedValue.startsWith("\"") && trimmedValue.endsWith("\""))
			|| (trimmedValue.startsWith("'") && trimmedValue.endsWith("'")))) {
			String quote = trimmedValue.substring(0, 1);
			return quote + sanitizeName(trimmedValue.substring(1, trimmedValue.length() - 1)) + quote;
		}
		return sanitizeName(trimmedValue);
	}

	private static String sanitizePath(String path) {
		return Arrays.stream(path.split("/", -1))
			.map(LogSanitizer::sanitizePathSegment)
			.collect(Collectors.joining("/"));
	}

	private static String sanitizePathSegment(String segment) {
		if (segment == null || segment.isBlank()) {
			return segment;
		}

		String sanitized = EMAIL_PATTERN.matcher(segment).replaceAll(result -> maskEmail(result.group()));
		sanitized = JWT_PATTERN.matcher(sanitized).replaceAll(result -> maskMiddle(result.group(), 6, 4));
		sanitized = LONG_SECRET_PATTERN.matcher(sanitized).replaceAll(result -> maskMiddle(result.group(), 6, 4));
		return sanitized;
	}

	private static String sanitizeQuery(String query) {
		return Arrays.stream(query.split("&", -1))
			.map(LogSanitizer::sanitizeQueryParameter)
			.collect(Collectors.joining("&"));
	}

	private static String sanitizeQueryParameter(String parameter) {
		if (parameter.isBlank()) {
			return parameter;
		}

		int separatorIndex = parameter.indexOf('=');
		if (separatorIndex < 0) {
			return parameter;
		}
		return parameter.substring(0, separatorIndex + 1) + "***";
	}

	private static String maskEmail(String email) {
		int separatorIndex = email.indexOf('@');
		if (separatorIndex < 0) {
			return email;
		}

		String localPart = email.substring(0, separatorIndex);
		String domainPart = email.substring(separatorIndex + 1);
		if (localPart.length() <= 2) {
			return localPart.charAt(0) + "***@" + domainPart;
		}
		return localPart.substring(0, 2) + "***@" + domainPart;
	}

	private static String maskIpv4(String ipAddress) {
		String[] parts = ipAddress.split("\\.");
		if (parts.length != 4) {
			return ipAddress;
		}
		return parts[0] + "." + parts[1] + "." + parts[2] + ".***";
	}

	private static String maskMiddle(String value, int prefixLength, int suffixLength) {
		if (value == null || value.length() <= prefixLength + suffixLength) {
			return "***";
		}
		return value.substring(0, prefixLength) + "***" + value.substring(value.length() - suffixLength);
	}

	private static String maskName(String value) {
		return Arrays.stream(value.split("\\s+", -1))
			.map(LogSanitizer::maskNameToken)
			.collect(Collectors.joining(" "));
	}

	private static String maskNameToken(String value) {
		if (value == null || value.isBlank()) {
			return value;
		}
		if (value.length() == 1) {
			return "*";
		}
		if (value.length() == 2) {
			return value.charAt(0) + "*";
		}
		return value.charAt(0) + "*" + value.charAt(value.length() - 1);
	}

	private static String normalizeWhitespace(String value) {
		return value
			.replace('\r', ' ')
			.replace('\n', ' ')
			.replace('\t', ' ');
	}
}
