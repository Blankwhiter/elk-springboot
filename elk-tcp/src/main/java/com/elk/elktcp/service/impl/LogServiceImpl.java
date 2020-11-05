package com.elk.elktcp.service.impl;

import com.elk.elktcp.dao.LogDao;
import com.elk.elktcp.entity.Log;
import com.elk.elktcp.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("logService")
public class LogServiceImpl implements LogService {

    @Autowired
    private LogDao logDao;

    @Override
    public  List<SearchHit<Log>> findByMethodNameLike(String methodName) {
        return logDao.findByMethodNameLike(methodName);
    }

}
