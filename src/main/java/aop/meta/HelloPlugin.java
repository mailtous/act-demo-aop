package aop.meta;

import act.app.App;
import act.app.AppByteCodeScanner;
import act.app.AppSourceCodeScanner;
import act.util.AppCodeScannerPluginBase;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/21/17
 */
public class HelloPlugin extends AppCodeScannerPluginBase {

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
