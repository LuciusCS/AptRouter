package com.example.compiler;

import com.example.annotation.Parameter;
import com.example.annotation.model.RouterBean;
import com.example.compiler.factory.ParameterFactory;
import com.example.compiler.utils.Constants;
import com.example.compiler.utils.EmptyUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.example.compiler.utils.Constants.PARAMETER_FILE_NAME;
import static com.example.compiler.utils.Constants.PARAMETER_LOAD;
import static com.example.compiler.utils.Constants.PARAMETER_NAME;


//通过AutoService注解，自动生成注解处理器，用来做注册，在对应的文件夹下生成相应文件
@AutoService(Processor.class)
//允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.PARAMETER_ANNOTATION_TYPES})  //完整的类名
//指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)

public class ParameterProcessor extends AbstractProcessor {

    //操作Element工具类(类、函数、属性都是Elements)
    private Elements elementUtils;

    //type(类信息)工具类,包含用于操作的TypeMirror的工具方法
    private Types typeUtils;

    //用来输出警告，错误等日志
    private Messager messager;

    //文件生成器  类/资源/Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    //临时map存储，用来存放@Parameter注解的属性集合，生成类文件时遍历
    //key: 类结点, value:被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();


    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //一旦有类上面使用了@Parameter注解
        if (!EmptyUtils.isEmpty(set)) {

            //获取项目中所有使用了Parameter注解的节点集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);

            if (!EmptyUtils.isEmpty(elements)) {

                //用临时的map存储，用来生成遍历代码
                valueOfParameterMap(elements);
                //生成类文件
                try {
                    createParameterFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                return true;

            }


        }

        return false;

    }

    private void createParameterFile() throws IOException {


        if (EmptyUtils.isEmpty(tempParameterMap)) return;

        //通过Element工具类，获取Parameter类型
        TypeElement parameterType= elementUtils.getTypeElement(PARAMETER_LOAD);

        //参数配置（Object target）
        ParameterSpec parameterSpec=ParameterSpec.builder(TypeName.OBJECT,PARAMETER_NAME).build();


        //遍历分组，每一个富足创建一个文件
        for (Map.Entry<TypeElement, List<Element>> entry :tempParameterMap.entrySet()) {

            //Map集合中的key是类名，如：MainActivity
            TypeElement typeElement=entry.getKey();

            //获取类名
            ClassName className=ClassName.get(typeElement);

            //方法体内容构建
            ParameterFactory factory=new ParameterFactory.Builder(parameterSpec)
                    .setMessager(messager)
                    .setElementUtils(elementUtils)
                    .setTypeUtils(typeUtils)
                    .setClassName(className)
                    .build();
            //添加方法体内容的第一行：MainActivity t=(MainActivity)target;
            factory.addFirstStatement();

            //遍历类里面所有的属性: t.name=t.getIntent().getStringExtra("name");
            for (Element fieldElement:entry.getValue()){
                factory.buildStatement(fieldElement);
            }

            //最终生成的类文件名(类名$$Parameter)
            String finalClassName=typeElement.getSimpleName()+PARAMETER_FILE_NAME;
            messager.printMessage(Diagnostic.Kind.NOTE,"APT生成获取参数类文件"+className.packageName()+"."+finalClassName);

            //MainActivity$$Parameter
            JavaFile.builder(className.packageName(),        //包名
                    TypeSpec.classBuilder(finalClassName)    //类名
                                .addSuperinterface(ClassName.get(parameterType)) //实现ParameterLoad接口
                                .addModifiers(Modifier.PUBLIC)   //public修饰符
                                .addMethod(factory.build()).build())  //方法的构建（方法参数+方法体）
                    .build()     //JavaFile构建完成
                    .writeTo(filer);  //文件生成器开始生成类文件



        }




    }

    private void valueOfParameterMap(Set<? extends Element> elements) {
        for (Element element:elements){
            //注解的属性，父节点是类节点
            TypeElement typeElement=(TypeElement)element.getEnclosingElement();
            //如果map集合中有这个类节点
            if (tempParameterMap.containsKey(typeElement)){
                tempParameterMap.get(typeElement).add(element);
            }else {
                List<Element>fields=new ArrayList<>();
                fields.add(element);
                tempParameterMap.put(typeElement,fields);
            }
        }

    }
}
