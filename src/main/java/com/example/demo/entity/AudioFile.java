package com.example.demo.entity;

import javax.persistence.*;
import java.io.File;

@Entity
public class AudioFile {
    @GeneratedValue
    @Id
    private Long id;
    private String fileName;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
