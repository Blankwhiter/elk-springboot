package com.elk.elktcp.aspect;

import com.elk.elktcp.service.EsAuditorAware;
import com.elk.elktcp.utils.SpringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审计切面
 */
@Component
@Aspect
public class AuditAspect {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 添加 更新 ES实体-切入点
     */
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository.save(..))")
    public void save() {
    }

    /**
     * 插入 更新实体拦截器.
     *
     * @param joinPoint
     * @throws IllegalAccessException
     */
    @Before("save()")
    public void beforeSave(JoinPoint joinPoint) throws IllegalAccessException {
        if (joinPoint.getArgs().length > 0) {
            Object esBaseEntity = joinPoint.getArgs()[0];
            Field[] fields = getAllFields(esBaseEntity.getClass());
            List<Field> fieldList = Arrays.stream(fields)
                    .filter(o -> o.getAnnotation(CreatedDate.class) != null
                            || o.getAnnotation(LastModifiedDate.class) != null)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(fieldList)) {
                for (Field field : fieldList) {
                    field.setAccessible(true);//取消私有字段限制
                    if (field.get(esBaseEntity) == null || field.getAnnotation(LastModifiedDate.class) != null) {
                        field.set(esBaseEntity, df.format(new Date()));
                    }
                }
            }
            List<Field> auditFieldList = Arrays.stream(fields)
                    .filter(o -> o.getAnnotation(CreatedBy.class) != null
                            || o.getAnnotation(LastModifiedBy.class) != null)
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(auditFieldList)) {
                for (Field field : auditFieldList) {
                    field.setAccessible(true);//取消私有字段限制
                    if (field.get(esBaseEntity) == null || field.getAnnotation(LastModifiedBy.class) != null) {
                        EsAuditorAware esAuditorAware = SpringUtils.getBean(EsAuditorAware.class);
                        if (esAuditorAware != null) {
                            field.set(esBaseEntity, esAuditorAware.getCurrentAuditor().orElse(null));
                        }
                    }
                }
            }
        }

    }


    /**
     * 获取本类及其父类的属性的方法
     *
     * @param clazz 当前类对象
     * @return 字段数组
     */
    private static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

}
