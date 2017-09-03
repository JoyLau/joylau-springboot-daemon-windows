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
MP4 ： http://image.joylau.cn/blog/joylau-springboot-daemon-service-video.mp4


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
进入文件夹，解压这个压缩包，你会看见如下内容的文件
![joylau-springboot-daemon-windows-package-file](http://image.joylau.cn/blog/joylau-springboot-daemon-windows-package-file.jpg)
注意：
 1. 5个 bat 文件，请右键以管理员的身份运行
 2. 各文件的文件名无特殊情况，不需要修改
 3. 一旦安装成了 Windows 服务，目录下的文件就不要移动了
 4. 命令运行时，可能会提示安装.NET,安装完成就可运行命令了，不过现在大部分的 Windows 服务器或者个人电脑都会默认安装了.NET,没有的话启用一下就好了，如下图：
 ![joylau-springboot-daemon-windows-.NET](http://image.joylau.cn/blog/joylau-springboot-daemon-windows-.net.jpg)
 5. 运行各个命令是注意提示信息，例如卸载完服务都的状态为NonExistent，刚安装完服务后的状态为Stopped，服务成功启动的状态为Started...等等
 ![joylau-springboot-daemon-windows-service-status](http://image.joylau.cn/blog/joylau-springboot-daemon-service-status.jpg)


## 扩展参数
想要在服务启动时添加自定义参数,如 SpringBoot 的配置参数或者 JMV 参数？
像如下配置即可：
``` xml
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
        <configuration>
            <arguments>
                <argument>--server.port=9090</argument>
            </arguments>
        </configuration>
    </plugin>
```

上面配置了一个 Spring Boot 应用的启动端口9090

## 使用注意
- 打包使用过程中需要联网
- 文档中有些图片可能看不到，再次刷新下页面就可以
- 服务的id为artifactId，服务的名称为artifactId+version，服务的描述为description