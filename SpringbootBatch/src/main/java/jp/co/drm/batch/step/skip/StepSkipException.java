package jp.co.drm.batch.step.skip;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * BatchのJobSkip時の処理
 */
public class StepSkipException extends RuntimeException {

	private  final Logger logger = LogManager.getLogger(this.getClass());

	public StepSkipException(List<String> errMsgs) {
		// エラーメッセージ出力
		errMsgs.forEach(s -> logger.info(s));
	}
}
