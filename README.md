#
zipkin是一个开放源代码分布式的跟踪系统，由Twitter公司开源，它致力于收集服务的定时数据，以解决微服务架构中的延迟问题，包括数据的收集、存储、查找和展现。它的理论模型来自于Google Dapper 论文。
#
运行service1、service2、service3、service4之前先启动zipkin-server-2.11.8，启动命令：java -jar zipkin-server-2.11.8-exec.jar
