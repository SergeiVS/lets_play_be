package org.lets_play_be.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Bean
    public AmazonS3 amazonS3(S3ConfigurationProperties properties){

        // Authorization on Storage
        AWSCredentials credentials = new BasicAWSCredentials(
                properties.getAccessKey(),
                properties.getSecretKey()
        );

        // Endpoint SetUp

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                properties.getEndpoint(),
                properties.getRegion()
        );

        // Create file loading client

        AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials));

        amazonS3ClientBuilder.setEndpointConfiguration(endpointConfiguration);

        return amazonS3ClientBuilder.build();
    }
}
