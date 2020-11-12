package com.elk.elktcp.annotation;

import com.elk.elktcp.aspect.AuditAspect;
import com.elk.elktcp.utils.SpringUtils;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AuditAspect.class, SpringUtils.class})
public @interface EnableEsAuditing {
}
