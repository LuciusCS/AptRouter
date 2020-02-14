package com.example.compiler;

import com.example.annotation.ARouter;
import com.example.annotation.model.RouterBean;
import com.example.compiler.utils.Constants;
import com.example.compiler.utils.EmptyUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;


import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.io.IOException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

//AutoService是固定写法，加个注解即可
//通过autos-service中的@AutoService可以自动申城AutoService注解处理器，用来注册
//用来生成META-INF/services/javax.annotation.processing.Processor文件

//通过AutoService注解，自动生成注解处理器，用来做注册，在对应的文件夹下生成相应文件
@AutoService(Processor.class)
//允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})  //完整的类名
//指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//接受build.gradle穿过来的参数
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})
//@SupportedOptions("content")
public class ARouterProcessor extends AbstractProcessor {

    //操作Element工具类(类、函数、属性都是Elements)
    private Elements elementUtils;

    //type(类信息)工具类,包含用于操作的TypeMirror的工具方法
    private Types typeUtils;

    //用来输出警告，错误等日志
    private Messager messager;

    //文件生成器  类/资源/Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    //子模块名，如：app/order/personal，需要拼接类名时用到（必传）ARouter$$Group$$order
    private String moduleName;

    //包名，用于存放APT生成的类文件
    private String packageNameForAPT;


    //临时map存储，用来存放路由组Group对应的详细Path对象，生成路由路径文件时遍历
    //key: 组名"app" value:"app"组的路由路径"ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    //临时map存储，用来存放路由组Group信息，生成路由路径文件时遍历
    //key: 组名"app" value:雷鸣"ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    //    //用于进行一些初始化的工作，有用的工具类或文件生成器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        //通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {

            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);

            //有坑：Diagnostic.Kind.ERROR 异常会自动结束，不同于Log.e
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>>" + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageName >>>" + packageNameForAPT);

        }


        //必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或packageName为空，请在build.gradle中进行参数配置");
        }


    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    /**
     * 相当于main函数，开始处理注解
     * 注解处理器的核心方法，处理具体的注解，生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合（类上面写了注解）
     * @param roundEnvironment 当前或是之前的运行环境，可以通过该对象查找找到的注解
     * @return true表示后续处理器不再会处理，false则会继续进行处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        //一旦有类上面使用了@ARouter注解
        if (!EmptyUtils.isEmpty(set)) {

            //获取项目中所有使用了ARouter注解的节点集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);

            if (!EmptyUtils.isEmpty(elements)) {
                //解析元素
                try {
                    parseElements(elements);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return true;
        }


        return false;
    }


    //解析所有被@ARouter注解的元素集合
    private void parseElements(Set<? extends Element> elements) throws IOException {

        //通过Element工具类，获取Activity类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);

        //显示类信息
        TypeMirror acitivityMirror = activityType.asType();

        for (Element element : elements) {
            //获取每个元素的类信息
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.NOTE, "遍历的元素信息为：" + elementMirror.toString());

            //获取每个类上的@ARouter注解，对应的path值
            ARouter aRouter = element.getAnnotation(ARouter.class);

            //路由详细信息，封装到实体类
            RouterBean routerBean = new RouterBean.Builder().
                    setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();

            //告诫判断 @ARouter注解只能作用域类智商，并且是规定的Activity
            if (typeUtils.isSubtype(elementMirror, acitivityMirror)) {

                routerBean.setType(RouterBean.Type.ACTIVITY);
            } else {
                throw new RuntimeException("@ARouter注解目前仅限用于Activity");
            }

            //赋值临时Map存储以上信息，用来遍历时生成代码
            valueOfPathMap(routerBean);
        }

        //ArouterLoadGroup和ARouterLoadPath，用来生成类文件时实现接口
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTER_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTER_PATH);

        //1、 生成路由的详细Path文件，如：ARouter$$Path$$app
        createPathFile(pathLoadType);

        //2、 生成路由组Group类文件 （没有Path类文件，取不到），如 ARouter$$Group$$app
        createGroupFile(groupLoadType, pathLoadType);

    }

    /**
     * 生成路由组Group对应详细Path，如：ARouter$$Path$$app
     *
     * @param pathLoadType ARouterLoadPath接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws IOException {

        if (EmptyUtils.isEmpty(tempPathMap)) return;

        //方法的返回值 Map<String,RouterBean>

        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class));

        //遍历分组，每一个富足创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {

            //方法体构造public Map<String,RouterBean> loadPath(){

            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)  //方法名
                    .addAnnotation(Override.class)   //重写注解
                    .addModifiers(Modifier.PUBLIC)   //public修饰符
                    .returns(methodReturns);         //方法的返回值

            //不循环部分Map<String,RouterBean>pathMap=new HashMap<>()
            methodBuilder.addStatement("$T<$T,$T>$N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class);

            // app/MainActivity, app/...
            List<RouterBean> pathList = entry.getValue();
            //方法内容的循环部分
            for (RouterBean bean : pathList) {

                /**
                 *  pathMap.put("/app/MainActivity",
                 *                  RouterBean.create(RouterBean.Type.ACTIVITY,
                 *                      MainActivity.class,
                 *                      "/app/MainActivity",
                 *                      "app"));
                 *
                 */

                methodBuilder.addStatement("$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETER_NAME,
                        bean.getPath(),      // "/app/MainActivity"
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),     //枚举ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()), //  MainActivity.class
                        bean.getPath(),      // "/app/MainActivity"
                        bean.getGroup());   //"app"


            }

            // 遍历过后，最后return pathMap
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            //生成类文件
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();

            JavaFile.builder(packageNameForAPT,            //创建包路径
                    TypeSpec.classBuilder(finalClassName)  //类名
                            .addSuperinterface(ClassName.get(pathLoadType))   //实现接口
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())    //方法的构建
                            .build())
                    .build()  //类构建完成
                    .writeTo(filer);

            //别忘了，非常重要赋值
            tempGroupMap.put(entry.getKey(), finalClassName);

        }

    }

    /**
     * 生成路由组Group文件，如：ARouter$$Group$$app
     *
     * @param groupLoadType ARouterLoadGroup 接口信息
     * @param pathLoadType  ARouterLoadPath接口信息
     */

    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws IOException {

        //判断是否有需要生成的类文件
        if (EmptyUtils.isEmpty(tempGroupMap) || EmptyUtils.isEmpty(tempPathMap)) return;


        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),          //Map
                ClassName.get(String.class),       //Map<String,
                //第二个参数：Class<? extends ARouterLoadPath>
                //某个Class是否属于ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        //方法配置： public Map<String,Class<? extends ARouterLoadPath>>loadGroup{
        MethodSpec.Builder methodBuilder =MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)//方法名
                .addAnnotation(Override.class) //重写注解
                .addModifiers(Modifier.PUBLIC) //PUBLIC修饰符
                .returns(methodReturns);  //方法返回值

        //遍历之前：Map<String,Class<? extends ARouterLoadPath>> groupMap=new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T>$N = new $T<> () " ,
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);

        //方法内容配置
        for (Map.Entry<String,String>entry:tempGroupMap.entrySet()){
            //类似String.format("hello %s ccc %d","ass",16) 通配符
            //groupMap.put("main",ARouter$$Path$$app.class);
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Constants.GROUP_PARAMETER_NAME,    //groupMap.put
                    entry.getKey(),
            //类文件在指定包名下
                    ClassName.get(packageNameForAPT,entry.getValue()));

        }

        //遍历之后： return groupMap
        methodBuilder.addStatement("return $N",Constants.GROUP_PARAMETER_NAME);

        //最终生成的类文件名
        String finalClassName= Constants.GROUP_FILE_NAME+moduleName;

        messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由组Group类文件：" +
                packageNameForAPT+"."+finalClassName);


        //生成类文件：ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT,         //包名
                TypeSpec.classBuilder(finalClassName)   //类名
                    .addSuperinterface(ClassName.get(groupLoadType))   //实现ARouterLoadGroup接口
                    .addModifiers(Modifier.PUBLIC)      //public修饰符
                    .addMethod(methodBuilder.build())   //方法构建（方法参数+方法体）
                    .build())           //类构建完成
                .build()               //JavaFile构建完成
                .writeTo(filer);      //文件生成器开始生成类文件
    }

    /**
     * 赋值临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时便利
     *
     * @param routerBean 路由详细信息，最终实体封装类
     */

    private void valueOfPathMap(RouterBean routerBean) {

        if (checkRouterPath(routerBean)) {

            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + routerBean.toString());

            //开始赋值
            List<RouterBean> routerBeans = tempPathMap.get(routerBean.getGroup());
            //如果从map中找不到key
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(routerBean);
                tempPathMap.put(routerBean.getGroup(), routerBeans);
            } else {
                //找到了key,直接加入临时集合
                routerBeans.add(routerBean);

            }

        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如： /app/MainActivity");
        }

    }

    /**
     * 校验@ARouter注解的只，如果Group未填写就从必填项path中截取数据
     *
     * @param routerBean 路由详细信息，最终实体封装类
     * @return
     */

    private boolean checkRouterPath(RouterBean routerBean) {
        String group = routerBean.getGroup();
        String path = routerBean.getPath();

        //@ARouter注解的path的值，必须要以/开头  （模仿阿里ARouter路由架构）
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }

        //如果开法阵代码为： path="/MainActivity"
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }


        //从第一个 / 到第二个 / 截取出组名
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        //比如开发者代码为： path="/MainActivity/MainActivity/MainActivity"
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }

        // @ARouter 注解中有group赋值
        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中group值必须和当前子模块名相同");
            return false;
        } else {
            routerBean.setGroup(finalGroup);
        }


        return true;

    }


}
