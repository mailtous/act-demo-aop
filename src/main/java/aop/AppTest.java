package aop;

import act.Act;
import act.app.App;
import act.app.AppCodeScannerManager;
import act.app.event.AppEventId;
import act.job.OnAppEvent;
import act.job.OnAppStart;
import aop.meta.HelloBytecodeScanner;
import aop.meta.HelloPlugin;
import aop.meta.MyClassLoad;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/18/17
 */
public class AppTest {


    @Hello(name = "leeton",age=18)
    public void toSay(String who,Integer age) {
        System.out.println("========> MSG FROM ANNOTATION : " + who + ", age : " + age);
    }


    public static void main(String[] args) throws Exception {
        Act.start("mq");
    }

    @OnAppStart
    public void afterStarted() throws ClassNotFoundException {
        App app = Act.app();
        AppCodeScannerManager csm = app.scannerManager();
        HelloBytecodeScanner scanner = new HelloBytecodeScanner();
        csm.register(scanner);
        MyClassLoad loader = new MyClassLoad(app);
        loader.preLoadClass(); //重新加载系统的所有类
//        loader.loadClass("aop.AppTest");
        loader.scan();

        /*        App app = Act.app();
        AppCodeScannerManager csm = app.scannerManager();
        csm.register(new HelloBytecodeScanner()).byteCodeScanners();*/
        // toSay();
        // Act.scannerPluginManager().register(new HelloPlugin());
//        Act.registerPlugin(new HelloPlugin());

/*        Act.scannerPluginManager().register(new HelloPlugin());
        Act.scannerPluginManager().initApp(Act.app());*/

    }
}
