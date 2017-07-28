package jp.co.drm.batch.partition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import jp.co.drm.common.util.CommUtils;

public class ImagePartitioner implements Partitioner {

	private  final Logger logger = LogManager.getLogger(this.getClass());

	private List<String> allImgName;

	private static final String PARTITION_KEY = "partition";
	private static final String DEFAULT_KEY_NAME = "imgs";
	private String keyName = DEFAULT_KEY_NAME;

	public ImagePartitioner(String imgHome) {
		allImgName = CommUtils.getFileNamesOfDir(imgHome);
	}

	@Override
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> result = new HashMap<String, ExecutionContext>();


		int pageCnt = allImgName.size() / gridSize;
		int mod = allImgName.size() % gridSize;
		List<String> curImgs;
		int fromIndex = 0;
		int toIndex = 0;

		List<Integer> lstPagecnt = new ArrayList<Integer>();

		for(int i = 1; i <= gridSize; i++){
			int curPageCnt = pageCnt;
			if(i <= mod){
				curPageCnt += 1;
			}

			lstPagecnt.add(curPageCnt);
		}

		for (int i = 1; i <= gridSize; i++) {
			int curPageCnt = lstPagecnt.get(i - 1);
			fromIndex = toIndex;
			toIndex = fromIndex + curPageCnt;
			curImgs = allImgName.subList(fromIndex, toIndex);

			ExecutionContext value = new ExecutionContext();

			logger.info("nStarting : Thread" + i);


			value.put(keyName, curImgs);

			// give each thread a name
			value.putString("name", "Thread" + i);

			result.put(PARTITION_KEY + i, value);
		}

		return result;
	}

}
