package jp.co.drm.batch.chunk.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import jp.co.drm.batch.item.dto.ImageDto;
import jp.co.drm.common.util.CommUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

@Component
public class ImageItemProcessor implements ItemProcessor<File, ImageDto> {

	@Autowired
	MessageSource messageSource;

	@Override
	public  ImageDto process(File item) throws Exception {
		File waterPic = new File(messageSource.getMessage("water_mark_path", null, Locale.getDefault()));

		File origin = item;
		byte[] s;
		byte[] n;
		int width;
		int height;

		width = Integer.valueOf(messageSource.getMessage("aws_bucket_n_width", null, Locale.getDefault()));
		height = Integer.valueOf(messageSource.getMessage("aws_bucket_n_height", null, Locale.getDefault()));
		n = getWaterMarkedImageData(origin, waterPic, width, height);

		width = Integer.valueOf(messageSource.getMessage("aws_bucket_s_width", null, Locale.getDefault()));
		height = Integer.valueOf(messageSource.getMessage("aws_bucket_s_height", null, Locale.getDefault()));
		s = getWaterMarkedImageData(origin, null, width, height);

		ImageDto dto = new ImageDto();
		dto.setOrigin(origin);
		dto.setN(n);
		dto.setS(s);

		return dto;
	}

	private byte[] getWaterMarkedImageData(File file, File waterPic, int width, int heigth) throws IOException {
		// 透かしを追加しない
		if (waterPic == null) {
			// 透かしを追加する
			BufferedImage bimg = Thumbnails.of(file).size(width, heigth).outputQuality(1f).asBufferedImage();

			return CommUtils.BufferedImage2Bytes(bimg, "JPG");
		}

		// 透かしを追加する
		BufferedImage bimg = Thumbnails.of(file).size(width, heigth)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(waterPic), 1f).outputQuality(1f).asBufferedImage();

		return CommUtils.BufferedImage2Bytes(bimg, "JPG");

	}

}
