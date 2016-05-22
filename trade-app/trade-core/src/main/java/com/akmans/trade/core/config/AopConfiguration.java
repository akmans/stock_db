package com.akmans.trade.core.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.CustomizableTraceInterceptor;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass=true)
@Aspect
public class AopConfiguration {
	/** Pointcut for execution of methods on {@link Service} annotation */
	@Pointcut("execution(public * (@org.springframework.stereotype.Service com.akmans.trade.core.service.impl..*).*(..))")
	public void serviceAnnotation() {
	}

	/** Pointcut for execution of methods on {@link Repository} annotation */
	@Pointcut("execution(public * (@org.springframework.stereotype.Repository com.akmans.trade.core.springdata.jpa.repositories..*).*(..))")
	public void repositoryAnnotation() {
	}

	/** Pointcut for execution of methods on {@link JpaRepository} interfaces */
//	@Pointcut("execution(public * org.springframework.data.jpa.repository.Repository+.*(..))")
//	public void jpaRepository() {
//	}

//	@Pointcut("serviceAnnotation() || repositoryAnnotation() || jpaRepository()")
	@Pointcut("serviceAnnotation() || repositoryAnnotation()")
	public void performanceMonitor() {
	}

	@Bean
	public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
		PerformanceMonitorInterceptor interceptor = new PerformanceMonitorInterceptor(true);
		interceptor.setLoggerName("org.springframework.aop.interceptor.CustomizableTraceInterceptor");
//		interceptor.setLogTargetClassInvocation(true);
		return interceptor;
	}

/*	@Bean
    public CustomizableTraceInterceptor customizableTraceInterceptor() {
        CustomizableTraceInterceptor customizableTraceInterceptor = new CustomizableTraceInterceptor();
//        customizableTraceInterceptor.setUseDynamicLogger(true);
        customizableTraceInterceptor.setLoggerName("org.springframework.aop.interceptor.CustomizableTraceInterceptor");
        customizableTraceInterceptor.setEnterMessage("Entering $[methodName]($[arguments])");
        customizableTraceInterceptor.setExitMessage("Leaving  $[methodName](), returned $[returnValue]");
        return customizableTraceInterceptor;
    }*/

	@Bean
	public Advisor performanceMonitorAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("com.akmans.trade.core.config.AopConfiguration.serviceAnnotation()");
//		pointcut.setExpression("execution(public * com.akmans.trade..*.*(..))");
		return new DefaultPointcutAdvisor(pointcut, performanceMonitorInterceptor());
	}
}
