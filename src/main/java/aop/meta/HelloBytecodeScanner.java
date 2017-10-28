package aop.meta;

import act.app.AppByteCodeScannerBase;
import act.asm.AnnotationVisitor;
import act.asm.MethodVisitor;
import act.util.ByteCodeVisitor;
import act.util.ClassDetector;
import aop.Hello;
import org.osgl.$;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.C;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/21/17
 */
public class HelloBytecodeScanner extends AppByteCodeScannerBase {

    private HelloClassMetaInfo classInfo;
    private volatile HelloClassMetaInfoManager classInfoBase;
    private ClassDetector detector;
    private HelloMethodMetaInfo methodInfo;


    @Override
    protected boolean shouldScan(String className) {
        classInfo = new HelloClassMetaInfo();
//        System.out.println("className = [" + className + "]");
//        System.out.println(className.endsWith("Test"));
        return className.endsWith("Test");
    }


    @Override
    public void scanFinished(String className) {
        classInfoBase().register(classInfo);

    }

    private HelloClassMetaInfoManager classInfoBase() {
        if (null == classInfoBase) {
            synchronized (this) {
                if (null == classInfoBase) {
                    classInfoBase = new HelloClassMetaInfoManager();
                }
            }
        }
        return classInfoBase;
    }

    public static boolean isHelloAnno(String desc) {
        return act.asm.Type.getType(Hello.class).getDescriptor().equals(desc);
    }

    @Override
    protected void onAppSet() {
        System.out.println(" ===== DO  ON APP SET ======== ");
        this.start("AppTest");

    }


    @Override
    public ByteCodeVisitor byteCodeVisitor() {
       return new HelloVisitor();
    }

    class HelloVisitor extends ByteCodeVisitor {

        private String className;
        private String methodName;
        private Object value;
        private List<String> paramTypes;



        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            classInfo.className(name);
            System.out.println("visit class = [" + name + "]");
            act.asm.Type superType = act.asm.Type.getObjectType(superName);
            classInfo.superType(superType);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public void visitEnd()  throws NotAppliedException, $.Break {
            super.visitEnd();
            new HelloAnnotationPrecess(methodInfo,app()).apply();
        }


        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            act.asm.Type[] arguments = act.asm.Type.getArgumentTypes(desc);
            String methodName = name;
            paramTypes = C.newList();
            if (null != arguments) {
                for (act.asm.Type type : arguments) {
                    paramTypes.add(type.getClassName());
                }
            }

            return new MethodVisitor(ASM5, mv) {
                @Override
                public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                    AnnotationVisitor av = super.visitAnnotation(desc, visible);
                    act.asm.Type type = act.asm.Type.getType(desc);
                    String className = type.getClassName();

                    try {
                        Class<? extends Annotation> c = (Class<? extends Annotation>) Class.forName(className);

                        if (isHelloAnno(desc)) {
                            if (null == methodInfo) {
                                methodInfo = new HelloMethodMetaInfo(classInfo,paramTypes, methodName);
                                classInfo.addAction(methodInfo);
                            }

                            return new AnnotationVisitor(ASM5, av) {
                                @Override
                                public void visit(String name, Object ov) {
                                    super.visit(name, ov);
                                    methodInfo.annoDataMap.put(name, ov);
/*                                    Class<?> targetClass = $.classForName(classInfo.className(), Act.app().classLoader());
                                    Object instance = app().getInstance(targetClass);
                                    MethodAccess access = MethodAccess.get(targetClass);
                                   access.invoke(instance, methodName,value); */
                                }
                            };

                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return av;
                }
            };
        }
    }

    @Override
    public String toString() {
        return "HelloBytecodeScanner{" +
                ", classInfo=" + classInfo +
                ", classInfoBase=" + classInfoBase +
                '}';
    }
}