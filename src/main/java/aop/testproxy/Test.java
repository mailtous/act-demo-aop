package aop.testproxy;


import aop.utils.AddLogic;
import aop.utils.GenProxy;

import java.util.Date;

public class Test {

    public static void main(String[] args) {

        SayHello sayHello = new SayHello();

        AddLogic logic = new AddLogicImpl(sayHello);

        GenProxy<SayHello> genSubProxy = new GenProxy(logic);

        SayHello sh = (SayHello) genSubProxy.proxyOf(SayHello.class);

       // sh.hh((byte) 1, new byte[]{}, 1, 1f, 's', 1, 1, new int[][]{{12}}, "", new String[][]{{"sdf", "s"}}, new Date());

        sh.sayHello("sg", "srt", 234, new String[]{});

    }

}