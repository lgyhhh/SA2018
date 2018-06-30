package com.nju.mdfs.datanode.block;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockRepository extends CrudRepository<Block,String> {
    Block findByBlockid(String blockid);
    void deleteByBlockid(String blockid);
}
