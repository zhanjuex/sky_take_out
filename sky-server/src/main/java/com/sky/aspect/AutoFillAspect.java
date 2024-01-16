package com.sky.aspect;

import com.sky.annoatation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段的填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    // 拦截返回为所有类型，com.sky.mapper包下所有类所有方法里所有参数, 同时方法上要有AutoFill的注解
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annoatation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 在通知中进行公共字段赋值
     */
    // aop有切面 切点 通知（代码增强的部分）的概念
    // 拦截到之后要为公共字段赋值，即通知的时候改，并且要在sql执行之前改？？
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始公共字段自动填充");

        // 1. 获取当前被拦截的方法上的数据库操作类型

        // 方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获得方法上的注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 获取当前被拦截的方法的参数-实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;

        Object eneity = args[0];

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT) {
            try {
                Method setCreateTime = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setCreateTime.invoke(eneity, now);
                setCreateUser.invoke(eneity, currentId);
                setUpdateTime.invoke(eneity, now);
                setUpdateUser.invoke(eneity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                Method setUpdateTime = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = eneity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                setUpdateTime.invoke(eneity, now);
                setUpdateUser.invoke(eneity, currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
