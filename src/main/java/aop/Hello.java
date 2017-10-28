package aop;

import act.app.App;
import act.app.AppByteCodeScanner;
import act.app.AppHolderBase;
import act.app.AppSourceCodeScanner;
import act.util.AppCodeScannerPluginBase;
import aop.meta.HelloBytecodeScanner;
import aop.meta.HelloMethodMetaInfo;
import org.osgl.logging.LogManager;
import org.osgl.logging.Logger;

import java.lang.annotation.*;

/**
 * AOP demo 把 name,age 传给加注解的方法
 * Created by leeton on 9/18/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Hello {
    String name() default "";
    int age() default 0;

/*
    class Plugin extends AppCodeScannerPluginBase {
        @Override
        public AppSourceCodeScanner createAppSourceCodeScanner(App app) {
            return null;
        }

        @Override
        public AppByteCodeScanner createAppByteCodeScanner(App app) {
            return new HelloBytecodeScanner();
        }

        @Override
        public boolean load() {
            return true;
        }
    }
*/



}
