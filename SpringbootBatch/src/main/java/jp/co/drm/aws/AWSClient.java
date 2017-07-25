package jp.co.drm.aws;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerConfiguration;
import com.amazonaws.services.s3.transfer.Upload;

import jp.co.drm.common.util.MessageSoureUtils;

/*
 * 今後、下記URLを参考してご改善ください。
 * http://www.ne.jp/asahi/hishidama/home/tech/aws/s3/api.html
 */
@Component
public class AWSClient {

	@Autowired
	MessageSource messageSoure;

	private static AmazonS3 s3;
	private static TransferManager tx;

    public void init() throws Exception {
    	if(s3 == null){
    		// 認証オブジェクトを作成
        	AWSCredentials credentials = new BasicAWSCredentials(MessageSoureUtils.getMessage(messageSoure, "aws_access_key_id"),
        			MessageSoureUtils.getMessage(messageSoure, "aws_secret_access_key"));
            // ConfigurationでTimeout時間を24時間(1秒 * 3600 * 24)に設定
            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setConnectionTimeout(1000 * 60 * 60 * 24);
/*            //add by zhangwei 20170707 「リクエストタイムアウト対応のため」start
            clientConfiguration.setRequestTimeout(1000 * 60 * 60 * 24);
            clientConfiguration.setSocketTimeout(1000 * 60 * 60 * 24);			//ClientConfiguration.DEFAULT_SOCKET_TIMEOUT
            //add by zhangwei 20170707 「リクエストタイムアウト対応のため」end
*/            s3 = new AmazonS3Client(credentials, clientConfiguration);
            // ドメイン「日本」を設定する
            s3.setEndpoint(MessageSoureUtils.getMessage(messageSoure, "aws_end_point"));

            tx = new TransferManager(s3);
            Long partSize = Long.valueOf(MessageSoureUtils.getMessage(messageSoure, "aws_bucket_part_size"));
            partSize = partSize * 1024L * 1024L;
            // 分割サイズを設定
            TransferManagerConfiguration c = new TransferManagerConfiguration();
            c.setMinimumUploadPartSize(partSize);
            tx.setConfiguration(c);
    	}

    }

    public  void createBucket(String bucketName) {
        if(s3.doesBucketExist(bucketName) == true) {
            System.out.println(bucketName + " already exist!");
            return;
        }
        System.out.println("creating " + bucketName + " ...");
        s3.createBucket(bucketName);
        System.out.println(bucketName + " has been created!");
    }

    public  void listObjects(String bucketName) {
        System.out.println("Listing objects of " + bucketName);
        ObjectListing objectListing = s3.listObjects(bucketName);

        int objectNum = 0;
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey());
            objectNum ++;
        }
        System.out.println("total " + objectNum + " object(s).");
    }

    public  List<String> listObjects(String bucketName, String prefix) {
    	List<String> fileNames = new ArrayList<String>();
    	String delimiter = "/";
    	String fileName;
    	ListObjectsRequest request = new ListObjectsRequest(bucketName, prefix, null, delimiter, null);

        ObjectListing list;
    	do {
    		list = s3.listObjects(request);
    		for (S3ObjectSummary s : list.getObjectSummaries()) {
    			System.out.println(String.format("bucket=%s, key=%s, size=%d\n", s.getBucketName(), s.getKey(), s.getSize()));
    			fileName = StringUtils.stripStart(s.getKey(), prefix);
    			fileNames.add(fileName);
    		}
    		request.setMarker(list.getNextMarker());
    	} while (list.isTruncated());

    	return fileNames;

    }

    public  boolean isObjectExit(String bucketName, String key) {
        int len = key.length();
        ObjectListing objectListing = s3.listObjects(bucketName);
        String s = new String();
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            s = objectSummary.getKey();
            int slen = s.length();
            if(len == slen) {
                int i;
                for(i=0;i<len;i++) if(s.charAt(i) != key.charAt(i)) break;
                if(i == len) return true;
            }
        }
        return false;
    }

    public  void createSampleFile(String bucketName, String filename) throws IOException {
        if(isObjectExit(bucketName, filename) == true) {
            System.out.println(filename +" already exists in " + bucketName + "!");
            return;
        }
        System.out.println("creating file " + filename);
        File file = new File(filename);
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        s3.putObject(bucketName, filename, file);
        System.out.println("create sample file " + filename + " succeed!");
    }

    public  void showContentOfAnObject(String bucketName, String key) {
        S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
        InputStream input = object.getObjectContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("    " + line);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showContentOfAnGzipObject(String bucketName, String key) {
        try {
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
            InputStream input = new GZIPInputStream(object.getObjectContent());
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                System.out.println("    " + line);
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listBuckets() {
        System.out.println("Listing buckets");
        int bucketNum = 0;
        for(Bucket bucket : s3.listBuckets()) {
            System.out.println(" - " + bucket.getName());
            bucketNum ++;
        }
        System.out.println("total " + bucketNum + " bucket(s).");
    }

    public void deleteBucket(String bucketName) {
        if(s3.doesBucketExist(bucketName) == false) {
            System.out.println(bucketName + " does not exists!");
            return;
        }
        System.out.println("deleting " + bucketName + " ...");
        ObjectListing objectListing = s3.listObjects(bucketName);
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            s3.deleteObject(bucketName, key);
        }
        s3.deleteBucket(bucketName);
        System.out.println(bucketName + " has been deleted!");
    }

    public void deleteObjectsWithPrefix(String bucketName, String prefix) {
        if(s3.doesBucketExist(bucketName) == false) {
            System.out.println(bucketName + " does not exists!");
            return;
        }
        System.out.println("deleting " + prefix +"* in " + bucketName + " ...");
        int pre_len = prefix.length();
        ObjectListing objectListing = s3.listObjects(bucketName);
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            int len = key.length();
            if(len < pre_len) continue;
            int i;
            for(i=0;i<pre_len;i++)
                if(key.charAt(i) != prefix.charAt(i))
                    break;
            if(i < pre_len) continue;
            s3.deleteObject(bucketName, key);
        }
        System.out.println("All " + prefix + "* deleted!");
    }

    public void uploadFileWithNoAccess(String path, String bucketName) throws Exception {
        File fileToUpload = new File(path);
        if(fileToUpload.exists() == false) {
            System.out.println(path + " not exists!");
            return;
        }

         // メタデータに分割したデータのサイズを指定
        ObjectMetadata putMetaData = new ObjectMetadata();
        putMetaData.setContentLength(fileToUpload.length());
        InputStream is = new FileInputStream(fileToUpload);
        PutObjectRequest request = new PutObjectRequest(bucketName, fileToUpload.getName(), is, putMetaData);
        Upload upload = tx.upload(request);
        upload.waitForCompletion();

        System.out.println(path + " upload succeed!");
    }

    public void uploadFileWithNoAccess(String path, String bucketName, String updatePath) throws Exception {
        File fileToUpload = new File(path);
        if(fileToUpload.exists() == false) {
            System.out.println(path + " not exists!");
            return;
        }

         // メタデータに分割したデータのサイズを指定
        ObjectMetadata putMetaData = new ObjectMetadata();
        putMetaData.setContentLength(fileToUpload.length());
        InputStream is = new FileInputStream(fileToUpload);
        PutObjectRequest request = new PutObjectRequest(bucketName, updatePath, is, putMetaData);
        Upload upload = tx.upload(request);
        upload.waitForCompletion();

        System.out.println(updatePath + " upload succeed!");
    }

    public void uploadFileWithNoAccess(byte[] bytes, String bucketName, String updatePath) throws Exception {

        // メタデータに分割したデータのサイズを指定
       ObjectMetadata putMetaData = new ObjectMetadata();
       putMetaData.setContentLength(bytes.length);
       InputStream is = new ByteArrayInputStream(bytes);;

  	   PutObjectRequest request = new PutObjectRequest(bucketName, updatePath, is, putMetaData);
       Upload upload = tx.upload(request);
       upload.waitForCompletion();
       System.out.println(updatePath + " upload succeed!");

   }


    public void uploadFileWithReadAccess(String path, String bucketName) throws Exception {
        File fileToUpload = new File(path);
        if(fileToUpload.exists() == false) {
            System.out.println(path + " not exists!");
            return;
        }

         // メタデータに分割したデータのサイズを指定
        ObjectMetadata putMetaData = new ObjectMetadata();
        putMetaData.setContentLength(fileToUpload.length());
        InputStream is = new FileInputStream(fileToUpload);
   	 	PutObjectRequest request = new PutObjectRequest(bucketName, fileToUpload.getName(), is, putMetaData);
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        Upload upload = tx.upload(request.withAccessControlList(acl));
        upload.waitForCompletion();
        System.out.println(path + " upload succeed!");

    }

    public void uploadFileWithReadAccess(String path, String bucketName, String updatePath) throws Exception {
        File fileToUpload = new File(path);
        if(fileToUpload.exists() == false) {
            System.out.println(path + " not exists!");
            return;
        }

         // メタデータに分割したデータのサイズを指定
        ObjectMetadata putMetaData = new ObjectMetadata();
        putMetaData.setContentLength(fileToUpload.length());
        InputStream is = new FileInputStream(fileToUpload);
   	 	PutObjectRequest request = new PutObjectRequest(bucketName, updatePath, is, putMetaData);
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        Upload upload = tx.upload(request.withAccessControlList(acl));
        upload.waitForCompletion();
        System.out.println(updatePath + " upload succeed!");

    }

    public void uploadFileWithReadAccess(byte[] bytes, String bucketName, String updatePath) throws Exception {

         // メタデータに分割したデータのサイズを指定
        ObjectMetadata putMetaData = new ObjectMetadata();
        putMetaData.setContentLength(bytes.length);
        InputStream is = new ByteArrayInputStream(bytes);;

   	 	PutObjectRequest request = new PutObjectRequest(bucketName, updatePath, is, putMetaData);
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
        Upload upload = tx.upload(request.withAccessControlList(acl));
        upload.waitForCompletion();
        System.out.println(updatePath + " upload succeed!");

    }

    public void createFolder(String bucketName, String folderName) {
        // Create metadata for my folder & set content-length to 0
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(0);
        // Create empty content
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        // Create a PutObjectRequest passing the foldername suffixed by /
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, folderName+"/",
                        emptyContent, metadata);
        // Send request to S3 to create folder
        s3.putObject(putObjectRequest);
    }


}