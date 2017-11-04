package aop.meta;

import act.app.App;
import act.app.AppByteCodeScannerBase;
import act.app.AppClassLoader;
import act.asm.AnnotationVisitor;
import act.asm.ClassVisitor;
import act.asm.MethodVisitor;
import act.asm.Type;
import act.mail.bytecode.MailerEnhancer;
import act.mail.bytecode.SenderEnhancer;
import act.mail.meta.MailerClassMetaInfoHolder;
import act.mail.meta.SenderMethodMetaInfo;
import act.plugin.finder.Scanner;
import act.util.AppByteCodeEnhancer;
import act.util.ByteCodeVisitor;
import act.util.ClassDetector;
import aop.Hello;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.osgl.$;
import org.osgl.Osgl;
import org.osgl.exception.NotAppliedException;
import org.osgl.util.C;
import org.osgl.util.S;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 10/21/17
 */
//@Scanner
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



    @Override
    public ByteCodeVisitor byteCodeVisitor() {
        return new HelloVisitor();
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
        return Type.getType(Hello.class).getDescriptor().equals(desc);
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
            Type superType = act.asm.Type.getObjectType(superName);
            classInfo.superType(superType);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public void visitEnd()  throws NotAppliedException, $.Break {
            super.visitEnd();
//            new HelloAnnotationPrecess(methodInfo,app()).apply();
        }


        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            act.asm.Type[] arguments = act.asm.Type.getArgumentTypes(desc);
            String methodName = name;
            String methodType = desc;
            int methodAccess= access;
            String methodSignature = signature;
            String [] methodexecptions = exceptions;

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
 /*                                   ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                                    //()V表示函数，无参数，无返回值
                                    org.objectweb.asm.MethodVisitor runMethod = cw.visitMethod(methodAccess, methodName, methodType, methodSignature, methodexecptions);
                                    //先获取一个java.io.PrintStream对象
                                    runMethod.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                                    //将int, float或String型常量值从常量池中推送至栈顶  (此处将message字符串从常量池中推送至栈顶[输出的内容])
                                    runMethod.visitLdcInsn("OKOKOK");
                                    //执行println方法（执行的是参数为字符串，无返回值的println函数）
                                    runMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
                                    runMethod.visitInsn(Opcodes.RETURN);
                                    runMethod.visitMaxs(1, 1);
                                    runMethod.visitEnd();*/

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