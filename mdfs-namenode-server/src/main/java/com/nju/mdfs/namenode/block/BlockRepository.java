package com.nju.mdfs.namenode.block;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends CrudRepository<Block,String> {

    List<Block> findByDataNodeURL(String dataNodeURL);
    List<Block> findAllByNodenameAndDataNodeURL(String nodename,String dataNodeURL);
    void deleteAllByNodename(String nodename);
}
