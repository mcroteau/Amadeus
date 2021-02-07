package social.amadeus.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@PropertySource("classpath:application.properties")
public class S3Service {

    private static final String BUCKET = "goamadeus";

    @Value("${aws.access.key.id}")
    private String key;

    @Value("${aws.secret.access.key}")
    private String secret;

    public PutObjectResult send(String name, InputStream stream){
        try {
            Regions region = Regions.US_EAST_1;
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest putObj = new PutObjectRequest(S3Service.BUCKET, name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            return s3.putObject(putObj);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
