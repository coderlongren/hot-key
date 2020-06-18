# hotkey
![输入图片说明](https://images.gitee.com/uploads/images/2020/0616/105737_e5b876cd_303698.png "redis热key探测及缓存到JVM (1).png")

京东APP后台热数据探测框架，历经多次高压压测和2020年京东618大促考验。在上线运行的这段时间内，每天探测的key数量数十亿计，精准捕获了大量爬虫、刷子用户，另准确探测大量热门商品并毫秒级推送到各个服务端内存，大幅降低了热数据对数据层的查询压力，提升了应用性能。

该框架历经多次压测，8核单机worker端每秒可接收处理16万个key探测任务，16核单机至少每秒平稳处理20万以上，实际压测达到30万以上，CPU平稳支撑，框架无异常。在真实业务场景中，可用1：1000的比例，即1台worker支撑1000台服务端Tomcat的key探测任务，即可带来极大的数据存储资源节省（如对redis集群的扩充）。测试详情可去我[CSDN博客](https://blog.csdn.net/tianyaleixiaowu)查看。

#### 介绍
对任意突发性的无法预先感知的热点请求，包括并不限于热点数据（如突发大量请求同一个商品）、热用户（如爬虫、刷子）、热接口（突发海量请求同一个接口）等，进行毫秒级精准探测到。
然后对这些热数据、热用户等，推送到该应用部署的所有机器JVM内存中，以大幅减轻对后端数据存储层的冲击，并可以由客户端决定如何使用这些热key（譬如对热商品做本地缓存、对热用户进行拒绝访问、对热接口进行熔断或返回默认值）。
这些热key在整个应用集群内保持一致性。

核心功能：热数据探测并推送至集群各个服务器

适用场景：

1 mysql热数据本地缓存

2 redis热数据本地缓存

3 黑名单用户本地缓存

4 爬虫用户限流

5 接口、用户维度限流

6 单机接口、用户维度限流限流

7 集群用户维度限流

8 集群接口维度限流


#### 尚未完成
文档有待完善。


该开源项目战略意义重大，经历百万级并发，参与京东开源中间件项目建设，一直在等你。

#### worker端强悍的性能表现
每10秒打印一行，totalDealCount代表处理过的key总量，可以看到每10秒处理量在270万-310万之间，对应每秒30万左右QPS。

仅需要很少的机器，即可完成海量key的实时探测计算推送任务。比扩容redis集群规模成本低太多。
![输入图片说明](https://images.gitee.com/uploads/images/2020/0611/152336_78597937_303698.png "屏幕截图.png")
![输入图片说明](https://images.gitee.com/uploads/images/2020/0611/152249_4ac01178_303698.png "屏幕截图.png")


### 界面效果
[图片上传中…(image-CfkSG1Ut0xJrhmLo5hnK)]

### 微信群
![输入图片说明](https://images.gitee.com/uploads/images/2020/0615/202512_3a190552_303698.jpeg "WechatIMG3.jpeg")



#### 安装教程

1.  xxxx
2.  xxxx
3.  xxxx

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
