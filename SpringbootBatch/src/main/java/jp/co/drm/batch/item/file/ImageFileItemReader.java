package jp.co.drm.batch.item.file;

import java.io.File;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.core.io.Resource;

import jp.co.drm.common.util.CommUtils;

/**
 * Restartable {@link ItemReader} that reads lines from input
 * {@link #setResource(Resource)}. Line is defined by the
 * {@link #setRecordSeparatorPolicy(RecordSeparatorPolicy)} and mapped to item
 * using {@link #setLineMapper(LineMapper)}. If an exception is thrown during
 * line mapping it is rethrown as {@link FlatFileParseException} adding
 * information about the problematic line and its line number.
 *
 * @author Robert Kasanicky
 * @param <T>
 */
public class ImageFileItemReader implements ItemReader<File>, ItemReadListener<File> ,StepExecutionListener  {

	private  final Logger logger = LogManager.getLogger(this.getClass());
	private List<File> imgs;
	private int nextIndex;

	public ImageFileItemReader(String imgHome) {
		imgs = CommUtils.getFilsOfDir(imgHome);
		logger.info("画像件数：" + imgs.size());
	}

	@Override
	public File read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		File nextFile = null;

		if (nextIndex < imgs.size()) {
			nextFile = imgs.get(nextIndex);
			nextIndex++;
		}


		return nextFile;
	}

	@Override
	public void beforeRead() {


	}

	@Override
	public void afterRead(File item) {


	}

	@Override
	public void onReadError(Exception ex) {
		// TODO 自動生成されたメソッド・スタブ

	}



	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO 自動生成されたメソッド・スタブ
		logger.info("beforeStep画像件数：" + imgs.size());
		stepExecution.getExecutionContext().put("imageTotals", imgs.size() );

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}



}
