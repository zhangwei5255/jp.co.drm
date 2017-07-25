package jp.co.drm.batch.item.dto;

import java.io.File;
import java.io.Serializable;

public class ImageDto implements Serializable {

	private File origin;
	private byte[] s;
	private byte[] n;

	public byte[] getS() {
		return s;
	}
	public void setS(byte[] s) {
		this.s = s;
	}
	public byte[] getN() {
		return n;
	}
	public void setN(byte[] n) {
		this.n = n;
	}
	public File getOrigin() {
		return origin;
	}
	public void setOrigin(File origin) {
		this.origin = origin;
	}
}
