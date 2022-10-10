package com.example.demo.repository;

import com.example.demo.entity.AudioFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFileRepository extends CrudRepository<AudioFile, Long> {


}
