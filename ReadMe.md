# scheduled.preventer 手册

## 1.What for/解决什么问题

In to test environment , people may want to ***stop automatically to run scheduled (Spring version )*** . This product provided ability to achieve this purpose .

在批处理的测试环境中，有时希望某些批处理不要自动运行。通过使用本插件和相关配置达到 ***禁止自动启动批处理*** 的目的。

------

## 2. How to/如何使用

### 2.1 configuration/配置自动运行

+ Target app **must** with **springboot**
+ 目标程序必须使用springboot框架

------

+ Copy the jar file to the class pass of target app. copy 
+ 本jar包到 目标应用的classpath中

------

+ Target app should scan component with path "***com.dane.brown***"
+ 目标应用必须scan到 ***com.dane.brown*** 这个包

------

+ Write the configure below in target app for preventing some schedule you want, otherwise, ***this product will prevent all scheduled method***.
+ 在目标应用的配置文件中增加如下配置:

```properties
com.dane.brown.scheduled.manage.prevent-regex=com.dane.brown.*.test1,com.dane.brown.*.test2
```

or 或者

```yaml
com:
  dane:
    brown:
    scheduled:
        manage:
        prevent-regex: com.dane.brown.*.test1,com.dane.brown.*.test2
```

The **prevent-regex** is regex which means you want to preventing, split by comma.

上述的**prevent-regex** 表示正则表达式列表，用逗号, 分割。

### 2.2 generate http api/通用http接口

#### 2.2.1 generalschedulingquery

Used to query the status of certain batches, including execution time, etc.

用于查询某些批处理的状态，包括执行时间等

*syntax*:
http://{host}:{port}/{webapppath}/***generalschedulingquery***/{you scheduled class full name}/{you scheduled method}

example：
http://localhost:8080/generalschedulingquery/com.dane.brown.AppTest/test3

or

```shell
curl "http://localhost:8080/generalschedulingquery/com.dane.brown.AppTest/test3"
```


#### 2.2.1 generalschedulinginvoke:

Used to manually execute some batches. If the batch to be executed is still running, the plugin will terminate the http request and return.

用于手动执行某些批处理，如果要执行的批处理还在运行过程中，本插件会终止http请求并返回。

*syntax*:
http://{host}:{port}/{webapppath}/***generalschedulinginvoke***/{you scheduled class full name}/{you scheduled method}

example:
http://localhost:8080/generalschedulinginvoke/com.dane.brown.AppTest/test3

or

```shell
curl "http://localhost:8080/generalschedulinginvoke/com.dane.brown.AppTest/test3"
```

------

## 3.Essential/本质

In fact, this product is a plugin, which implement of  **SchedulingConfigurer** interface. **SchedulingConfigurer** interface is called by **ScheduledAnnotationBeanPostProcessor** , which is implement interface of **BeanPostProcessor** essentially.  

The **ScheduledAnnotationBeanPostProcessor** will first get all the local methods that marked by Schedule annotations, and put them into a local List, then pass these lists as arguments to the **SchedulingConfigurer** implementation.  

In my implementation, I get the real class and method names behind these tasks. After splicing the class and method names as conditions, check according to the prevent-regex regular expression mentioned above. All the tasks that need to be blocked will be removed and passed to the **ScheduledTaskRegistrar**, which is equivalent to re-injecting **Scheduled**.

------

实际上，本产品是个**插件**。实现了 **SchedulingConfigurer** 接口。这个接口是被  **ScheduledAnnotationBeanPostProcessor** 调用的，本质上  **ScheduledAnnotationBeanPostProcessor** 
实现了  **BeanPostProcessor** 接口。 

**ScheduledAnnotationBeanPostProcessor** 首先会获取本地所有实现了 **Schedule** 注解的方法，放到本地的List<Task>中，之后会将这些list
当做参数传递给 **SchedulingConfigurer** 的实现方法。

在我的实现中，我获取这些这些task的背后真实的类和方法名称。将类和方法名称拼接后作为条件，根据上面提到的 **prevent-regex**
正则表达式进行检查。所有需要阻止运行的task，都会被剔除后，再传递给 **ScheduledTaskRegistrar**，相当于重新注入了 **Scheduled** .

## 4. Why not AOP/为什么不用AOP

May someone asked me "why dosen't you use AOP"? Honestly, I had tried it, a main reason make me to discard AOP, that is ***AOP will prevent all candidate method no matter what invoker is***. Which means, AOP is a kind of "AOE" skill, it does prevent the "Scheduled" way, and it prevented all the other Spring ways to invoke it at the same time. That is too hard to use.

也许有人问我“你为什么不用AOP呢？”真的，我尝试过。使用AOP最大的问题是 ***它会阻止所有的调用者***。换句话说，AOP是一种AOE技能，它的确阻止了"Schedule"自动启动的方式，但是同时，它也阻止了其他使用spring方式的调用。这个对于实际使用来说，太"硬"了。
