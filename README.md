[TOC]

## 组件介绍

### 1.1 组件介绍
WEBASE-BEE 是一个基于[FISCO-BCOS](https://github.com/FISCO-BCOS/FISCO-BCOS)平台的数据导出工具。

数据导出组件WEBASE-BEE的目的在于降低获取区块链数据的开发门槛，提升研发效率。研发人员几乎不需要编写任何代码，只需要进行简单配置，就可以把数据导出到Mysql数据库。

WEBASE-BEE可以导出区块链上的基础数据，如当前块高、交易总量等。如果正确配置了FISCO-BCOS上运行的所有合约，WEBASE-BEE可以导出区块链上这些合约的业务数据，包括event、构造函数、合约地址、执行函数的信息等。

数据导出组件支持多数据源、分库分表、读写分离、分布式部署。

WEBASE-BEE提供了基于Restful的API，支持通过http的方式调用这些接口。

WEBASE-BEE还集成了Swagger组件，提供了可视化的文档和测试控制台。

你可以通过WEBASE-MONKEY来自动生成本工程，只需要在一个配置文件中进行少量简单的配置，同时按照要求提供相关的智能合约信息；我们推荐这种方式。

### 1.2 使用场景和解决方案
区块链的数据存储在区块链上，需要使用智能合约暴露的接口来进行调用。由于智能合约暴露的接口的限制，区块链上不适合进行复杂的数据查询、大数据分析和数据可视化等工作。因此，我们致力于提供一种智能化、自动化的数据导出和备份的解决方案。

#### 案例 数据可视化后台系统
- 背景

某互联网小贷公司基于FISCO-BCOS开发了区块链借条业务系统，客户之间的借贷合同信息和证明材料都会在脱敏后保存到区块链上。该公司的运营人员需要获得当前业务进展的实时信息和摘要信息。

- 解决方案

该公司使用webase-monkey迅速生成了webase-bee的代码，并根据实际需求进行了定制化开发，在一天之内投入到线上使用。

导出到db的数据接入到了该公司的统一监控平台，该公司PM可以在业务后台系统上获得该业务的实时进展，该公司运维人员可以在公司运维监控室的大屏幕实时监控业务系统的状态。

#### 案例 区块链业务数据对账系统
- 背景

某公司基于FISCO-BCOS开发了区块链的业务系统，需要将本地数据与链上的数据进行对账。

- 解决方案
该公司使用webase-monkey迅速生成了webase-bee的代码，并根据实际需求进行了定制化开发。通过在智能合约中设计的各类event，相关的业务数据都被导出到数据库中；从而实现轻松对账的需求。

#### 案例 区块链业务数据查询系统
- 背景

某互联网公司基于FISCO-BCOS开发了区块链的业务系统，但是发现智能合约对业务报表的支持不佳。但是，公司的一线业务部门要求实时查看各类复杂的业务报表。

- 解决方案

该公司使用webase-monkey迅速生成了webase-bee的代码，并根据实际需求进行了定制化开发，区块链上的数据可以实时导出到数据库中。利用webase-bee自带的Restful API，该公司的报表系统实现了和区块链数据的对接，可以获得准实时的各类业务报表。

### 1.3 特性介绍

#### 可自动生成代码
可使用webase-monkey生成的代码和配置文件，自动组装成数据导出工程实例

#### 支持灵活的数据库策略
集成sharding-jdbc组件，支持多数据源、分库分表、读写分离

#### 支持集群部署和分布式任务调度
集成elstic-job开源组件，支持灵活的分布式部署和任务调度

#### 可定制化的数据导出策略
提供灵活的可配置的区块、交易、事件、账户等数据导出功能，过滤不需要的数据

#### 提供丰富的Restful API查询接口
支持丰富的Restful API数据查询接口

#### 提供可视化的互动API控制台
集成swagger插件，提供可视化互动API控制台

## 2. 快速开始

### 2.1 前置依赖
在使用本组件前，请确认系统环境已安装相关依赖软件，清单如下：

| 依赖软件 | 说明 |备注|
| --- | --- | --- |
| Bash | 需支持Bash（理论上来说支持所有ksh、zsh等其他unix shell，但未测试）|
| Java | >= Oracle JDK[1.8] | |
| Git | 下载的安装包使用Git | |
| MySQL | >= mysql-community-server[5.7] | |
| zookeeper | >= zookeeper[3.4] | 只有在进行集群部署的时候需要安装|


### 2.2 部署步骤

#### 2.2.1 获取工程代码

请按照webase-monkey的操作手册进行操作。

如果你已经按照webase-monkey的操作手册进行操作，那么恭喜，你将获得一个完整webase-bee工程目录。

webase-bee的工程使用gradle进行构建，是一个SpringBoot工程。


#### 2.2.2 配置工程

在得到webase-bee工程后，主要的配置文件位于src/main/resources目录下。其中，application.properties包含了除部分数据库配置外的全部配置。 application-sharding-tables.properties包含了数据库部分的配置。

##### 单节点部署的配置
单节点任务调度的配置，分布式任务调度的配置默认位于 src/main/resources/application.properties

```
#### 当此参数为false时，进入单节点任务模式
system.multiLiving=false

#### 系统执行的最大线程数，默认为CPU的核数
system.maxScheduleThreadNo = 20
#### 开启多线程下载的区块阈值，如果当前已完成导出的区块高度小于当前区块总高度减去该阈值，则启动多线程下载
system.maxBlockHeightThreshold=50
#### 多线程下载的分片数量，当完成该分片所有的下载任务后，才会统一更新下载进度。
system.crawlBatchUnit=100
```

##### 集群部署的配置
多节点任务调度的配置，分布式任务调度的配置默认位于 src/main/resources/application.properties

```
#### 当此参数为true时，进入多节点任务模式
system.multiLiving=true

#### zookeeper配置信息，ip和端口
regcenter.serverList=ip:port
#### zookeeper的命名空间
regcenter.namespace=namespace

#### prepareTaskJob任务：主要用于读取当前区块链块高，将未抓取过的块高存储到数据库中。
#### cron表达式，用于控制作业触发时间
prepareTaskJob.cron=0/5 * * * * ?
### 分片总数量
prepareTaskJob.shardingTotalCount=1
#### 分片序列号和参数用等号分隔，多个键值对用逗号分隔,分片序列号从0开始，不可大于或等于作业分片总数
prepareTaskJob.shardingItemParameters=0=A

#### dataflowJob任务： 主要用于执行区块下载任务
dataflowJob.cron=0/5 * * * * ?
### 分片总数量
dataflowJob.shardingTotalCount=3
#### 分片序列号和参数用等号分隔，多个键值对用逗号分隔,分片序列号从0开始，不可大于或等于作业分片总数
dataflowJob.shardingItemParameters=0=A,1=B,2=C
```

数据库配置解析，数据库的配置默认位于 src/main/resources/application-sharding-tables.properties

##### 分库分表的配置
实践表明，当区块链上存在海量的数据时，导出到单个数据库或单个业务表会对运维造成巨大的压力，造成数据库性能的衰减。
一般来讲，单一数据库实例的数据的阈值在1TB之内，单一数据库表的数据的阈值在10G以内，是比较合理的范围。

如果数据量超过此阈值，建议对数据进行分片。将同一张表内的数据拆分到多个或同个数据库的多张表。

```

#### 定义多个数据源
sharding.jdbc.datasource.names=ds0,ds1

#### 数据源ds0的默认配置
sharding.jdbc.datasource.ds0.type=com.zaxxer.hikari.HikariDataSource
sharding.jdbc.datasource.ds0.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds0.url=jdbc:mysql://localhost:3306/ds0
sharding.jdbc.datasource.ds0.username=root
sharding.jdbc.datasource.ds0.password=

#### 数据源ds1的默认配置
sharding.jdbc.datasource.ds1.type=com.zaxxer.hikari.HikariDataSource
sharding.jdbc.datasource.ds1.driver-class-name=com.mysql.cj.jdbc.Driver
sharding.jdbc.datasource.ds1.url=jdbc:mysql://localhost:3306/ds1
sharding.jdbc.datasource.ds1.username=root
sharding.jdbc.datasource.ds1.password=

#### 数据库默认分库分表的列字段
sharding.jdbc.config.sharding.default-database-strategy.inline.sharding-column=user_id
#### 数据库默认分库分表的算法
sharding.jdbc.config.sharding.default-database-strategy.inline.algorithm-expression=ds$->{user_id % 2}

#### 数据库表block_tx_detail_info的配置，以下配置即为数据自动分为5个分片，以block_height%5来进行路由，pk-id为自增值
sharding.jdbc.config.sharding.tables.block_tx_detail_info.actual-data-nodes=ds.block_tx_detail_info_$->{0..4}
sharding.jdbc.config.sharding.tables.block_tx_detail_info.table-strategy.inline.sharding-column=block_height
sharding.jdbc.config.sharding.tables.block_tx_detail_info.table-strategy.inline.algorithm-expression=block_tx_detail_info_$->{block_height % 5}
sharding.jdbc.config.sharding.tables.block_tx_detail_info.key-generator-column-name=pk_id

#### 如果需要对更多的数据库表进行分片，请按上面的例子进行修改、配置

```

##### 数据库读写分离的配置：

数据库读写分离的主要设计目标是让用户无痛地使用主从数据库集群，就好像使用一个数据库一样。读写分离的特性支持往主库写入数据，往从库查询数据，从而减轻数据库的压力，提升服务的性能。

**注意**，本组件不会实现主库和从库的数据同步、主库和从库的数据同步延迟导致的数据不一致、主库双写或多写。

```
#### 配置一主两从的数据库
sharding.jdbc.datasource.names=master,slave0,slave1

sharding.jdbc.datasource.master.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.master.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.master.url=jdbc:mysql://localhost:3306/master
sharding.jdbc.datasource.master.username=root
sharding.jdbc.datasource.master.password=

sharding.jdbc.datasource.slave0.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.slave0.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.slave0.url=jdbc:mysql://localhost:3306/slave0
sharding.jdbc.datasource.slave0.username=root
sharding.jdbc.datasource.slave0.password=

sharding.jdbc.datasource.slave1.type=org.apache.commons.dbcp.BasicDataSource
sharding.jdbc.datasource.slave1.driver-class-name=com.mysql.jdbc.Driver
sharding.jdbc.datasource.slave1.url=jdbc:mysql://localhost:3306/slave1
sharding.jdbc.datasource.slave1.username=root
sharding.jdbc.datasource.slave1.password=

sharding.jdbc.config.masterslave.name=ms
sharding.jdbc.config.masterslave.master-data-source-name=master
sharding.jdbc.config.masterslave.slave-data-source-names=slave0,slave1

sharding.jdbc.config.props.sql.show=true

```


