#
zipkin是一个开放源代码分布式的跟踪系统，由Twitter公司开源，它致力于收集服务的定时数据，以解决微服务架构中的延迟问题，包括数据的收集、存储、查找和展现。它的理论模型来自于Google Dapper 论文。<br>
运行service1、service2、service3、service4之前先启动zipkin-server-2.11.8。<br>
1）zipkin采用memory存储链路日志 <br>
  直接启动zipkin：java -jar zipkin-server-2.11.8-exec.jar <br>

2）zipkin采用mysql存储链路日志 <br>
先创建相关数据库表：<br>
CREATE TABLE IF NOT EXISTS zipkin_spans ( <br>
  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit', <br>
  `trace_id` BIGINT NOT NULL, <br>
  `id` BIGINT NOT NULL, <br> 
  `name` VARCHAR(255) NOT NULL, <br>
  `remote_service_name` VARCHAR(255), <br>
  `parent_id` BIGINT, <br>
  `debug` BIT(1), <br>
  `start_ts` BIGINT COMMENT 'Span.timestamp(): epoch micros used for endTs query and to implement TTL', <br>
  `duration` BIGINT COMMENT 'Span.duration(): micros used for minDuration and maxDuration query', <br>
  PRIMARY KEY (`trace_id_high`, `trace_id`, `id`) <br>
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci; <br>
<br>
ALTER TABLE zipkin_spans ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTracesByIds'; <br>
ALTER TABLE zipkin_spans ADD INDEX(`name`) COMMENT 'for getTraces and getSpanNames'; <br>
ALTER TABLE zipkin_spans ADD INDEX(`remote_service_name`) COMMENT 'for getTraces and getRemoteServiceNames'; <br>
ALTER TABLE zipkin_spans ADD INDEX(`start_ts`) COMMENT 'for getTraces ordering and range'; <br>
<br>
CREATE TABLE IF NOT EXISTS zipkin_annotations ( <br>
  `trace_id_high` BIGINT NOT NULL DEFAULT 0 COMMENT 'If non zero, this means the trace uses 128 bit traceIds instead of 64 bit', <br>
  `trace_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.trace_id', <br>
  `span_id` BIGINT NOT NULL COMMENT 'coincides with zipkin_spans.id', <br>
  `a_key` VARCHAR(255) NOT NULL COMMENT 'BinaryAnnotation.key or Annotation.value if type == -1', <br>
  `a_value` BLOB COMMENT 'BinaryAnnotation.value(), which must be smaller than 64KB', <br>
  `a_type` INT NOT NULL COMMENT 'BinaryAnnotation.type() or -1 if Annotation', <br>
  `a_timestamp` BIGINT COMMENT 'Used to implement TTL; Annotation.timestamp or zipkin_spans.timestamp', <br>
  `endpoint_ipv4` INT COMMENT 'Null when Binary/Annotation.endpoint is null', <br>
  `endpoint_ipv6` BINARY(16) COMMENT 'Null when Binary/Annotation.endpoint is null, or no IPv6 address', <br>
  `endpoint_port` SMALLINT COMMENT 'Null when Binary/Annotation.endpoint is null', <br>
  `endpoint_service_name` VARCHAR(255) COMMENT 'Null when Binary/Annotation.endpoint is null' <br>
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci;
<br>
ALTER TABLE zipkin_annotations ADD UNIQUE KEY(`trace_id_high`, `trace_id`, `span_id`, `a_key`, `a_timestamp`) COMMENT 'Ignore insert on duplicate'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`, `span_id`) COMMENT 'for joining with zipkin_spans'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`trace_id_high`, `trace_id`) COMMENT 'for getTraces/ByIds'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`endpoint_service_name`) COMMENT 'for getTraces and getServiceNames'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`a_type`) COMMENT 'for getTraces and autocomplete values'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`a_key`) COMMENT 'for getTraces and autocomplete values'; <br>
ALTER TABLE zipkin_annotations ADD INDEX(`trace_id`, `span_id`, `a_key`) COMMENT 'for dependencies job'; <br>
<br> 
CREATE TABLE IF NOT EXISTS zipkin_dependencies ( <br>
  `day` DATE NOT NULL, <br>
  `parent` VARCHAR(255) NOT NULL, <br>
  `child` VARCHAR(255) NOT NULL, <br>
  `call_count` BIGINT, <br>
  `error_count` BIGINT, <br>
  PRIMARY KEY (`day`, `parent`, `child`) <br>
) ENGINE=InnoDB ROW_FORMAT=COMPRESSED CHARACTER SET=utf8 COLLATE utf8_general_ci; <br>
<br>
然后启动zipkin：java -jar zipkin-server-2.11.8-exec.jar --STORAGE_TYPE=mysql --MYSQL_HOST=127.0.0.1 --MYSQL_TCP_PORT=3306 --MYSQL_DB=zipkin --MYSQL_USER=root --MYSQL_PASS=password  <br>
<br>
3）zipkin采用rabbitmq存储链路日志 <br>
启动zipkin：java -jar zipkin-server-2.11.8-exec.jar --zipkin.collector.rabbitmq.addresses=localhost:5672 --zipkin.collector.rabbitmq.username=guest --zipkin.collector.rabbitmq.password=guest --zipkin.storage.type=mysql --zipkin.storage.mysql.host=127.0.0.1 --zipkin.storage.mysql.port=3306  --zipkin.storage.mysql.db=zipkin --zipkin.storage.mysql.username=root --zipkin.storage.mysql.password=password <br>

