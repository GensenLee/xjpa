# XJPA Code Wiki

> 一个基于条件对象构造 SQL 查询语句的轻量级 ORM 框架（MySQL Only）

## 1. 项目概述

XJPA（eXtended JPA）是一个专注于 **基于动态条件对象** 构造原生 SQL 的轻量级 ORM 框架。它不依赖于 JPA/Hibernate 的复杂实现，而是直接通过 JDBC 执行 SQL，并支持：

- 基于实体类 + Repository 接口的 CRUD 操作
- 动态条件构造（`QueryWhere` / `JoiningQueryWhere`）
- 左连接 / 右连接 / 内连接 等多表连表查询
- 多数据源管理（按包路径绑定数据源 Bean）
- 代码生成工具（根据数据库表自动生成 Entity / Repository）
- 逻辑删除配置（全局字段名 + 排除表）
- 与 Spring / Spring Boot 深度集成（自动配置 + 动态代理）

源码根目录：[src/main/java/com/glee/xjpa](file:///workspace/src/main/java/com/glee/xjpa)

### 1.1 版本信息

| 项 | 值 |
| --- | --- |
| GroupId / ArtifactId | `com.glee:xjpa` |
| 版本 | `2.0.0` |
| Java 版本 | `17` |
| Spring 框架 | `spring-core / spring-jdbc / spring-tx / spring-context`（可选） |
| Spring Boot | `spring-boot-autoconfigure`（可选） |
| 数据库 | 仅 MySQL（使用 `mysql-connector-java`） |
| 构建工具 | Maven |

构建配置：[pom.xml](file:///workspace/pom.xml)

### 1.2 关键依赖

| 依赖 | 用途 |
| --- | --- |
| `jakarta.persistence-api` | 解析实体类中的 `@Column` / `@GeneratedValue` 注解 |
| `spring-jdbc` | 通过 `DataSourceUtils` 获取 / 释放连接，支持事务 |
| `spring-boot-autoconfigure` | 通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自动装配 |
| `mysql-connector-java` | MySQL 驱动 |
| `hutool-all` | 反射 / Bean 属性操作工具（`BeanUtil`、`BeanDesc`、`PropDesc`） |
| `reflections` | 类路径扫描（Repository 接口发现） |
| `org.apache.velocity` | 代码生成模板引擎（可选，只在 autocode 模块用到） |

自动装配入口：[org.springframework.boot.autoconfigure.AutoConfiguration.imports](file:///workspace/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports)

其内容为：
```
com.glee.xjpa.configuration.XJpaBootstrapperConfiguration
```

---

## 2. 整体架构

XJPA 的运行时流程可以划分为 5 个关键层次：

```
+-----------------------------------------------------+
|        应用层（业务代码）                           |
|   @XJpaRepository 接口 + @XJpaTable 实体类          |
+-------------------+---------------------------------+
                    |
                    V
+-----------------------------------------------------+
|  配置引导层（configuration）                        |
|  XJpaBootstrapperConfiguration -> XJpaProxyRegistrar |
|  扫描 Repository 接口 -> 构造 TableProperties       |
|  -> 生成 Repository FactoryBean（JDK 动态代理）     |
+-------------------+---------------------------------+
                    |
                    V
+-----------------------------------------------------+
|  元数据层（table）                                  |
|  TableMetadata / XJpaTableMetadata / TableField     |
|  EntityTableField / TableFieldProvider              |
+-------------------+---------------------------------+
                    |
                    V
+-----------------------------------------------------+
|  IO / 查询描述层（io）                              |
|  StandardXJpaRepository                             |
|  Query / XQuery / JoiningQuery / GroupBy / Include  |
|  OrderBy / QueryPage / QueryRequest                 |
+-------------------+---------------------------------+
                    |
                    V
+-----------------------------------------------------+
|  SQL 生成 & 执行层（sql）                           |
|  SqlTemplate / InsertSqlTemplate                    |
|  UpdateSqlTemplate / JoiningSqlTemplate             |
|  XQueryWhere / QueryWhere / WhereOperator           |
|  executor: TypeReadSqlExecutor / InsertSqlExecutor  |
|            UpdateSqlExecutor / JoiningSqlExecutor   |
+-------------------+---------------------------------+
                    |
                    V
+-----------------------------------------------------+
|  数据源管理层（datasource）                         |
|  DataSourceManager / LazyDataSourceManager          |
|  按 Repository 所在包名路由到指定 DataSource Bean   |
+-----------------------------------------------------+
```

### 2.1 启动流程（关键时序）

1. Spring Boot 启动 → 通过 `AutoConfiguration.imports` 自动装配
   [XJpaBootstrapperConfiguration](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaBootstrapperConfiguration.java)。

2. `XJpaBootstrapperConfiguration` 通过 `@Import(XJpaProxyRegistrar.class)`
   将 [XJpaProxyRegistrar](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProxyRegistrar.java) 作为
   `ImportBeanDefinitionRegistrar` 引入。

3. `XJpaProxyRegistrar.registerBeanDefinitions()`：
   - 读取应用容器中所有实现了 `XJpaDataSourceConfig` 接口的 Bean（每个 Bean 提供一个 `包路径 -> 数据源Bean名` 的 Map）。
   - 汇总这些配置后，使用 `ClassPathScanningCandidateComponentProvider` 扫描所有
     带有 `@XJpaRepository` 或实现了 `StandardXJpaRepository` 的类。
   - 为每个扫描到的 Repository 类，构造一个
     [TableProperties](file:///workspace/src/main/java/com/glee/xjpa/table/TableProperties.java)
     对象（包含 Repository 类型、
     [XJpaTableMetadata](file:///workspace/src/main/java/com/glee/xjpa/table/XJpaTableMetadata.java)、
     绑定的数据源 Bean 名）。
   - 通过 `BeanDefinitionBuilder.genericBeanDefinition(RepositoryFactoryBean.class)`
     为每个 Repository 注册一个 JDK 动态代理的 `FactoryBean`。

4. 运行时：当业务代码 `@Autowire` 一个 Repository 时，Spring 会调用
   [RepositoryFactoryBean](file:///workspace/src/main/java/com/glee/xjpa/proxy/RepositoryFactoryBean.java)
   的 `afterPropertiesSet()`，最终生成调用
   [XJpaStandardRepositoryImpl](file:///workspace/src/main/java/com/glee/xjpa/proxy/XJpaStandardRepositoryImpl.java)
   的 `java.lang.reflect.Proxy` 实例。

### 2.2 Repository 代理调用链

```
业务代码
  |
  V
repository.list(QueryWhere)  [StandardXJpaRepository 接口]
  |
  V
Proxy (JDK Dynamic Proxy) -> RepositoryInvocationHandler
  |
  V
XJpaStandardRepositoryImpl
  |
  +-- list(Query)        -> ReadQueryRequest  -> TypeReadSqlExecutor
  +-- count(QueryWhere)  -> ReadQueryRequest  -> TypeReadSqlExecutor (JSON 结果转换)
  +-- insert(entity)     -> InsertQueryRequest-> InsertSqlExecutor
  +-- update(entity)     -> UpdateSqlTemplate -> UpdateSqlExecutor
  +-- delete(id)         -> DeleteQueryRequest-> UpdateSqlExecutor
  |
  V
DataSourceManager.getConnection(repositoryClass)
  |
  V
LazyDataSourceManager -> Spring DataSourceUtils.getConnection(ds)
  |
  V
PreparedStatement.executeQuery/executeUpdate  [JDBC]
```

---

## 3. 模块职责

### 3.1 annotation — 注解定义

目录：[src/main/java/com/glee/xjpa/annotation](file:///workspace/src/main/java/com/glee/xjpa/annotation)

| 类 | 职责 |
| --- | --- |
| [XJpaRepository.java](file:///workspace/src/main/java/com/glee/xjpa/annotation/XJpaRepository.java) | 标记在 Repository 接口上。属性 `dataSource` 可指定数据源 Bean 名；如未指定则走 `XJpaDataSourceConfig` 的包路径映射。本身带 `@Repository`，因此可被 Spring 识别。 |
| [XJpaTable.java](file:///workspace/src/main/java/com/glee/xjpa/annotation/XJpaTable.java) | 标记在实体类上，可提供 `ddl` / `ddlPath` 字段用于建表（元数据层读取）。 |

### 3.2 autocode — 代码生成工具

目录：[src/main/java/com/glee/xjpa/autocode](file:///workspace/src/main/java/com/glee/xjpa/autocode)

| 类 | 职责 |
| --- | --- |
| [XJpaAutoCoder.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/XJpaAutoCoder.java) | 通过 JDBC `show tables` / `show full fields from xxx` / `SHOW INDEX FROM xxx` / `SHOW CREATE TABLE xxx` 查询获取表元数据，填入 `TableProperty`，供模板引擎渲染。 |
| [TableProperty.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/TableProperty.java) | 描述一张表的元数据（表名、字段列表、索引、主键、注释等）。 |
| [ItemProperty.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/ItemProperty.java) | 描述单个字段的元数据（字段名、类型、注释、是否主键、是否可空、默认值等）。 |
| [ProjectInfo.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/ProjectInfo.java) | 描述输出工程路径 / 包名 / 使用的 Velocity 模板 / 后缀名 / import 列表。 |
| [CreatorFile.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/CreatorFile.java) | 生成文件的描述对象。 |
| [VMCreator.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/VMCreator.java) | 调用 Apache Velocity 渲染模板，生成 `.java` 文件。 |
| [JavaType.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/JavaType.java) | 将数据库字段类型映射为 Java 类型。 |

**Velocity 模板** 位于：
- [vm/xjpa/XJpaEntity.vm](file:///workspace/src/main/resources/vm/xjpa/XJpaEntity.vm)
- [vm/xjpa/XJpaRepository.vm](file:///workspace/src/main/resources/vm/xjpa/XJpaRepository.vm)

### 3.3 configuration — 启动与配置

目录：[src/main/java/com/glee/xjpa/configuration](file:///workspace/src/main/java/com/glee/xjpa/configuration)

| 类 | 职责 |
| --- | --- |
| [XJpaBootstrapperConfiguration.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaBootstrapperConfiguration.java) | 自动装配入口。实现 `PriorityOrdered`（最高优先级），`@ComponentScan("com.glee.xjpa")`，`@EnableConfigurationProperties(XJpaProperties.class)`，并 `@Import(XJpaProxyRegistrar.class)`。 |
| [XJpaProxyRegistrar.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProxyRegistrar.java) | **核心启动引导**。实现 `ImportBeanDefinitionRegistrar`。读取 `XJpaDataSourceConfig` Bean，扫描 Repository 类，为其注册 `RepositoryFactoryBean` 动态代理 Bean。 |
| [XJpaProperties.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProperties.java) | 配置属性，前缀 `xjpa`。含 `LogicDelete` 子配置（全局开关、控制列、已删除值、排除表等）。 |
| [XJpaDataSourceConfig.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaDataSourceConfig.java) | 接口：返回 `包名 -> 数据源 Bean 名` 的映射。 |
| [PackageMappingDataSourceConfig.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/PackageMappingDataSourceConfig.java) | `XJpaDataSourceConfig` 的最常见实现。通过 `bind(dataSourceName, String... packages)` 绑定。 |
| [XJpaResources.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaResources.java) | 接口：汇总所有已注册 `TableProperties`，通过 Repository 类 / 表名反向查找。 |
| [DefaultXJpaResources.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/DefaultXJpaResources.java) | `XJpaResources` 的默认实现。 |
| [RepositoryScannerResult.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/RepositoryScannerResult.java) | 存储扫描到的 Repository 类、对应的数据源名称的临时数据结构。 |
| [TableFieldsLoader.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/TableFieldsLoader.java) | 启动后按注册的 `TableFieldProvider` 列表触发懒加载。 |

### 3.4 datasource — 数据源管理

目录：[src/main/java/com/glee/xjpa/datasource](file:///workspace/src/main/java/com/glee/xjpa/datasource)

| 类 | 职责 |
| --- | --- |
| [DataSourceManager.java](file:///workspace/src/main/java/com/glee/xjpa/datasource/DataSourceManager.java) | 接口：`getConnection(repositoryClass)`、`releaseConnection(...)`。 |
| [LazyDataSourceManager.java](file:///workspace/src/main/java/com/glee/xjpa/datasource/LazyDataSourceManager.java) | 懒加载实现：先通过 `XJpaResources` 找到 `TableProperties`，再按 `dataSourceName` 从 Spring BeanFactory 获取 `DataSource`，然后通过 Spring 的 `DataSourceUtils` 获取 / 释放连接（**这样就能参与到 Spring 事务**）。 |

### 3.5 table — 表元数据

目录：[src/main/java/com/glee/xjpa/table](file:///workspace/src/main/java/com/glee/xjpa/table)

| 类 / 接口 | 职责 |
| --- | --- |
| [TableMetadata.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableMetadata.java) | 表元数据接口：表名、实体类、主键类型、字段列表。 |
| [XJpaTableMetadata.java](file:///workspace/src/main/java/com/glee/xjpa/table/XJpaTableMetadata.java) | `TableMetadata` 的实现。内部通过 `TableFieldProvider` 懒加载字段信息，并结合 `EntityUtil` / `jakarta.persistence @Column @GeneratedValue` 注解把实体类字段与 DB 列做映射。提供 `getPrimaryKeyField()` / `isAutoincrement()`。 |
| [TableField.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableField.java) | 描述一个 DB 字段的元数据（字段名、类型、注释、是否主键、是否可空、默认值、Extra、Privileges、Key）。 |
| [TableFieldMetadata.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableFieldMetadata.java) | `TableField` 实现的字段元数据接口。 |
| [EntityTableField.java](file:///workspace/src/main/java/com/glee/xjpa/table/EntityTableField.java) | 把 DB 列元数据 + Java 反射字段 + JPA 注解（`@Column` / `@GeneratedValue`）合成为一个对象，供执行器在 set / get 参数时使用。 |
| [TableFieldProvider.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableFieldProvider.java) | 懒加载字段元数据的接口（函数式接口，返回字段列表）。 |
| [LazyLoadTableFieldProvider.java](file:///workspace/src/main/java/com/glee/xjpa/table/LazyLoadTableFieldProvider.java) | 懒加载实现：在第一次被调用时，真正去 DB 查询 `SHOW FULL FIELDS FROM <table>` 获取表字段列表；失败时由 `TableInitErrorHandler` 处理。 |
| [StorageTableFieldProvider.java](file:///workspace/src/main/java/com/glee/xjpa/table/StorageTableFieldProvider.java) | 直接存储字段列表的 Provider（用于测试或已解析好的场景）。 |
| [TableInitErrorHandler.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableInitErrorHandler.java) | 表初始化失败处理器接口。 |
| [DefaultTableInitErrorHandler.java](file:///workspace/src/main/java/com/glee/xjpa/table/DefaultTableInitErrorHandler.java) | 默认实现：记录错误日志并抛出 `XJpaException`。 |
| [TableProperties.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableProperties.java) | 聚合体：`repositoryClass + XJpaTableMetadata + dataSourceName`。`isSoftDeleteEnabled()` 目前默认返回 `false`，供未来扩展。 |
| [TableKeyGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableKeyGenerator.java) | 主键生成器（占位接口，结合 identifier 子包可扩展雪花 ID / UUID / ULID 等）。 |

#### identifier 子包 — 主键生成策略

目录：[src/main/java/com/glee/xjpa/table/identifier](file:///workspace/src/main/java/com/glee/xjpa/table/identifier)

| 类 | 职责 |
| --- | --- |
| [IdentifierGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/IdentifierGenerator.java) | 主键生成器接口。 |
| [IdentifierGeneratorType.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/IdentifierGeneratorType.java) | 主键生成类型枚举。 |
| [DefaultIdentifierGeneratorFactory.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/DefaultIdentifierGeneratorFactory.java) | 默认的工厂实现，根据类型选择具体生成器。 |
| [SnowflakeIdGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/SnowflakeIdGenerator.java) / [SnowflakeIdentifierGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/SnowflakeIdentifierGenerator.java) | 雪花 ID 生成器。 |
| [UuidIdentifierGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/UuidIdentifierGenerator.java) | UUID 生成器。 |
| [ULID.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/ULID.java) / [UlidIdentifierGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/UlidIdentifierGenerator.java) | ULID 生成器（可排序的类 UUID）。 |
| [TimestampIdentifier.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/TimestampIdentifier.java) | 基于时间戳的 ID。 |

### 3.6 io — 查询 / 插入 / 更新 请求描述

目录：[src/main/java/com/glee/xjpa/io](file:///workspace/src/main/java/com/glee/xjpa/io)

这一层的核心职责是 **描述"我要查什么"**，而不是直接构造 SQL。它是应用层与 SQL 生成层之间的契约。

| 类 / 接口 | 职责 |
| --- | --- |
| [StandardXJpaRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/StandardXJpaRepository.java) | **Repository 核心接口**。提供：`listAll / listByIds / getById / get(Query) / list(Query) / list(GroupByInQuery) / count(QueryWhere) / insert / update(...) / delete(...)`。应用代码应定义子接口泛型化 `<K, E>`，并由 `XJpaProxyRegistrar` 自动生成代理。 |
| [QueryRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/QueryRequest.java) | 请求对象接口：`getSqlTemplate()` 返回最终 SQL，`getParameters()` 返回参数索引 -> 值的 Map。 |
| [ReadQueryRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/ReadQueryRequest.java) | 读请求。将一个 `Query` + `TableProperties` 翻译为 SQL 模板和参数 Map。 |
| [InsertQueryRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/InsertQueryRequest.java) | 插入请求。内部使用 `InsertSqlTemplate` 处理批量参数（每个实体对应一组 `PstParameter`）。 |
| [DeleteQueryRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/DeleteQueryRequest.java) | 删除请求。 |
| [GroupingReadQueryRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/GroupingReadQueryRequest.java) | 带 GROUP BY 的读请求，返回 `List<Map<String,Object>>`。 |

查询描述器：

| 类 / 接口 | 职责 |
| --- | --- |
| [Query.java](file:///workspace/src/main/java/com/glee/xjpa/io/Query.java) | 基础查询描述，支持 `where / limit / distinct / orderBy / include / groupBy` 的流式 API。 |
| [XQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/XQuery.java) | 查询接口（面向列的泛型化设计）。 |
| [XGroupByQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/XGroupByQuery.java) | Group By 子句请求接口。 |
| [JoiningQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/JoiningQuery.java) | 连表查询描述：where / limit / distinct / orderBy / include / groupBy（生成 `JoiningGroupByQuery`）。 |
| [XJoiningQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/XJoiningQuery.java) | 连表查询接口。 |
| [JoiningGroupByQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/JoiningGroupByQuery.java) | 连表 + GROUP BY 组合请求。 |
| [GroupByInQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/GroupByInQuery.java) | GROUP BY 在主查询中的请求对象。 |
| [QueryPage.java](file:///workspace/src/main/java/com/glee/xjpa/io/QueryPage.java) | 分页（start / size）。 |
| [SqlClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/SqlClause.java) / [PlainSqlClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/PlainSqlClause.java) / [WhereSqlClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/WhereSqlClause.java) / [MultiplePlainColumnClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/MultiplePlainColumnClause.java) / [SingleColumnClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/SingleColumnClause.java) | SQL 子句的描述接口与实现，供模板内部拼装。 |

#### io/column — 列定义抽象

目录：[src/main/java/com/glee/xjpa/io/column](file:///workspace/src/main/java/com/glee/xjpa/io/column)

| 类 | 职责 |
| --- | --- |
| [TableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/TableColumn.java) | 列定义接口（用于连表查询时的左右表列别名、聚合列等）。 |
| [AliasTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasTableColumn.java) | 带表别名的普通列（如 `lt.column`、`rt.column`）。 |
| [DefaultAliasTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/DefaultAliasTableColumn.java) | 默认实现。 |
| [AliasCountJoinTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasCountJoinTableColumn.java) | `COUNT(列) AS alias` 聚合列。 |
| [AliasSumJoinTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasSumJoinTableColumn.java) | `SUM(列) AS alias` 聚合列。 |
| [PlainTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/PlainTableColumn.java) | 普通列（用于单表查询时，不需要表别名）。 |
| [AbstractJoinTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AbstractJoinTableColumn.java) | 连接表列抽象基类。 |
| [JoinPointTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/JoinPointTableColumn.java) | 连表关联点列（用于 `ON` 条件两侧）。 |
| [JoiningTable.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/JoiningTable.java) | 连表场景中表的描述：`LEFT_TABLE` / `RIGHT_TABLE` 等常量。 |
| [JoiningTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/JoiningTableColumn.java) | 带连表信息的列实现。 |

#### io/groupby、io/include、io/orderby — Group By / Include / Order By 子句

| 目录 | 主要类 | 用途 |
| --- | --- | --- |
| [io/groupby](file:///workspace/src/main/java/com/glee/xjpa/io/groupby) | `GroupBy` / `JoiningGroupBy` / `GroupBySingleColumn` / `GroupByMultipleColumn` | 描述 GROUP BY 子句。 |
| [io/include](file:///workspace/src/main/java/com/glee/xjpa/io/include) | `IncludeBy` / `JoiningIncludeBy` / `IncludeSingleColumn` / `IncludeMultipleColumn` / `IncludeCountingColumn` | 描述要查询的列（`SELECT ...` 中的列列表）。 |
| [io/orderby](file:///workspace/src/main/java/com/glee/xjpa/io/orderby) | `OrderBy` / `JoiningOrderBy` / `OrderByAsc` / `OrderByDesc` / `OrderByMultipleColumn` | 描述 ORDER BY 子句。 |
| [io/having](file:///workspace/src/main/java/com/glee/xjpa/io/having) | `GroupByHaving` / `JoiningGroupByHave` / `WhereGroupByHaving` / `JoiningWhereGroupByHaving` | 描述 HAVING 条件（将 Where 对象附着在 Group By 之后）。 |

#### io/join — 连表抽象

目录：[src/main/java/com/glee/xjpa/io/join](file:///workspace/src/main/java/com/glee/xjpa/io/join)

| 类 | 职责 |
| --- | --- |
| [JoinSpec.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoinSpec.java) | 连表规格（LEFT / RIGHT / INNER 的枚举 + join 条件）。 |
| [LeftJoin.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/LeftJoin.java) / [RightJoin.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/RightJoin.java) | 左 / 右连接具体实现。 |
| [AbstractJoinOn.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/AbstractJoinOn.java) | JOIN `ON` 子句基类。 |
| [JoinPoint.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoinPoint.java) | JOIN 连接点。 |
| [XJpaJoinPoint.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/XJpaJoinPoint.java) | XJPA 连接点实现。 |
| [JoiningContext.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoiningContext.java) | 连表执行上下文（保存左表、右表、当前 JoinSpec 等）。 |
| [JoiningRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoiningRepository.java) / [XJpaJoiningRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/XJpaJoiningRepository.java) | **连表查询的 Repository 入口**：通过 leftJoin / rightJoin / innerJoin 等方法构造连表查询，并流式指定 include / where / groupBy / having。 |
| [JoiningTableIndex.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoiningTableIndex.java) | 连表中的表索引（左 / 右 / 其它）。 |
| [InitJoinPoint.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/InitJoinPoint.java) | 初始连接点。 |

#### io/update — 更新请求描述

目录：[src/main/java/com/glee/xjpa/io/update](file:///workspace/src/main/java/com/glee/xjpa/io/update)

| 类 | 职责 |
| --- | --- |
| [UpdateSets.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateSets.java) | 描述 `SET column1 = ?, column2 = ? ...` 的多个字段。 |
| [UpdateSetBlock.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateSetBlock.java) / [DefaultUpdateSetBlock.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/DefaultUpdateSetBlock.java) | 单个 SET 子句块的抽象。 |
| [UpdateSetClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateSetClause.java) / [DefaultUpdateSetClause.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/DefaultUpdateSetClause.java) | 单个列的 SET 子句（column = value）。 |
| [UpdateOption.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateOption.java) | 更新选项（如是否启用逻辑删除等）。 |
| [UpdateRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateRequest.java) | 更新请求：聚合 `TableProperties + UpdateSets + QueryWhere`，供 `UpdateSqlExecutor` 执行。 |

### 3.7 sql — SQL 生成与执行

目录：[src/main/java/com/glee/xjpa/sql](file:///workspace/src/main/java/com/glee/xjpa/sql)

#### SQL 模板生成

| 类 | 职责 |
| --- | --- |
| [SqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/SqlTemplate.java) | 通用 SQL 模板生成器：把 Query 描述 + TableMetadata 翻译为可执行 SQL 模板 + 参数 Map。 |
| [InsertSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/InsertSqlTemplate.java) | 插入语句模板：处理多实体批量插入。 |
| [UpdateSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/UpdateSqlTemplate.java) | 更新语句模板：处理单字段更新、条件更新、批量分组更新。 |
| [JoiningSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/JoiningSqlTemplate.java) | 连表查询 SQL 模板：处理 `LEFT JOIN ... ON ...`、别名、include、where、group by、having、order by、limit。 |

#### SQL 执行器（executor）

目录：[src/main/java/com/glee/xjpa/sql/executor](file:///workspace/src/main/java/com/glee/xjpa/sql/executor)

| 类 | 职责 |
| --- | --- |
| [TypeReadSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/TypeReadSqlExecutor.java) | **读执行器**：执行 `QueryRequest`，调用 `con.prepareStatement(sql)` 执行 `executeQuery()`，使用 `ResultConverter<T>` 将 `ResultSet` 转换为结果列表。所有读取操作均走这里。 |
| [InsertSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/InsertSqlExecutor.java) | **插入执行器**：接收 `InsertQueryRequest`（包含多组 `PstParameter`），使用 `addBatch()` 批量执行，最后返回 `int[]` 受影响行数。 |
| [UpdateSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/UpdateSqlExecutor.java) | **更新 / 删除执行器**：同时承担 `update(实体)` / `update(QueryWhere, UpdateSets)` / `delete(QueryWhere)` / 批量更新 任务。批量更新会按 `字段是否为空` 将对象分组，每组单独构造 UPDATE SQL 以节省参数数量，并使用 PreparedStatement 批量执行。 |
| [JoiningSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/JoiningSqlExecutor.java) | **连表查询执行器**：`executeQuery(sql)` 返回 `List<Map<String,Object>>`；`executeCount(sql)` 返回行数。不依赖实体类型，结果用列标签（Label）做 Map 键。 |

#### SQL 日志（logger）

目录：[src/main/java/com/glee/xjpa/sql/logger](file:///workspace/src/main/java/com/glee/xjpa/sql/logger)

| 类 | 职责 |
| --- | --- |
| [SqlLogger.java](file:///workspace/src/main/java/com/glee/xjpa/sql/logger/SqlLogger.java) | 日志接口：`logSql(sql)` / `logAffect(count, start, end)` / `logResult(rs, start, end)`。 |
| [DefaultSqlLogger.java](file:///workspace/src/main/java/com/glee/xjpa/sql/logger/DefaultSqlLogger.java) | 默认实现：使用 SLF4J 打印 SQL、耗时、受影响行数。 |

#### 结果转换（result）

目录：[src/main/java/com/glee/xjpa/sql/result](file:///workspace/src/main/java/com/glee/xjpa/sql/result)

| 类 | 职责 |
| --- | --- |
| [ResultConverter.java](file:///workspace/src/main/java/com/glee/xjpa/sql/result/ResultConverter.java) | 接口：`T convert(ResultSet)`。泛型化返回值。 |
| [MapListResultConverter.java](file:///workspace/src/main/java/com/glee/xjpa/sql/result/MapListResultConverter.java) | 将 ResultSet 转换为 `List<Map<String,Object>>`。列名使用 `ResultSetMetaData.getColumnLabel()`。 |
| [JsonResultConverter.java](file:///workspace/src/main/java/com/glee/xjpa/sql/result/JsonResultConverter.java) | **最常用转换器**：先通过 `MapListResultConverter` 转为 `List<Map>`，再使用 `fastjson` / `hutool` 将每个 Map 序列化为实体对象 `E`。 |

#### 查询条件（where）

目录：[src/main/java/com/glee/xjpa/sql/where](file:///workspace/src/main/java/com/glee/xjpa/sql/where)

这是 XJPA **最具特色的一个子系统**：通过对象树描述任意嵌套的 WHERE 条件，并通过 Visitor（Explorer）模式把它序列化为 SQL 文本 + 参数列表。

**usermodel 面向用户的 API：**

| 类 | 职责 |
| --- | --- |
| [QueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/QueryWhere.java) | **单表查询条件**：继承自 `GenericQueryWhere<String>`，列用字符串列名表示。提供 `andEqual / andIn / andBetween / andLike / andGreaterThan / andLessThan / orEqual / ...` 的流式 API，并支持 `put(QueryWhere)` 进行嵌套（构造 `AND (...)`）。重写 `toString()` 会调用 `XQueryWhereExplorer` 输出 SQL 文本。 |
| [JoiningQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/JoiningQueryWhere.java) | **连表查询条件**：继承自 `GenericQueryWhere<TableColumn>`，列使用 `TableColumn`（左表列 / 右表列 / 聚合列）。用法与 `QueryWhere` 一致。 |
| [GenericQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/GenericQueryWhere.java) | 所有条件对象的基类。它持有一个 `IQueryWhereObject` 根对象（可以是条件节点列表，也可以是子查询或文本），并暴露统一的 `andXxx / orXxx / add / put` 方法。 |
| [QueryWhereModel.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/QueryWhereModel.java) | 泛型接口：统一 `QueryWhere` 与 `JoiningQueryWhere` 给 SQL 模板消费。 |
| [SoftDeleteWhereValue.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/SoftDeleteWhereValue.java) | 逻辑删除条件值（占位）。 |
| [XQueryWhereValue.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/XQueryWhereValue.java) / [XQueryWhereValues.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/XQueryWhereValues.java) / [XQueryWhereString.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/XQueryWhereString.java) | "原始值 / 值列表 / 文本" 的简单包装，供 `IQueryWhereObject` 树内承载。 |

**operate — 操作符与连接符：**

| 类 | 职责 |
| --- | --- |
| [WhereOperator.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/operate/WhereOperator.java) | 操作符枚举：`EQ / NE / GT / LT / GE / LE / IN / NOT_IN / LIKE / LEFT_LIKE / RIGHT_LIKE / BETWEEN / NOT_BETWEEN / IS_NULL / IS_NOT_NULL / ...`。 |
| [Condition.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/operate/Condition.java) | 连接符枚举：`AND` / `OR`。 |

**objects — 内部对象树（Visitor 模式）：**

| 类 | 职责 |
| --- | --- |
| [IQueryWhereObject.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereObject.java) | 条件对象根接口。实现 `accept(visitor)` 用于 SQL 生成；`isEmpty()` 用于跳过空子条件。 |
| [IQueryWhereNode.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereNode.java) | 节点接口：包含 `列 + 操作符 + 值 + 连接符`。 |
| [IQueryWhereNodes.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereNodes.java) | 节点列表：通常是一个 `(cond1 AND cond2 AND cond3)` 块。 |
| [IQueryWhereAttach.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereAttach.java) | 附加节点（用于嵌套子对象）。 |
| [IQueryWhereString.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereString.java) | 直接拼接 SQL 的节点（用于手写片段）。 |
| [IQueryWhereObjectVisitor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/IQueryWhereObjectVisitor.java) | Visitor 接口：提供 `visit(node)` / `visit(nodes)` / `visit(attach)` / `visit(string)` 的多态入口。 |
| [ObjectVisitResult.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/ObjectVisitResult.java) | 访问结果（SQL 字符串 + 参数 Map）。 |
| [package-info.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/objects/package-info.java) | 包文档。 |

**subquery — 子查询：**

| 类 | 职责 |
| --- | --- |
| [SubQuery.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/subquery/SubQuery.java) | 子查询工具类：提供 `selectOneColumn(entityClass, column, where)` 等工厂方法。 |
| [InlineSubQuery.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/subquery/InlineSubQuery.java) | 子查询对象：内部是一个内联的 `SELECT column FROM table WHERE ...`，可直接嵌入 `AND column IN (...)` 等条件。 |

**Explorer — SQL 序列化（即 Visitor 的实现）：**

| 类 | 职责 |
| --- | --- |
| [XQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/XQueryWhere.java) | 查询条件的内部表示（模板层使用的中间形态）。 |
| [XQueryWhereExplorer.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/XQueryWhereExplorer.java) | **单表条件 SQL 生成器**：实现 `IQueryWhereObjectVisitor`，深度优先遍历条件对象树，输出 `WHERE` 之后的 SQL 文本（`?` 占位符），并记录参数位置 -> 值的 Map。 |
| [JoiningQueryWhereExplorer.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/JoiningQueryWhereExplorer.java) | **连表条件 SQL 生成器**：与 `XQueryWhereExplorer` 类似，但节点类型为 `TableColumn`，输出带表别名前缀（如 `lt.` / `rt.`）。 |
| [QueryWhereUtil.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/QueryWhereUtil.java) | 条件对象工具类（合并、判断非空等）。 |
| [AliasTableQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/AliasTableQueryWhere.java) | 带别名的单表查询条件（用于区分主表）。 |
| [AliasTableQueryWhereExplorer.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/AliasTableQueryWhereExplorer.java) | 别名版本的 SQL 生成器。 |

### 3.8 proxy — Repository 代理实现

目录：[src/main/java/com/glee/xjpa/proxy](file:///workspace/src/main/java/com/glee/xjpa/proxy)

| 类 | 职责 |
| --- | --- |
| [RepositoryFactoryBean.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/RepositoryFactoryBean.java) | 实现 Spring `FactoryBean<T>`。`afterPropertiesSet()` 中实例化 `XJpaStandardRepositoryImpl`，再把它包装在 JDK 动态代理中，返回代理对象作为 Repository Bean。 |
| [RepositoryInvocationHandler.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/RepositoryInvocationHandler.java) | `InvocationHandler` 实现：将接口方法调用转发到底层 `XJpaStandardRepositoryImpl`。 |
| [XJpaStandardRepositoryImpl.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/XJpaStandardRepositoryImpl.java) | **Repository 的真正实现类**：构造时持有 `TypeReadSqlExecutor` / `InsertSqlExecutor` / `UpdateSqlExecutor` 三个执行器实例，并暴露 `StandardXJpaRepository<K, E>` 的所有方法。每个方法最终都会构造对应的 `QueryRequest` 或执行器参数并调用。 |

### 3.9 util — 通用工具

目录：[src/main/java/com/glee/xjpa/util](file:///workspace/src/main/java/com/glee/xjpa/util)

| 类 | 职责 |
| --- | --- |
| [NameUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/NameUtil.java) | 名称转换：下划线命名 <-> 驼峰命名；首字母大小写处理。 |
| [EntityUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/EntityUtil.java) | 反射读取实体类的字段集合（含父类）。 |
| [TableUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/TableUtil.java) | 根据 Repository 类创建 `XJpaTableMetadata` 等辅助方法。 |
| [PreparedStatementUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/PreparedStatementUtil.java) | 根据 Java 值类型选择合适的 `ps.setXxx(index, value)`，并处理 MySQL 特有的 `Date / Timestamp / LocalDateTime` 等转换。 |
| [PstParameter.java](file:///workspace/src/main/java/com/glee/xjpa/util/PstParameter.java) | 参数封装：值 + 元数据（字段、类型、是否主键等）。 |
| [PrimitiveTypeUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/PrimitiveTypeUtil.java) | 基本类型与包装类型判断工具。 |
| [ParameterizedTypeUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/ParameterizedTypeUtil.java) | 解析泛型参数（如从 `UserRepository extends StandardXJpaRepository<Long, User>` 中提取主键类型 `Long` 与实体类型 `User`）。 |

### 3.10 exception — 自定义异常

目录：[src/main/java/com/glee/xjpa/exception](file:///workspace/src/main/java/com/glee/xjpa/exception)

| 类 | 职责 |
| --- | --- |
| [XJpaException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaException.java) | 框架通用异常。 |
| [XJpaInitException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaInitException.java) | 启动 / 初始化时抛出。 |
| [XJpaExecuteException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaExecuteException.java) | SQL 执行错误时抛出。 |
| [XJpaEmptyWhereException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaEmptyWhereException.java) | 当一个"无条件的更新 / 删除"被阻止时抛出（防止整表被误操作）。 |

### 3.11 constant — 常量

目录：[src/main/java/com/glee/xjpa/constant](file:///workspace/src/main/java/com/glee/xjpa/constant)

| 类 | 职责 |
| --- | --- |
| [XJpaConstant.java](file:///workspace/src/main/java/com/glee/xjpa/constant/XJpaConstant.java) | 常量集合（如 `PRI_KEY`）。 |
| [SortType.java](file:///workspace/src/main/java/com/glee/xjpa/constant/SortType.java) | 排序类型（ASC / DESC）。 |

---

## 4. 关键类与函数详解

### 4.1 StandardXJpaRepository — 统一 Repository 接口

文件：[StandardXJpaRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/StandardXJpaRepository.java)

```java
public interface StandardXJpaRepository<K extends Serializable, E> {

    // ===== 查询 =====
    List<E> listAll();
    List<E> listByIds(Set<K> ids);
    E      getById(K id);
    E      get(Query query);
    Optional<E> findById(K id);
    Optional<E> find(Query query);
    List<E> list(Query query);
    List<Map<String,Object>> list(GroupByInQuery query);

    long count(QueryWhere where);
    long count(QueryWhere where, String countColumn);

    // ===== 写入 =====
    boolean insert(E entity);
    int     insert(List<E> entities);

    int     update(Set<E> entities);    // 需要事务
    default int update(List<E> entities) { return update(new HashSet<>(entities)); }
    boolean update(E entity);
    int     update(QueryWhere where, UpdateSets updateSets); // 条件更新

    // ===== 删除 =====
    boolean delete(K id);
    int     delete(Set<K> ids);
    int     delete(QueryWhere where);
}
```

用法：业务应用定义子接口，使用 `@XJpaRepository` 注解；由 `XJpaProxyRegistrar` 自动生成代理实现。

### 4.2 XJpaStandardRepositoryImpl — 代理实际实现

文件：[XJpaStandardRepositoryImpl.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/XJpaStandardRepositoryImpl.java)

核心方法 / 执行映射：

| 方法 | 构造的请求 | 调用的执行器 |
| --- | --- | --- |
| `listAll()` | `ReadQueryRequest(new Query(), tableProperties)` | `TypeReadSqlExecutor` + `JsonResultConverter` |
| `listByIds(ids)` | `ReadQueryRequest(Query.where(QueryWhere.andIn(ID, ids)))` | 同上 |
| `list(Query)` / `get(Query)` | `ReadQueryRequest(Query, tableProperties)` | 同上 |
| `list(GroupByInQuery)` | `GroupingReadQueryRequest` | `TypeReadSqlExecutor` + `MapListResultConverter` |
| `count(QueryWhere)` | `ReadQueryRequest`（include `IncludeCountingColumn`） | `TypeReadSqlExecutor` + Map 结果解析 |
| `insert(entity / list)` | `InsertQueryRequest(entities, tableProperties)` | `InsertSqlExecutor` |
| `update(entity / set)` | — | `UpdateSqlExecutor.execute(entity)` / `execute(set)`（按空字段分组） |
| `update(where, sets)` | `UpdateRequest(tableProperties, sets, where)` | `UpdateSqlExecutor.execute(request)` |
| `delete(id / ids / where)` | `DeleteQueryRequest(...)` | `UpdateSqlExecutor.execute(deleteRequest)` |

### 4.3 UpdateSqlExecutor.execute(Set<E>) — 智能批量更新

文件：[UpdateSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/UpdateSqlExecutor.java)

核心算法（`execute(Set<E> objectList)`）：

1. 要求运行在 Spring 事务内（通过 `TransactionSynchronizationManager.isActualTransactionActive()` 判断），否则抛错。
2. 对每个对象，构造一个"哪些字段非空"的 **位图状态 key**（`char[]`，每个字段一位：`'1'` 非空、`'0'` 空）。
3. 按 key 分组：相同状态的对象共享同一条 UPDATE SQL（相同的 SET 子句），并以主键作为 WHERE 条件。
4. 每一组内：
   - 调用 `UpdateSqlTemplate.buildUpdateSql(tableProperties, updateFieldList)` 生成 `UPDATE <table> SET col1=?, col2=? WHERE id=?`
   - 用 `PreparedStatement.addBatch()` 批量执行
   - 累积受影响行数并返回

为什么这么做？—— **避免为每个对象生成单独的 SQL**，也避免把 null 值写进 DB。

### 4.4 TypeReadSqlExecutor — 通用读取

文件：[TypeReadSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/TypeReadSqlExecutor.java)

```java
public <T> List<T> execute(QueryRequest queryRequest, ResultConverter<T> converter) {
    String sql = queryRequest.getSqlTemplate();
    Map<Integer, Object> params = queryRequest.getParameters();
    Connection conn = dataSourceManager.getConnection(tableProperties.getRepositoryType());
    try (PreparedStatement ps = conn.prepareStatement(sql,
            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
        params.forEach((idx, val) -> PreparedStatementUtil.setParameter(ps, val, idx));
        try (ResultSet rs = ps.executeQuery()) {
            sqlLogger.logResult(rs, start, System.currentTimeMillis());
            return converter.convert(rs);
        }
    } catch (SQLException e) {
        throw new XJpaExecuteException("sql execute error", e);
    } finally {
        dataSourceManager.releaseConnection(conn, tableProperties.getRepositoryType());
    }
}
```

核心要点：
- 通过 Spring `DataSourceUtils.getConnection(dataSource)` 获取连接，天然参与事务。
- 使用 `PreparedStatementUtil.setParameter` 统一处理类型 / 空值 / 日期转换。
- `ResultSet.TYPE_SCROLL_INSENSITIVE` 允许结果被日志层重读取。
- `ResultConverter<T>` 是策略模式：`JsonResultConverter<E>` 会把 `List<Map>` 转为实体列表。

### 4.5 XJpaProxyRegistrar — 动态代理生成核心

文件：[XJpaProxyRegistrar.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProxyRegistrar.java)

核心流程：

```
(1) 扫描 Repository 接口
    ClassPathScanningCandidateComponentProvider
      + AnnotationTypeFilter(@XJpaRepository)
      + AssignableTypeFilter(StandardXJpaRepository)
      + OR 检查是否实现 StandardXJpaRepository 的子接口

(2) 为每个 Repository 构造 metadata
    TableFieldProvider container = new LazyLoadTableFieldProvider(repositoryClass, ...);
    XJpaTableMetadata metadata = TableUtil.createMetadata(repositoryClass, container);
    TableProperties tp = new TableProperties(repositoryClass, metadata, dataSourceName);

(3) 注册 FactoryBean
    BeanDefinition bd = BeanDefinitionBuilder
        .genericBeanDefinition(RepositoryFactoryBean.class)
        .addConstructorArgValue(repositoryClass)
        .addConstructorArgValue(tp)
        .addConstructorArgValue(dataSourceManager)
        .setAutowireMode(AUTOWIRE_BY_TYPE)
        .getBeanDefinition();

    beanFactory.registerBeanDefinition(beanName, bd);
```

### 4.6 QueryWhere — 条件对象（核心 API）

文件：[QueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/QueryWhere.java)

典型用法：

```java
QueryWhere where = new QueryWhere()
    .andEqual("status", "active")
    .andGreaterThan("create_time", LocalDate.now().minusDays(7))
    .andIn("user_id", Set.of(1L, 2L, 3L));

List<User> list = userRepository.list(where);
```

嵌套条件：

```java
QueryWhere sub = new QueryWhere()
    .orEqual("role", "admin")
    .orEqual("role", "manager");

QueryWhere main = new QueryWhere()
    .andEqual("status", "active")
    .put(sub);   // AND (role = 'admin' OR role = 'manager')
```

子查询：

```java
InlineSubQuery sub = SubQuery.selectOneColumn(Order.class, "user_id",
    new QueryWhere("total_amount", 1000, WhereOperator.GT));

QueryWhere where = new QueryWhere()
    .andIn("id", sub);   // id IN (SELECT user_id FROM orders WHERE total_amount > 1000)
```

`toString()` 会走 `XQueryWhereExplorer` 把它序列化为 SQL 文本（便于调试）。

---

## 5. 依赖关系图

下面以一个"读取查询"为线索，展示涉及到的主要依赖：

```
+------------------------+          +--------------------------+
|  Application Service   |  @Autowired  UserRepository           |
|  (com.demo.service)    |--------->|  (extends StandardXJpaRepository)
+------------------------+          +-------------+------------+
                                                  |
                                                  V
                                      +--------------------------+
                                      | RepositoryFactoryBean     |
                                      | + TableProperties         |
                                      | + XJpaStandardRepositoryImpl  |
                                      +----+----------------+----+
                                           |                |
                              +------------+----+      +----+-----------+
                              |  TypeReadSqlExecutor |    |  UpdateSqlExecutor  |
                              |  InsertSqlExecutor   |    |  JoiningSqlExecutor  |
                              +---------+------------+      +----+--------------+
                                        |                      |
                      +-----------------+----------------------+-------+
                      |  SqlTemplate / InsertSqlTemplate                |
                      |  UpdateSqlTemplate / JoiningSqlTemplate          |
                      |  XQueryWhere / JoiningQueryWhere / SubQuery       |
                      +-----------------------------+-----------------------+
                                                    |
                     +------------------------------+------------------------+
                     | XJpaTableMetadata / EntityTableField / TableField     |
                     | DataSourceManager -> LazyDataSourceManager           |
                     | (via Spring DataSourceUtils & TransactionSynchronizationManager) |
                     +--------------------------------------------------------+
                                                    |
                                                    V
                                           +----------------+
                                           |  MySQL (JDBC)  |
                                           +----------------+
```

**模块间依赖拓扑（从上到下）：**

```
application (业务代码)
   │
   ▼
io / annotation (契约层)
   │
   ▼
sql (生成 + 执行)   proxy (RepositoryImpl / FactoryBean)
   │                    │
   ▼                    ▼
table (元数据)    configuration (XJpaBootstrapperConfiguration, XJpaProxyRegistrar)
   │                    │
   ▼                    ▼
datasource          util / constant / exception
   │
   ▼
JDBC / MySQL driver
```

---

## 6. 项目运行方式

### 6.1 构建

```bash
cd /workspace
mvn clean package -DskipTests
```

### 6.2 在业务工程中引入依赖

```xml
<dependency>
    <groupId>com.glee</groupId>
    <artifactId>xjpa</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 6.3 配置多数据源 + 包路由

在 Spring 配置类中注册 `PackageMappingDataSourceConfig`：

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource druidDataSource() { return new DruidDataSource(); }

    @Bean
    public XJpaDataSourceConfig defaultRepositoryBeanConfig() {
        return new PackageMappingDataSourceConfig()
                .bind("druidDataSource", "com.demo.dao.user.repository",
                                        "com.demo.dao.order.repository");
    }
}
```

或使用 `@XJpaRepository(dataSource = "druidDataSource")` 单独指定。

### 6.4 定义 Repository 接口

```java
@XJpaRepository
public interface UserRepository extends StandardXJpaRepository<Long, User> {
}
```

实体类 `User` 无需继承任何父类，字段名与 DB 列名通过
[NameUtil](file:///workspace/src/main/java/com/glee/xjpa/util/NameUtil.java) 的下划线 / 驼峰映射自动对齐。若需自定义列名或主键生成策略，使用
`jakarta.persistence @Column` / `@GeneratedValue` 注解即可。

### 6.5 代码生成（可选）

```java
List<ProjectInfo> projectList = new ArrayList<>();
projectList.add(new ProjectInfo(
        "src/main/java", "com.demo.entity",
        "vm/xjpa/XJpaEntity.vm", "", null, true));
projectList.add(new ProjectInfo(
        "src/main/java", "com.demo.repository",
        "vm/xjpa/XJpaRepository.vm", "Repository",
        new HashSet<>(List.of("com.demo.entity.${className}"))));

List<TableProperty> tables =
    XJpaAutoCoder.initTable(url, username, password, ".*", null, null);
for (TableProperty table : tables) {
    Map<String, CreatorFile> files =
        XJpaAutoCoder.createBaseJavaFile(table, projectList);
    VMCreator.create(table, files);
}
```

### 6.6 逻辑删除（全局配置）

`application.yml`：

```yaml
xjpa:
  logic-delete:
    enabled: true
    control-column: deleted
    deleted-value: "1"
    exclude-table:
      - log
      - audit_record
```

对应 [XJpaProperties](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProperties.java) 中的 `LogicDelete` 子配置。

### 6.7 事务支持

- `LazyDataSourceManager` 调用的是 Spring 的 `DataSourceUtils.getConnection(dataSource)`，
  因此只要业务方法标注了 `@Transactional`，XJPA 会**自动参与 Spring 事务**（包括同一事务内多次调用不同 Repository，以及与 `JdbcTemplate` 的混用）。
- `UpdateSqlExecutor.execute(Set<E>)` 会强制要求事务处于活动状态，以避免批量更新缺少事务保护。

---

## 7. 扩展点一览

| 扩展点 | 位置 | 说明 |
| --- | --- | --- |
| 新的数据源绑定策略 | `XJpaDataSourceConfig` + 注册为 Bean | 可以按请求上下文 / 动态路由等方式重写 `getPackageDataSourceMapping()`。 |
| 新的主键生成策略 | `table/identifier` 下实现 `IdentifierGenerator` | 在 `DefaultIdentifierGeneratorFactory` 增加一个分支。 |
| 自定义 SQL 日志 | 实现 `SqlLogger` 接口 | 替换 `RepositoryFactoryBean.afterPropertiesSet()` 中的 `new DefaultSqlLogger()`（或通过配置 Bean 注入）。 |
| 自定义查询结果转换 | 实现 `ResultConverter<T>` | 传给 `TypeReadSqlExecutor.execute(QueryRequest, converter)`。 |
| 新的 WHERE 条件节点类型 | 实现 `IQueryWhereObject` + 扩展 `GenericQueryWhere` | 并添加对应的 `visit(...)` 方法在 `XQueryWhereExplorer`。 |
| 逻辑删除列级定制 | `XJpaProperties.LogicDelete` + 自定义 `SoftDeleteWhereValue` | 可覆盖查询时的默认 WHERE。 |

---

## 8. 主要文件索引（便于快速跳转）

| 功能 | 文件路径 |
| --- | --- |
| 启动引导 / 自动装配 | [XJpaBootstrapperConfiguration.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaBootstrapperConfiguration.java) |
| 代理注册核心 | [XJpaProxyRegistrar.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProxyRegistrar.java) |
| Repository 接口契约 | [StandardXJpaRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/StandardXJpaRepository.java) |
| Repository 实际实现 | [XJpaStandardRepositoryImpl.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/XJpaStandardRepositoryImpl.java) |
| Repository 代理工厂 | [RepositoryFactoryBean.java](file:///workspace/src/main/java/com/glee/xjpa/proxy/RepositoryFactoryBean.java) |
| 表元数据 / 字段元数据 | [XJpaTableMetadata.java](file:///workspace/src/main/java/com/glee/xjpa/table/XJpaTableMetadata.java)、[TableField.java](file:///workspace/src/main/java/com/glee/xjpa/table/TableField.java)、[EntityTableField.java](file:///workspace/src/main/java/com/glee/xjpa/table/EntityTableField.java) |
| Repository/实体注解 | [XJpaRepository.java](file:///workspace/src/main/java/com/glee/xjpa/annotation/XJpaRepository.java)、[XJpaTable.java](file:///workspace/src/main/java/com/glee/xjpa/annotation/XJpaTable.java) |
| 条件对象核心 | [QueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/QueryWhere.java)、[GenericQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/GenericQueryWhere.java) |
| 连表条件对象 | [JoiningQueryWhere.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/usermodel/JoiningQueryWhere.java) |
| WHERE SQL 生成器 | [XQueryWhereExplorer.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/XQueryWhereExplorer.java)、[JoiningQueryWhereExplorer.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/JoiningQueryWhereExplorer.java) |
| 子查询支持 | [SubQuery.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/subquery/SubQuery.java)、[InlineSubQuery.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/subquery/InlineSubQuery.java) |
| 操作符 / 连接符枚举 | [WhereOperator.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/operate/WhereOperator.java)、[Condition.java](file:///workspace/src/main/java/com/glee/xjpa/sql/where/operate/Condition.java) |
| SQL 模板（单表） | [SqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/SqlTemplate.java)、[InsertSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/InsertSqlTemplate.java)、[UpdateSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/UpdateSqlTemplate.java) |
| SQL 模板（连表） | [JoiningSqlTemplate.java](file:///workspace/src/main/java/com/glee/xjpa/sql/JoiningSqlTemplate.java) |
| 读执行器 | [TypeReadSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/TypeReadSqlExecutor.java) |
| 写执行器（插入 / 更新 / 删除） | [InsertSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/InsertSqlExecutor.java)、[UpdateSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/UpdateSqlExecutor.java) |
| 连表执行器 | [JoiningSqlExecutor.java](file:///workspace/src/main/java/com/glee/xjpa/sql/executor/JoiningSqlExecutor.java) |
| 数据源管理 | [LazyDataSourceManager.java](file:///workspace/src/main/java/com/glee/xjpa/datasource/LazyDataSourceManager.java) |
| 包路径数据源配置 | [XJpaDataSourceConfig.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaDataSourceConfig.java)、[PackageMappingDataSourceConfig.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/PackageMappingDataSourceConfig.java) |
| 全局配置（逻辑删除等） | [XJpaProperties.java](file:///workspace/src/main/java/com/glee/xjpa/configuration/XJpaProperties.java) |
| 查询描述（Query / GroupBy / Include / OrderBy） | [Query.java](file:///workspace/src/main/java/com/glee/xjpa/io/Query.java)、[QueryPage.java](file:///workspace/src/main/java/com/glee/xjpa/io/QueryPage.java)、[XJoiningQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/XJoiningQuery.java)、[JoiningQuery.java](file:///workspace/src/main/java/com/glee/xjpa/io/JoiningQuery.java) |
| 列定义（单表 / 连表 / 聚合） | [TableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/TableColumn.java)、[PlainTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/PlainTableColumn.java)、[AliasTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasTableColumn.java)、[AliasCountJoinTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasCountJoinTableColumn.java)、[AliasSumJoinTableColumn.java](file:///workspace/src/main/java/com/glee/xjpa/io/column/AliasSumJoinTableColumn.java) |
| 连表入口（leftJoin / rightJoin / innerJoin） | [XJpaJoiningRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/XJpaJoiningRepository.java)、[JoiningRepository.java](file:///workspace/src/main/java/com/glee/xjpa/io/join/JoiningRepository.java) |
| 更新请求描述 | [UpdateRequest.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateRequest.java)、[UpdateSets.java](file:///workspace/src/main/java/com/glee/xjpa/io/update/UpdateSets.java) |
| 主键生成器 | [DefaultIdentifierGeneratorFactory.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/DefaultIdentifierGeneratorFactory.java)、[SnowflakeIdGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/SnowflakeIdGenerator.java)、[UuidIdentifierGenerator.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/UuidIdentifierGenerator.java)、[ULID.java](file:///workspace/src/main/java/com/glee/xjpa/table/identifier/ULID.java) |
| 代码生成 | [XJpaAutoCoder.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/XJpaAutoCoder.java)、[VMCreator.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/VMCreator.java)、[TableProperty.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/TableProperty.java)、[ProjectInfo.java](file:///workspace/src/main/java/com/glee/xjpa/autocode/ProjectInfo.java) |
| Velocity 模板 | [XJpaEntity.vm](file:///workspace/src/main/resources/vm/xjpa/XJpaEntity.vm)、[XJpaRepository.vm](file:///workspace/src/main/resources/vm/xjpa/XJpaRepository.vm) |
| 反射 / 命名工具 | [NameUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/NameUtil.java)、[EntityUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/EntityUtil.java)、[ParameterizedTypeUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/ParameterizedTypeUtil.java)、[PreparedStatementUtil.java](file:///workspace/src/main/java/com/glee/xjpa/util/PreparedStatementUtil.java) |
| 异常体系 | [XJpaException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaException.java)、[XJpaInitException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaInitException.java)、[XJpaExecuteException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaExecuteException.java)、[XJpaEmptyWhereException.java](file:///workspace/src/main/java/com/glee/xjpa/exception/XJpaEmptyWhereException.java) |

---

## 9. 小结

- **XJPA 是基于 Spring 的轻量级 ORM 框架**，不依赖 Hibernate / JPA，直接通过 JDBC 操作 MySQL。
- **核心思想**：通过 `QueryWhere` / `JoiningQueryWhere` 等对象树描述条件，再由 Visitor 模式的 `XQueryWhereExplorer` 序列化为 SQL 文本与参数绑定。
- **Repository 的生命周期**：由 `XJpaProxyRegistrar` 扫描并注册为 Spring Bean，内部是一个 JDK 动态代理，实际方法转发到 `XJpaStandardRepositoryImpl`，再分派给 `TypeReadSqlExecutor` / `InsertSqlExecutor` / `UpdateSqlExecutor`。
- **多数据源**：通过 `PackageMappingDataSourceConfig` 的 `包路径 -> dataSourceName` 映射实现。每个 Repository 的连接由 `LazyDataSourceManager` 根据 `TableProperties.dataSourceName` 取 Spring Bean 获取。
- **事务支持**：依赖 Spring 的 `DataSourceUtils` / `TransactionSynchronizationManager`，天然与 `@Transactional` 协作。
- **连表查询**：通过 `XJpaJoiningRepository` 的 `leftJoin / rightJoin / innerJoin` 入口，结合 `JoiningQuery + JoiningQueryWhere`，在 `JoiningSqlExecutor` 中执行；结果以 `List<Map<String,Object>>` 返回，不绑定到实体类，灵活。
- **代码生成**：`XJpaAutoCoder` 读取 DB 表元数据，通过 Apache Velocity 渲染实体与 Repository 模板，实现"表驱动"工程脚手架。
- **主键策略**：`table/identifier` 提供雪花 ID、UUID、ULID、时间戳等可扩展实现。
- **逻辑删除**：通过 `XJpaProperties.LogicDelete` 全局配置 `controlColumn / deletedValue / excludeTable`。

生成的完整 Code Wiki 文档位于：[CODE_WIKI.md](file:///workspace/CODE_WIKI.md)。