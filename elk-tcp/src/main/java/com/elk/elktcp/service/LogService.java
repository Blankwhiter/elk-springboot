package com.elk.elktcp.service;

import com.elk.elktcp.entity.Log;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.util.List;

public interface LogService {
    List<SearchHit<Log>> findByMethodNameLike(String methodName);

}
