package jp.co.drm.batch.item.file;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import jp.co.drm.aws.AWSClient;
import jp.co.drm.batch.item.dto.ImageDto;
import jp.co.drm.common.util.MessageSoureUtils;

@Component
public class ImageFileItemWriter implements ItemWriter<ImageDto> {

	@Autowired
	AWSClient awsClient;

	@Autowired
	MessageSource messageSource;

	public ImageFileItemWriter() {

	}


	@Override
	public void write(List<? extends ImageDto> items) throws Exception {
		String updatePath;

		awsClient.init();


		for(ImageDto dto : items){
			String fileName = dto.getOrigin().getName();
			String bucketName = messageSource.getMessage("aws_bucket_name", null, Locale.getDefault());

			// 全角を半角に変換する

			updatePath = messageSource.getMessage("aws_bucket_origin_path", null, Locale.getDefault())
					+ Normalizer.normalize(fileName, Normalizer.Form.NFKC);
			// 元画像アップロード
			awsClient.uploadFileWithNoAccess(dto.getOrigin().getPath(), bucketName,updatePath);

			// N
			updatePath =  MessageSoureUtils.getMessage(messageSource, "aws_bucket_n_path")
			+ Normalizer.normalize(fileName, Normalizer.Form.NFKC);
			awsClient.uploadFileWithReadAccess(dto.getN(), bucketName, updatePath);
			// S
			updatePath = MessageSoureUtils.getMessage(messageSource, "aws_bucket_s_path")
					+ Normalizer.normalize(fileName, Normalizer.Form.NFKC);
			awsClient.uploadFileWithReadAccess(dto.getS(), bucketName, updatePath);

		}

	}





}
