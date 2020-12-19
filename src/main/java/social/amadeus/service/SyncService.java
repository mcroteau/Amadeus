package social.amadeus.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import social.amadeus.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SyncService {

    private static final Logger log = Logger.getLogger(SyncService.class);

    @Value("${digital.ocean.key}")
    private String key;

    @Value("${digital.ocean.secret}")
    private String secret;

    public PutObjectResult send(String name, String bucket, InputStream stream){
        try {

            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(Constants.DO_ENDPOINT, Constants.DO_REGION))
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest obj = new PutObjectRequest("", name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            return s3Client.putObject(obj);

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
