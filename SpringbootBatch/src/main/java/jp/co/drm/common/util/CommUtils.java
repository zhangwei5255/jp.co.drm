package jp.co.drm.common.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class CommUtils {

	public static byte[] BufferedImage2Bytes(BufferedImage image,
            String imageFormat) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, imageFormat, bos);
		return bos.toByteArray();

	}
	public static  List<File> getFilsOfDir(File dir){

		List<File> lstFile = new ArrayList<File>();

		File[] files = dir.listFiles();

		for(File file : files) {

			if(!file.exists() || file.isHidden()){
				continue;
			}

			if(file.isDirectory()){
				List<File> subFiles = getFilsOfDir(file);
				lstFile.addAll(subFiles);
			}else if(file.isFile()){
				lstFile.add(file);
			}
		}

		return lstFile;

	}

	public static  List<File> getFilsOfDir(String homeDir){
		return getFilsOfDir(new File(homeDir));


	}


}
