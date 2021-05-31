1. 编写hello.g4
2. 生成代码，IDEA中右键hello.g4, 选择antlr4的插件(https://plugins.jetbrains.com/plugin/7358-antlr-v4)
   菜单 'Generate ANTLR Recognizer',在项目的目录下会生成 gen目录，hello包下面有helloXXX类
   
3. pom中添加 antlr4-runtime 依赖
4. gen 文件夹下面类 copy到你工程路径 // 当然你也可以生成到classpath下
5. 