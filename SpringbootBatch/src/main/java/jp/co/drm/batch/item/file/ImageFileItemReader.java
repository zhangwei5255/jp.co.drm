package jp.co.drm.batch.item.file;

import java.io.File;
import java.util.List;

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
public class ImageFileItemReader implements ItemReader<File> {

	private List<File> imgs;
	private int nextIndex;

	public ImageFileItemReader(String imgHome) {
		imgs = CommUtils.getFilsOfDir(imgHome);
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



}
