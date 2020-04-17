package com.jd.platform.hotkey.dashboard.common.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;


@Configuration
public class MyWebAppConfigurer  implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//拦截路径可自行配置多个 可用 ，分隔开
		InterceptorRegistration registration = registry.addInterceptor(new JwtInterceptor()).addPathPatterns("/**");
		registration.excludePathPatterns("/admin/login","/error","/static/**");
	}


	/** 解决跨域问题 **/
	@Override
	public void addCorsMappings(CorsRegistry registry){
		/*
			registry.addMapping("/**")
			// 设置允许跨域请求的域名
			.allowedOrigins("*")
			// 是否允许证书
			.allowCredentials(true)
			// 设置允许的方法
			.allowedMethods("GET", "POST", "DELETE", "PUT")
			// 设置允许的header属性
			.allowedHeaders("*")
			// 跨域允许时间
			.maxAge(3600);
			super.addCorsMappings(registry);
		*/
	}
	
	/** 静态资源处理 **/
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
		registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
    }
	/** 默认静态资源处理器 **/
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}


	
	
}
