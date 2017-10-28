package aop.meta;

import act.Act;
import act.app.App;
import act.event.meta.SimpleEventListenerMetaInfo;
import act.sys.meta.InvokeType;
import act.util.DestroyableBase;
import org.osgl.$;
import org.osgl.inject.BeanSpec;
import org.osgl.util.S;

import javax.enterprise.context.ApplicationScoped;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/20/17
 */
@ApplicationScoped
public final class HelloMethodMetaInfo extends DestroyableBase {
    private String id;
    private String name;
    private InvokeType invokeType;
    private HelloClassMetaInfo clsInfo;
    private List<BeanSpec> paramTypes;
    private Method method;
    public Map<String, Object> annoDataMap = new HashMap<>(); // 注解的值

    public HelloMethodMetaInfo(HelloClassMetaInfo clsInfo,String methodName){
        this.clsInfo = clsInfo;
        name(methodName);
    }

    public HelloMethodMetaInfo(final HelloClassMetaInfo clsInfo, final List<String> paramTypes,String methodName) {
        name(methodName);
        final App app = Act.app();
        this.clsInfo = clsInfo;
        Class<?> targetClass = $.classForName(clsInfo.className(), app.classLoader());
        $.Var<Method> var = $.var();
        HelloMethodMetaInfo.this.paramTypes = SimpleEventListenerMetaInfo.convert(paramTypes, clsInfo.className(), methodName, var);
        HelloMethodMetaInfo.this.method = var.get();

/*        app.jobManager().on(AppEventId.DEPENDENCY_INJECTOR_PROVISIONED, new Runnable() {
            @Override
            public void run() {
                $.Var<Method> var = $.var();
                HelloMethodMetaInfo.this.paramTypes = SimpleEventListenerMetaInfo.convert(paramTypes, clsInfo.className(), methodName, var);
                HelloMethodMetaInfo.this.method = var.get();

            }
        });*/


    }


    @Override
    protected void releaseResources() {
        clsInfo.destroy();
        super.releaseResources();
    }

    public HelloClassMetaInfo classInfo() {
        return clsInfo;
    }

    public HelloMethodMetaInfo name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return name;
    }

    public String fullName() {
        return S.concat(clsInfo.className(), ".", name());
    }

    public HelloMethodMetaInfo id(String id) {
        this.id = id;
        return this;
    }

    public String id() {
        return S.blank(id) ? fullName() : id;
    }

    public Method method() {
        if (null == method) {
            Class<?> c = $.classForName(classInfo().className(), Act.app().classLoader());
            if (null == paramTypes() || paramTypes().isEmpty()) {
                method = $.getMethod(c, name);
            } else {
                throw new IllegalStateException("method cannot have parameters for Job invoked before app fully loaded");
            }
        }
        return method;
    }

    public HelloMethodMetaInfo invokeStaticMethod() {
        invokeType = InvokeType.STATIC;
        return this;
    }

    public HelloMethodMetaInfo invokeInstanceMethod() {
        invokeType = InvokeType.VIRTUAL;
        return this;
    }

    public boolean isStatic() {
        return InvokeType.STATIC == invokeType;
    }

    public List<BeanSpec> paramTypes() {
        return paramTypes;
    }

    private String _invokeType() {
        if (null == invokeType) {
            return "";
        }
        switch (invokeType) {
            case VIRTUAL:
                return "";
            case STATIC:
                return "static ";
            default:
                assert false;
                return "";
        }
    }

//======================

    @Override
    public int hashCode() {
        return $.hc(fullName());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof HelloMethodMetaInfo) {
            HelloMethodMetaInfo that = (HelloMethodMetaInfo) obj;
            return $.eq(that.fullName(), fullName());
        }
        return false;
    }

    @Override
    public String toString() {
        S.Buffer sb = S.newBuffer();
        sb.append(_invokeType())
                .append(fullName());
        return sb.toString();
    }


}
