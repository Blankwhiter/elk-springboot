package com.elk.elktcp.service;

import java.util.Optional;

/**
 * 获得当前审计人员
 *
 * @param <T>
 */
public interface EsAuditorAware<T> {
    Optional<T> getCurrentAuditor();
}
