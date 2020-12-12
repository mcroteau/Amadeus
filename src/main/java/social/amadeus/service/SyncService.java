package social.amadeus.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SyncService {

    private static final Logger log = Logger.getLogger(SyncService.class);

    @Value("${digital.ocean.key}")
    private String key;

    @Value("${digital.ocean.secret}")
    private String secret;

    private static final String DO_REGION = "sfo2";
    private static final String DO_ENDPOINT = "rad.sfo2.digitaloceanspaces.com";

    public boolean send(String name, String bucket, InputStream stream){
        try {
            log.info(key  + " : " + secret);
            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(key, secret);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(DO_ENDPOINT, DO_REGION))
                    .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials)).build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest obj = new PutObjectRequest("", name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);

            s3Client.putObject(obj);

            return true;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
