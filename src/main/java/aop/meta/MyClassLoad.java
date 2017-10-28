package aop.meta;

import act.app.App;
import act.app.AppClassLoader;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/24/17
 */
public class MyClassLoad extends AppClassLoader{
    public MyClassLoad(App app) {
        super(app);
    }

    public void scan() {
        super.scan();
    }

    public void preLoadClass() {
        super.preloadClasses();
    }
}
