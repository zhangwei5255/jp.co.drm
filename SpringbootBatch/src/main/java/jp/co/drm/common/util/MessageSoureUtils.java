package jp.co.drm.common.util;

import java.util.Locale;

import org.springframework.context.MessageSource;

public class MessageSoureUtils {

	public static String getMessage(MessageSource messageSoure, String code){
		return messageSoure.getMessage(code, null, Locale.getDefault());
	}

	public static String getMessage(MessageSource messageSoure, String code, Object[] args){
		return messageSoure.getMessage(code, args, Locale.getDefault());
	}

}
