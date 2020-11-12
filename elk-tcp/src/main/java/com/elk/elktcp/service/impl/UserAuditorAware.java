package com.elk.elktcp.service.impl;

import com.elk.elktcp.service.EsAuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 用户审计 实现
 */
@Component
public class UserAuditorAware implements EsAuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("belongHuang");
    }
}
