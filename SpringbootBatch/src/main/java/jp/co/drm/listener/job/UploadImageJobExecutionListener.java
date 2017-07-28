package jp.co.drm.listener.job;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

/**
 * Batch1のJobExecutionListener。Job実行の前後の処理を定義
 */
public class UploadImageJobExecutionListener extends JobExecutionListenerSupport {
	private  final Logger logger = LogManager.getLogger(this.getClass());

	public void beforeJob(JobExecution JobExecution) {
		logger.info("Start Time : " + new Date());
	}

	public void afterJob(JobExecution jobExecution) {
		logger.info("End Time : " + new Date());
	}
}
