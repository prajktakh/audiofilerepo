package com.example.demo.controller;

import com.example.demo.entity.Ids;
import com.example.demo.service.AudioFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    AudioFileService audioFileService;

    @PostMapping
    public ResponseEntity<String> createResource(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.getContentType().equals("audio/mpeg")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type");
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(audioFileService.upload(file).toString());
        }
    }
    @GetMapping("/{id}")
    public byte[] getResource(@RequestHeader(HttpHeaders.RANGE) String range, @PathVariable Long id) throws IOException {
       return  audioFileService.get(range, id);
    }

    @DeleteMapping
    public Ids delete(@RequestParam String id) throws IOException {
        return  audioFileService.delete(id);
    }

}
