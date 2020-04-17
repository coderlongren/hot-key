package com.jd.platform.hotkey.dashboard.common.log;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;


@Aspect
@Component
@EnableAsync
public class LogAspect
{
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);


    // 配置织入点
    @Pointcut("@annotation(com.jd.platform.hotkey.dashboard.common.log.Log)")
    public void logPointCut(){
    }


    @AfterReturning(pointcut = "logPointCut()")
    public void doBefore(JoinPoint joinPoint)
    {
        handleLog(joinPoint, null);
    }

    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfter(JoinPoint joinPoint, Exception e)
    {
        handleLog(joinPoint, e);
    }

    @Async
    protected void handleLog(final JoinPoint joinPoint, final Exception e){
        try
        {
            // 获得注解
         /*   Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null)
            {
                return;
            }*/

            // 获取当前的用户
          //  TsysUser currentUser = null;  // getUser();

            // *========数据库日志=========*//
          //  TsysOperLog operLog = new TsysOperLog();
           
            //赋值操作
            /*String ip = ShiroUtils.getIp();
            operLog.setOperIp(ip);*/
            // 操作地点
            //operLog.setOperLocation(AddressUtils.getRealAddressByIP(ip));
            // 请求的地址
        //    operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            if ("currentUser" != null)
            {
//            	//操作人
             //   operLog.setOperName(currentUser.getUsername());
//                if (StringUtils.isNotNull(currentUser.getDept())
//                        && StringUtils.isNotEmpty(currentUser.getDept().getDeptName()))
//                {
//                    operLog.setDeptName(currentUser.getDept().getDeptName());
//                }
            }

            if (e != null)
            {
            	//错误日志
           // 	operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
         //   operLog.setMethod(className + "." + methodName + "()");
         //   operLog.setOperTime(new Date());
            // 处理设置注解上的参数
         //   getControllerMethodDescription(controllerLog, operLog);
            // 保存数据库
            //System.out.println("-----------------");
            //System.out.println(new Gson().toJson(operLog));
            //System.out.println("-----------------");
         //   operLogService.insertSelective(operLog);
        }
        catch (Exception exp)
        {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

/*
    public void getControllerMethodDescription(Log log) throws Exception
    {
        // 设置action动作
       // operLog.setAction(log.action());
        // 设置标题
     //   operLog.setTitle(log.title());
        // 设置channel
        //operLog.setChannel(log.channel());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData())
        {
            // 获取参数的信息，传入到数据库中。
       //     setRequestValue(operLog);
        }
    }

    private void setRequestValue(TsysOperLog operLog)
    {
        Map<String, String[]> map = ServletUtils.getRequest().getParameterMap();
        Gson gson=new Gson();
        String params = gson.toJson(map);
        operLog.setOperParam(StringUtils.substring(params, 0, 255));
    }

    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception
    {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null)
        {
            return method.getAnnotation(Log.class);
        }
        return null;
    }*/
}