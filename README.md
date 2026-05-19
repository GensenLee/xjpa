# XJPA

> 一个基于条件对象构造 SQL 查询语句的轻量级 ORM 框架

## 简介

XJPA 是一个高性能、轻量级的 ORM 框架，通过条件对象构造 SQL 查询语句，无需编写任何 SQL 即可实现绝大部分增删改查操作。

### 核心特性

- **条件对象构造**：支持条件对象列表、嵌套条件、子查询等复杂条件构造
- **多数据源支持**：根据包路径管理数据源，轻松实现多数据源切换
- **连表查询**：支持 LEFT JOIN、RIGHT JOIN、INNER JOIN 等连表操作
- **SQL 执行支持**：支持执行用户编写的 SQL 语句
- **代码生成**：一键生成实体类和 Repository 类
- **逻辑删除**：支持全局或部分表开启逻辑删除

**注意：XJPA 仅针对 MySQL 实现，未考虑其他数据库类型的兼容性**

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>com.glee</groupId>
    <artifactId>xjpa</artifactId>
    <version>2.0.0</version>
</dependency>
```

### 数据源配置

#### 单数据源配置

```java
@Configuration
public class XJpaConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    @Bean
    public XjpaRepositoryBeanConfig defaultRepositoryBeanConfig() {
        DefaultXjpaRepositoryBeanConfig config = new DefaultXjpaRepositoryBeanConfig();
        config.bind("druidDataSource", "com.demo.dao.repository");
        return config;
    }
}
```

#### 多数据源配置

```java
@Configuration
public class XJpaMultiDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid1")
    public DataSource druidDataSource1() {
        return new DruidDataSource();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid2")
    public DataSource druidDataSource2() {
        return new DruidDataSource();
    }

    @Bean
    public XjpaRepositoryBeanConfig repositoryBeanConfig() {
        DefaultXjpaRepositoryBeanConfig config = new DefaultXjpaRepositoryBeanConfig();
        config.bind("druidDataSource1", "com.demo.dao.user.repository")
              .bind("druidDataSource2", "com.demo.dao.order.repository");
        return config;
    }
}
```

## Entity 与 Repository 生成

XJPA 提供代码生成工具，可根据数据库表一键生成实体类和 Repository 类。

```java
public class XjpaCodeGenerator {

    private static final String url = "jdbc:mysql://localhost:3306/example_db?useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static final String username = "root";
    private static final String password = "password";

    public static void main(String[] args) {
        List<ProjectInfo> projectList = new ArrayList<>();
        
        // 生成实体类
        projectList.add(new ProjectInfo(
            projectDir + "/src/main/java",
            "com.demo.entity",
            "vm/xjpa/XJpaEntity.vm",
            "",
            null,
            true
        ));
        
        // 生成 Repository 类
        projectList.add(new ProjectInfo(
            projectDir + "/src/main/java",
            "com.demo.repository",
            "vm/xjpa/XJpaRepository.vm",
            "Repository",
            new HashSet<>(Collections.singletonList("com.demo.entity.${className}"))
        ));

        List<TableProperty> tables = XjpaAutoCoder.initTable(url, username, password, ".*", null, null);
        for (TableProperty table : tables) {
            Map<String, CreatorFile> files = XjpaAutoCoder.createBaseJavaFile(table, projectList);
            VMCreator.create(table, files);
        }
    }
}
```

## 使用示例

### 基础查询

```java
@Autowired
private UsersRepository usersRepository;

// 查询所有
List<Users> allUsers = usersRepository.listAll();

// 根据ID查询
Users user = usersRepository.getById(1L);

// 根据ID列表查询
List<Users> users = usersRepository.listByIds(Set.of(1L, 2L, 3L));

// 分页查询
List<Users> page = usersRepository.limit(0, 10).list();
```

### 条件查询

```java
// 相等条件
QueryWhere where = new QueryWhere();
where.andEqual(Users.STATUS, "active");
List<Users> activeUsers = usersRepository.list(where);
// SQL: SELECT * FROM users WHERE (`status` = 'active')

// 范围条件
QueryWhere priceWhere = new QueryWhere();
priceWhere.andGreaterThan(Products.PRICE, 100);
priceWhere.andLessThan(Products.PRICE, 1000);
List<Products> products = productsRepository.list(priceWhere);
// SQL: SELECT * FROM products WHERE ((`price` > 100 AND `price` < 1000))

// 组合条件
QueryWhere complexWhere = new QueryWhere();
complexWhere.andEqual(Users.STATUS, "active");

QueryWhere subWhere = new QueryWhere();
subWhere.orEqual(Users.ROLE, "admin");
subWhere.orEqual(Users.ROLE, "manager");

complexWhere.put(subWhere);
List<Users> filteredUsers = usersRepository.list(complexWhere);
// SQL: SELECT * FROM users WHERE ((`status` = 'active' AND (`role` = 'admin' OR `role` = 'manager')))
```

### 子查询

```java
QueryWhere where = new QueryWhere();
where.andEqualOrGreaterThan(Orders.TOTAL_AMOUNT, 399.99);

// 创建子查询
InlineSubQuery subQuery = SubQuery.selectOneColumn(
    Users.class, 
    Users.ID, 
    new QueryWhere(Users.STATUS, "active", WhereOperator.NEQ)
);
where.andIn(Orders.USER_ID, subQuery);

List<Orders> orders = ordersRepository.list(where);
// SQL: SELECT * FROM orders WHERE ((`total_amount` >= 399.99 AND `user_id` IN (SELECT `id` FROM users WHERE `status` <> 'active')))
```

### 排序与分页

```java
// 排序
List<Products> sorted = productsRepository
    .descByColumn(Products.PRICE)
    .ascByColumn(Products.CREATED_AT)
    .list();
// SQL: SELECT * FROM products ORDER BY `price` DESC, `created_at` ASC

// 分页
List<Products> page1 = productsRepository.limit(0, 10).list();  // 第1页
List<Products> page2 = productsRepository.limit(10, 10).list(); // 第2页
```

### 限定返回列

```java
List<Products> result = productsRepository
    .include(Products.NAME, Products.PRICE)
    .list();
// SQL: SELECT `name`, `price` FROM products
```

### Group By 与 Having

```java
List<Orders> orders = ordersRepository
    .include(Orders.USER_ID, "SUM(total_amount) AS total")
    .groupByColumns(Orders.USER_ID)
    .list();
// SQL: SELECT `user_id`, SUM(total_amount) AS total FROM orders GROUP BY `user_id`

// 带 Having 条件
QueryWhere havingWhere = new QueryWhere();
havingWhere.andGreaterThan(ColumnDef.plain("total"), 1000);

List<Orders> filteredOrders = ordersRepository
    .include(Orders.USER_ID, "SUM(total_amount) AS total")
    .groupByColumns(Orders.USER_ID)
    .having(havingWhere)
    .list();
```

### 连表查询

```java
// 基础连表
List<Map<String, Object>> result = ordersRepository
    .leftJoin(Users.class)
    .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
    .include(ColumnDef.ofRight(Users.NAME), ColumnDef.ofLeft(Orders.TOTAL_AMOUNT))
    .list();
// SQL: SELECT rt.`name`, lt.`total_amount` FROM orders AS lt LEFT JOIN users AS rt ON (lt.`user_id` = rt.`id`)

// 连表 + Group By + Having
JoinQueryWhere joinWhere = new JoinQueryWhere();
joinWhere.andGreaterThan(ColumnDef.ofLeft(Orders.TOTAL_AMOUNT), 200);

JoinQueryWhere havingWhere = new JoinQueryWhere();
havingWhere.andGreaterThan(ColumnDef.plain("orderCount"), 1);

List<Map<String, Object>> complexResult = ordersRepository
    .leftJoin(Users.class)
    .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
    .include(ColumnDef.ofRight(Users.NAME), 
             ColumnDef.countLeft(Orders.ID, "orderCount"), 
             ColumnDef.sumLeft(Orders.TOTAL_AMOUNT, "totalAmount"))
    .where(joinWhere)
    .groupByColumns(ColumnDef.ofRight(Users.ID))
    .having(havingWhere)
    .list();
```

### 插入操作

```java
// 单条插入
Users user = new Users();
user.setName("John");
user.setEmail("john@example.com");
usersRepository.insert(user);

// 批量插入
List<Users> userList = Arrays.asList(user1, user2, user3);
usersRepository.insert(userList);
```

### 更新操作

```java
// 根据实体更新（根据ID）
Users user = usersRepository.getById(1L);
user.setName("Updated Name");
usersRepository.update(user);

// 批量更新
usersRepository.update(List.of(user1, user2));

// 条件更新
UpdateSets updateSets = new UpdateSets();
updateSets.append("status", "inactive");

QueryWhere where = new QueryWhere();
where.andLessThan(Users.LAST_LOGIN, new Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000));

usersRepository.update(where, updateSets);
```

### 删除操作

```java
// 根据ID删除
usersRepository.deleteById(1L);

// 批量删除
usersRepository.delete(Set.of(1L, 2L, 3L));

// 条件删除
QueryWhere where = new QueryWhere();
where.andEqual(Users.STATUS, "deleted");
usersRepository.delete(where);
```

## 逻辑删除配置

XJPA 支持逻辑删除，将 DELETE 操作转换为 UPDATE 操作。

### 实体类配置

```java
@XJpaTable(tableName = "users", softDelete = true, softDeleteField = "deleted")
public class Users {
    // ...
    private Integer deleted; // 0 = 未删除, 1 = 已删除
}
```

### 全局配置

在 `application.yml` 中配置默认逻辑删除字段：

```yaml
xjpa:
  soft-delete:
    enabled: true
    field-name: deleted
    deleted-value: 1
    not-deleted-value: 0
```

## 核心接口说明

### StandardXJpaRepository

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `listAll()` | 查询所有记录 | `List<E>` |
| `listByIds(Set<K>)` | 根据ID列表查询 | `List<E>` |
| `getById(K)` | 根据ID查询单条 | `E` |
| `list(QueryWhere)` | 条件查询 | `List<E>` |
| `get(Query)` | 查询单条 | `E` |
| `findById(K)` | 根据ID查询（Optional） | `Optional<E>` |
| `find(Query)` | 查询单条（Optional） | `Optional<E>` |
| `count(QueryWhere)` | 统计数量 | `long` |
| `insert(E)` | 插入单条 | `boolean` |
| `insert(List<E>)` | 批量插入 | `int` |
| `update(E)` | 更新单条 | `boolean` |
| `update(List<E>)` | 批量更新 | `int` |
| `update(QueryWhere, UpdateSets)` | 条件更新 | `int` |
| `delete(K)` | 根据ID删除 | `boolean` |
| `delete(Set<K>)` | 批量删除 | `int` |
| `delete(QueryWhere)` | 条件删除 | `int` |

### XJpaJoiningRepository

| 方法 | 说明 |
|------|------|
| `leftJoin(Class<?>)` | 左连接 |
| `rightJoin(Class<?>)` | 右连接 |
| `innerJoin(Class<?>)` | 内连接 |
| `on(ColumnDef, ColumnDef)` | 设置连接条件 |
| `include(ColumnDef...)` | 限定返回列 |
| `where(JoinQueryWhere)` | 设置 WHERE 条件 |
| `groupByColumns(ColumnDef...)` | GROUP BY |
| `having(JoinQueryWhere)` | HAVING 条件 |

## 注意事项

1. **数据库兼容性**：XJPA 仅支持 MySQL 数据库
2. **分页参数**：`limit(offset, size)` 中 offset 是偏移量，不是页码
3. **条件对象复用**：QueryWhere 对象使用后不会自动清空，如需复用请重新创建
4. **事务管理**：XJPA 依赖 Spring 事务管理，请确保正确配置事务
5. **主键生成**：支持自增主键、UUID、雪花ID等多种主键生成策略

## 版本历史

### 2.0.0
- 重构连表查询 API，支持更灵活的连接条件
- 优化 SQL 模板生成逻辑
- 增强条件对象构造能力
- 完善单元测试

### 1.1.0
- 支持多数据源配置
- 添加逻辑删除功能
- 优化代码生成工具

### 1.0.0
- 基础 CRUD 操作
- 条件查询支持
- 连表查询支持

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

如有问题或建议，请通过以下方式联系：
- GitHub: https://github.com/GensenLee/xjpa
- Gitee: https://gitee.com/GensenLee/xjpa
