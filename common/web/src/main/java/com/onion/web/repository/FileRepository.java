package com.onion.web.repository;

import com.onion.web.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhonglin on 16-1-7.
 */
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Integer> {

}
