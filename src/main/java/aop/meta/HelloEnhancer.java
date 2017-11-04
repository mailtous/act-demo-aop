package aop.meta;

import act.asm.AnnotationVisitor;
import act.asm.MethodVisitor;
import act.asm.Type;
import act.plugin.finder.Enhancer;
import act.util.AppByteCodeEnhancer;
import aop.Hello;
import org.osgl.util.C;
import org.osgl.util.S;

import java.util.Map;

/**
 * Function:
 *
 * @Autor: leeton
 * @Date : 11/1/17
 */
@Enhancer
public class HelloEnhancer extends AppByteCodeEnhancer<HelloEnhancer> {

    private String className;
    private Map<String, Object> annoData = C.newMap();

    public HelloEnhancer() {
        super(S.F.contains("Enhancer").negate()
                .and(S.F.startsWith("act").negate())
                .and(S.F.endsWith("aop.Hello").negate())
                .and(S.F.startsWith("aop.utils").negate())
        );
    }


    @Override
    protected Class<HelloEnhancer> subClass() {
        return HelloEnhancer.class;
    }


    private static boolean isHelloAnno(String desc) {
        return Type.getType(Hello.class).getDescriptor().equals(desc);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        className = Type.getObjectType(name).getClassName();
        System.err.println(" ======= ENCHANCE visit class: " + className);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        logger.debug(">>> HelloEnhancer to : %s , %s",className, name);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        act.asm.Type[] arguments = act.asm.Type.getArgumentTypes(desc);
        String methodName = name;
        String methodType = desc;
        int methodAccess= access;
        String methodSignature = signature;
        String [] methodexecptions = exceptions;

        mv = new MethodVisitor(ASM5, mv) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor av = super.visitAnnotation(desc, visible);
                    if (isHelloAnno(desc)) {
                        annoData.put("mv", mv);
                        return new AnnotationVisitor(ASM5, av) {
                            @Override
                            public void visit(String name, Object ov) {
                                super.visit(name, ov);
                                logger.warn(" key: " + name + ", value: "+ ov);
                                annoData.put(name, ov);
                                annoData.putIfAbsent("methodName", methodName);
                                annoData.putIfAbsent("methodType", methodType);
                            }

                        };
                    }
                return av;
            }

        };

       return mv;

    }

    @Override
    public void visitEnd() {
        if (!annoData.isEmpty()) {
            // change method param value
//            MethodVisitor mv = (MethodVisitor)annoData.get("mv");
/*
            MethodVisitor mv = super.visitMethod(ACC_PUBLIC, "toSay2", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            mv.visitLdcInsn(annoData.get("name"));
            //mv.visitLdcInsn(new Integer(18).byteValue());
            mv.visitMethodInsn(INVOKEVIRTUAL, annoData.get("class").toString(),"toSay2", "(Ljava/lang/String;)V", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 0);
            mv.visitEnd();
*/

        //    new DoEnhancer().run();
        }
        super.visitEnd();
    }




}



