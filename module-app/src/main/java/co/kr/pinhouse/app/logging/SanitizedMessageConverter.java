package co.kr.pinhouse.app.logging;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import co.kr.pinhouse.common.util.LogSanitizer;

public class SanitizedMessageConverter extends MessageConverter {

	@Override
	public String convert(ILoggingEvent event) {
		return LogSanitizer.sanitizeMessage(event.getFormattedMessage());
	}
}
