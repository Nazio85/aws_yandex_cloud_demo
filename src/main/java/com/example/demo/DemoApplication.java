package com.example.demo;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Position;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(DemoApplication.class, args);
    }


    public static void test() throws IOException {
        String key = "_99999999999999999";
        AWSCredentials credentials = new BasicAWSCredentials(
                key,
                "99999999999999999-999_999999"
        );
        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AmazonS3ClientBuilder.EndpointConfiguration(
                "storage.yandexcloud.net", "ru-central1");

        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(endpointConfiguration)
                .build();

        Path path = Path.of("s1200.png");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream outputStream = new BufferedOutputStream(byteArrayOutputStream);

        Position position = (enclosingWidth, enclosingHeight, width, height, insetLeft, insetRight,
                             insetTop, insetBottom) -> new Point(0, 0);

        BufferedImage waterMark = getImage(IMG_WATERMARK_PNG);
        Thumbnails.of(Files.newInputStream(path))
                .size(1600, 1600)
                .watermark(position, waterMark, 0.9f)
                .outputFormat("jpg")
                .toOutputStream(outputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();

        String fileName = System.currentTimeMillis() + ".jpg";
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType(MediaType.IMAGE_JPEG_VALUE);


        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        PutObjectResult test123 = s3client.putObject("interior-storage", "users/" + fileName,
                byteArrayInputStream, meta);
        URL url = s3client.getUrl("interior-storage", "users/" + fileName);
        System.out.println(url);
    }

    public static BufferedImage getImage(String path) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext();
        Resource resource = context.getResource(path);
        return ImageIO.read(resource.getInputStream());
    }

    public static final String IMG_WATERMARK_PNG = "static/images/watermark.png";
    public static final String IMAGES_NOPIC_PNG = "images/nopic.jpg";

}
