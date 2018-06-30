package com.nju.mdfs.namenode.node;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends CrudRepository<Node,Long> {
    List<Node> findByLocation(String location);
    Node findByName(String name);
    void deleteByName(String name);
}
