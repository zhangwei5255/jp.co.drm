package jp.co.drm.batch.item.file;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
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
public class PartitionImageFileItemReader implements ItemReader<File> ,StepExecutionListener{

	private final Logger logger = LogManager.getLogger(this.getClass());

	//@Value("#{stepExecutionContext[name]}")
	private String threadName;

	private List<File> imgs;
	private int nextIndex;

	@Override
	public  File  read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		logger.info("read() threadName: " + threadName);

		File nextFile = null;

		if (nextIndex < imgs.size()) {
			nextFile = imgs.get(nextIndex);
			nextIndex++;
		}

		imgs.forEach(System.out::println);

		return nextFile;
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized void beforeStep(StepExecution stepExecution) {

		nextIndex = 0;

		logger.info("beforeStep threadName: " + threadName);
		List<String> fileNames =  (List<String>)stepExecution.getExecutionContext().get("imgs");
		imgs = fileNames.stream().map(p -> new File(p)).collect(Collectors.toList());

		fileNames.forEach(System.out::println);

		threadName = stepExecution.getExecutionContext().getString("name");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}



}
