package aop.meta;

import act.app.App;
import act.inject.param.ParamValueLoaderManager;
import act.inject.param.ParamValueLoaderService;
import act.sys.Env;
import act.util.ActContext;
import act.util.ReflectedInvokerHelper;
import com.esotericsoftware.reflectasm.MethodAccess;
import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.inject.BeanSpec;
import org.osgl.util.E;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/26/17
 */
public class HelloAnnotationPrecess<M extends HelloMethodMetaInfo> extends $.F0<Object>{

    private App app;
    private ClassLoader cl;
    private HelloClassMetaInfo classInfo;
    private Class<?> targetClass;
    private MethodAccess methodAccess;
    private M methodInfo;
    private int methodIndex;
    protected Method method;
    private boolean disabled;
    private ParamValueLoaderService paramValueLoaderService;
    private Object singleton;
    private boolean isStatic;

    public HelloAnnotationPrecess(M handlerMetaInfo, App app) {
        this.cl = app.classLoader();
        this.methodInfo = handlerMetaInfo;
        this.classInfo = handlerMetaInfo.classInfo();
        this.app = app;

    }

    private void init() {
        disabled = false;
        targetClass = $.classForName(classInfo.className(), cl);
        methodAccess = MethodAccess.get(targetClass);
        disabled = disabled || !Env.matches(targetClass);
        method = methodInfo.method();
        disabled = disabled || !Env.matches(method);
        if (disabled) {
            return;
        }
        isStatic = methodInfo.isStatic();
        if (!isStatic) {
            singleton = ReflectedInvokerHelper.tryGetSingleton(targetClass, app);
        }
        ParamValueLoaderManager paramValueLoaderManager = app.service(ParamValueLoaderManager.class);
        if (null != paramValueLoaderManager) {
            paramValueLoaderService = paramValueLoaderManager.get(ActContext.class);
        } else {
            // this job is scheduled to run before ParamValueLoaderManager initialized
        }


        if (!Modifier.isStatic(method.getModifiers())) {
            Class[] paramTypes = paramTypes();
            methodIndex = methodAccess.getIndex(methodInfo.name(), paramTypes);
        } else {
            method.setAccessible(true);
        }
    }

    @Override
    public Object apply() throws NotAppliedException, $.Break {
        if (null == targetClass) {
            init();
        }
        if (disabled) {
            return null;
        }
        Object job = getInstance(app);
        return invoke(job);
    }

    private Class[] paramTypes() {
        List<BeanSpec> paramTypes = methodInfo.paramTypes();
        int sz = null == paramTypes ? 0 : paramTypes.size();
        Class[] ca = new Class[sz];
        for (int i = 0; i < sz; ++i) {
            BeanSpec spec = methodInfo.paramTypes().get(i);
            ca[i] = spec.rawType();
        }
        return ca;
    }


    private Object getInstance(App app) {
        if (isStatic) {
            return null;
        }
        if (null != singleton) {
            return singleton;
        }
        return null != paramValueLoaderService ? paramValueLoaderService.loadHostBean(targetClass, ActContext.Base.currentContext())
                : app.getInstance(targetClass);
    }

    private Object invoke(Object instance) {
        Object[] params = paramTypes();
        Object result;
        if (null != methodAccess) {
            result = methodAccess.invoke(instance, methodIndex, methodInfo.annoDataMap.values().toArray());
        } else {
            try {
                result = method.invoke(instance, methodInfo.annoDataMap.values().toArray());
            } catch (InvocationTargetException e) {
                throw E.unexpected(e.getCause());
            } catch (Exception e) {
                throw E.unexpected(e);
            }
        }
        return result;
    }


}
