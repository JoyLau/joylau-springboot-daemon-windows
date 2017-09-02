# 一款将 SpringBoot 项目做成Windows Service 的 Maven 插件

>> 包括但不限于 SpringBoot ,任何打成 java jar 包运行的 Maven 项目都可以使用

## 编写初衷
- 公司有个项目
- Java 部分的全部使用的是SpringBoot
- 该项目的部署环境是 Windows
- 公司想把 各个 SpringBoot 的模块托管一下
- 托管的使用方式要简单，易用，测试在打包部署的时候要很容易上手
- 期间尝试过 Spring Boot Admin 和 Jenkins,都说不好用...
- 于是就想着 将Spring Boot 的服务制作成 Windows 服务，这样基本上会操作电脑的人都会使用了，够简单易用的了吧
- 花了一上午时间将其中一个 Spring Boot 模块制作成了 Windows Service
- 发现再做其他的模块的时候，很多工作都是重复的，心想着能够将这个功能提取出来就好了
- 于是就写了这个 Maven 插件


## 使用演示地址：



## 怎么使用？
- 使用方法很简单，和普通的 Maven 插件一样使用就可以了，如下
``` xml
    <plugins>
        <plugin>
            <groupId>cn.joylau.code</groupId>
            <artifactId>joylau-springboot-daemon-windows</artifactId>
            <version>1.0.RELEASE</version>
            <executions>
                <execution>
                    <id>make-win-service</id>
                    <phase>package</phase>
                    <goals>
                        <goal>make-win-service</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
```

注意：
 1. 这里的 phase 写的是 package,意思是该插件在 mvn package 的时候调用,你也可以根据不同的需求来更改，比如 install, test等等
 2. goal 写 make-win-service 就可以了，不需要改动
 
- 在你的项目中按照以上的方式引入插件后，现在可以 打包了
``` 
    mvn package
```

打包过程中，看到如下日志信息，便制作成功了：
![joylau-springboot-daemon-windows-package-info](http://image.joylau.cn/blog/joylau-springboot-daemon-windows-package-info.jpg)

此时，在你项目的target目录下会生成一个 jar 包名字 一样的压缩包