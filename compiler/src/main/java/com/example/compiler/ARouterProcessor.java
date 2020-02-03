package com.example.compiler;

import com.example.annotation.ARouter;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;





//通过AutoService注解，自动生成注解处理器，用来做注册，在对应的文件夹下生成相应文件
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.example.annotation.ARouter"})  //完整的类名
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions("content")
public class ARouterProcessor extends AbstractProcessor {

    //操作Element工具类
    private Elements elementUtils;

    //type(类信息)工具类
    private Types typeUtils;

    //用来输出警告，错误等日志
    private Messager messager;

    //文件生成器
    private Filer filer;

    //使用注解的方式，实现下面方法
//    //接收外面传入的参数
//    @Override
//    public Set<String> getSupportedOptions() {
//        return super.getSupportedOptions();
//    }
//
//    //获取支持的注解类型
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();
//    }
//
//    //通过某JDK的版本进行编译，必填
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return super.getSupportedSourceVersion();
//    }
//
//
//    //用于进行一些初始化的工作，有用的工具类或文件生成器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        //在初始化工作时，接收 @SupportedOptions("content") 注解传入的参数
        String content = processingEnvironment.getOptions().get("content");
        //不能像Android 中的Log.e的写法，否则会报错
        messager.printMessage(Diagnostic.Kind.NOTE,content);   //会在下方红色显示

    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     *
     * @param set                  使用了支持处理注解的节点集合（类上面写了注解）
     * @param roundEnvironment     当前或是之前的运行环境，可以通过该对象查找找到的注解
     * @return                     true表示后续处理器不再会处理，false则会继续进行处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty())
            return false;

        //获取项目中所有使用了ARouter注解的节点
        Set<?extends Element>elements=roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        //遍历所有的类节点
        for (Element element:elements){
            //类节点之上，就是包节点
            String packageName=elementUtils.getPackageOf(element).getQualifiedName().toString();
            //获取简单类名
            String simpleName=element.getSimpleName().toString();
            //输出所有的被注解的类
            messager.printMessage(Diagnostic.Kind.NOTE,"备注接的类有："+simpleName);

            //最终需要生成的类文件，这里的写法参考EventBus, 如：MainActivity$$ARouter
            String finalClassName=simpleName+"$$ARouter";

            /**
             * 使用Writer的方式输出所需要的文件

            //createSourceFile创建源文件,创建的文件位置在packageName包下
            //在写输出文件时，要根据需要的输出文件进行编写
            try {
                JavaFileObject sourceFile=filer.createSourceFile(packageName+"."+finalClassName);

                Writer writer=sourceFile.openWriter();

                //设置包名，package后需要有空格
                writer.write("package "+packageName+";\n");
                //设置类名，class后需要有空格
                writer.write("public class "+finalClassName+"{\n");

                writer.write("public static Class<?> findTargetClass(String path){\n");

                //获取类之上@ARouter注解的path的值
                ARouter aRouter=element.getAnnotation(ARouter.class);

                writer.write("if(path.equalsIgnoreCase(\""+aRouter.path()+"\")){\n");

                //return后需要有空格
                writer.write("return "+simpleName+".class;\n}\n");

                writer.write("return null;\n");

                writer.write("}\n}");

                //关闭
                writer.close();




            } catch (IOException e) {
                e.printStackTrace();
            }

            */

            /**
             * 使用Java Poet的方式输出所需要的文件，Java Poet的使用方式在Github上有
             */

            ARouter aRouter=element.getAnnotation(ARouter.class);

            // public static Class<?> findTargetClass(String path){
            MethodSpec methodSpec=MethodSpec.methodBuilder("findTargetClass")
                    .addModifiers(Modifier.PUBLIC,Modifier.STATIC)
                    .returns(Class.class)
                    .addParameter(String.class,"path")
                    //return path.equals("app/MainActivity") ? MainActivity.class:null;
                    .addStatement("return path.equals($S) ? $T.class : null",
                            aRouter.path(),
                            ClassName.get((TypeElement)element))
                    .build();

            TypeSpec typeSpec=TypeSpec.classBuilder(finalClassName)
                    .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                    .addMethod(methodSpec)
                    .build();

            JavaFile javaFile= JavaFile.builder(packageName,typeSpec)
                    .build();

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        return true;
    }
}
