package com.example.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.demo.entity.AudioFile;
import com.example.demo.entity.Ids;
import com.example.demo.repository.AudioFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AudioFileService {

    @Autowired
    AmazonS3 amazonS3;

    @Autowired
    AudioFileRepository audioFileRepository;

    @Value("aws.bucket")
    String bucketName;

    public Long upload(MultipartFile file) throws IOException {
        File convertedfile = convertMultipartFile(file);
        if(!amazonS3.doesBucketExistV2(bucketName)){
            amazonS3.createBucket(bucketName);
        }

        amazonS3.putObject(bucketName,file.getName(),convertedfile);
        AudioFile audioFile = new AudioFile();
        audioFile.setFileName(file.getName());
        audioFileRepository.save(audioFile);
        return audioFile.getId();
    }

    private File convertMultipartFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    public byte[] get(String range, Long id) throws IOException {
        if(range!=null && !range.isEmpty()) {
            String[] rangeArray = range.split("-");
            int startRange = Integer.valueOf(rangeArray[0]);
            int endRange = Integer.valueOf(rangeArray[1]);
            Optional<AudioFile> audioFile = audioFileRepository.findById(id);
            String fileName = audioFile.get().getFileName();
            GetObjectRequest getObjectRequest = new GetObjectRequest(fileName, bucketName);
            getObjectRequest.setRange(startRange, endRange);
            return IOUtils.toByteArray(amazonS3.getObject(getObjectRequest).getObjectContent());
        }
        else {
            Optional<AudioFile> audioFile = audioFileRepository.findById(id);
            String fileName = audioFile.get().getFileName();
            return IOUtils.toByteArray(amazonS3.getObject(fileName, bucketName).getObjectContent());
        }
    }

    public Ids delete(String id) {
        String[] fileIds = id.split(",");
        List<Long> idList = new ArrayList<>();
        for(String fileId: fileIds){
            try {
                Optional<AudioFile> audioFile = audioFileRepository.findById(Long.valueOf(fileId));
                amazonS3.deleteObject(audioFile.get().getFileName(), bucketName);
                idList.add(Long.valueOf(fileId));
            }catch (Exception e){
                System.out.println("Failed to delete object");
            }
        }
        Ids ids = new Ids();
         ids.setIds(idList);
         return ids;
 }
}
