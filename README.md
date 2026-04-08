# XJPA

### 介绍
#### xjpa是一个使用条件对象构造SQL查询语句的ORM实现。在项目中可以不用编写任何sql语句即可实现绝大部分的增删改查操作。
- 支持条件对象列表，条件对象嵌套，子查询等条件构造方式。能够应对项目中的绝大部分where条件构造需求，当有特殊条件构造需求是xjpa也支持传入字符串条件对象，该方式允许用户输入任何类型的条件，例如包含函数的条件。
- 支持多数据源，xjpa根据包路径管理数据源。例如可配置com.demo.dao1包使用数据源datasource1，com.demo.dao2包使用数据源datasource2。
- 支持连表查询，目前仅支持2个表使用join方式连接，2个表以上的连接以后可能考虑实现。
- 支持执行用户编写的sql语句。
- 支持实体类和储存层类的生成。根据数据库表一键生成到执行的包路径中。
- 支持全局或部分表开启逻辑删除。xjpa自动处理逻辑删除字段，将delete操作转变为update操作。

**注意：xjpa仅针对MySQL实现，未考虑其他数据库类型的兼容性**

## 开始使用

### 获取源码
从Github中下载最新源码，将xjpa源码引入到项目中作为其中一个module，或下载源码发布到私有maven仓库中，以便于项目中使用

### pom引入xjpa的模块

```xml

<dependency>
    <groupId>com.glee</groupId>
    <artifactId>xjpa</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Entity类与Repository类生成
借助XjpaAutoCoder和VMCreator工具类一键生成，生成方式参照如下逻辑

```java
public class XjpaTestAutocode {

    private static final String url = "jdbc:mysql://localhost:3306/xjpa?useUnicode=true&characterEncoding=utf8&autoReconnect=true&allowMultiQueries=true&useSSL=false&allowPublicKeyRetrieval=true";

    private static final String username = "root";

    private static final String password = "12345678";

    public static String projectDir = "";

    public static final String suffix = "\\src\\main\\java";


    static {
        projectDir = MethodHandles.lookup().lookupClass().getClassLoader().getResource("").getPath().split("/target/")[0] + "/";
        projectDir = projectDir.replaceFirst("^/", "").replace("/", File.separator);
    }

    public static final List<ProjectInfo> projectList = new ArrayList<ProjectInfo>() {{
        add(new ProjectInfo(projectDir + suffix,
                "com.demo.dao.entity",
                "vm/xjpa/XJpaEntity.vm",
                "",
                null,
                true)); // model

        add(new ProjectInfo(projectDir + suffix,
                "com.demo.dao.repository",
                "vm/xjpa/XJpaRepository.vm",
                "Repository",
                new HashSet<String>() {{
                    add("com.demo.dao.entity.${className}");
                }})); // repository
    }};


    public static void main(String[] args) {
        try {
            String tableName = ".*";

            List<TableProperty> liTable = XjpaAutoCoder.initTable(url, username, password, tableName, null, null);
            for (TableProperty table : liTable) {

                Map<String, CreatorFile> modelToFile = XjpaAutoCoder.createBaseJavaFile(table, projectList);
                for (String key : modelToFile.keySet()) {
                    System.out.println(key + " *** " + modelToFile.get(key).toString());
                }
                VMCreator.create(table, modelToFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```
以上逻辑将在com.demo.dao.entity包中生成实体类，在com.demo.dao.repository包中生成repository类

### 配置数据源

bean配置方式，将包命名和数据源名称绑定。添加了如下配置后，xjpa将扫描TestUserRepository所在的包路径的所有Repository，并将druidDataSource作为执行数据源。

```java
public class Config {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }


    @Bean
    public XjpaRepositoryBeanConfig defaultRepositoryBeanConfig() {
        DefaultXjpaRepositoryBeanConfig defaultRepositoryBeanConfig = new DefaultXjpaRepositoryBeanConfig();
        defaultRepositoryBeanConfig
                .bind("druidDataSource", TestUserRepository.class.getPackage().getName());
        return defaultRepositoryBeanConfig;
    }
}

```

如果repository所属的包未配置数据源，将向父级包查找数据源，直至找到可用数据源。

#### 多数据源配置
当项目中使用了多个数据源，xjpa支持不同的repository包配置不同的数据源

bean配置方式时，如以下配置：
通过UserRepository所在的包的任何Repository执行的操作，都将使用数据源druidDataSource1。
通过OrderRepository所在的包的任何Repository执行的操作，都将使用数据源druidDataSource2。

```java
public class Config {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid1")
    public DataSource druidDataSource1() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid2")
    public DataSource druidDataSource2() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }


    @Bean
    public XjpaRepositoryBeanConfig defaultRepositoryBeanConfig() {
        DefaultXjpaRepositoryBeanConfig defaultRepositoryBeanConfig = new DefaultXjpaRepositoryBeanConfig();
        defaultRepositoryBeanConfig
                .bind("druidDataSource1", UserRepository.class.getPackage().getName())
                .bind("druidDataSource2", OrderRepository.class.getPackage().getName());
        return defaultRepositoryBeanConfig;
    }
}

```


### xjpa使用方式

以下为部分用法示例
```java
@SpringBootTest
public class XjpaRepositoryDemo {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProductsRepository productsRepository;

    @Test
    public void testSelectWithoutCondition() {
        List<Users> usersList = usersRepository.list();
        // 输出SQL：select * from `users`
    }


    @Test
    public void testSelectWhenGetSingleOne() {
        Users users = usersRepository.get();
        // 输出SQL：select * from `users` limit 1
    }

    @Test
    public void testSelectWhenGetSingleOneById() {
        Users users = usersRepository.getById(1);
        // 输出SQL：select * from `users` where (`id` in (1)) limit 1
    }

    @Test
    public void testSelectWhenGetListById() {
        List<Users> usersList = usersRepository.listByIds(Arrays.asList(1, 2, 3));
        // 输出SQL：select * from `users` where (`id` in (1,2,3)) limit 3
    }

    @Test
    public void testSelectWithEqualCondition01() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqual(Users.FIRST_NAME, "John");

        List<Users> usersList = usersRepository.list(queryWhere);
        // 输出SQL：select * from `users` where (`first_name` = 'John')
    }


    @Test
    public void testSelectWithEqualCondition02() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqual(Users.STATUS, "active");

        List<Users> usersList = usersRepository.list(queryWhere);
        // 输出SQL：select * from `users` where (`status` = 'active')
    }

    @Test
    public void testSelectWithEqualCondition03() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqual(Products.PRICE, 999.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where (`price` = 999.99)
    }

    @Test
    public void testSelectWithEqualCondition04() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqual(Products.PRICE, 999.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where (`price` = 999.99)
    }

    @Test
    public void testSelectWithGreaterThanCondition01() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andGreaterThan(Products.PRICE, 999.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where (`price` > 999.99)
    }

    @Test
    public void testSelectWithGreaterThanCondition02() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqualOrGreaterThan(Products.PRICE, 999.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where (`price` >= 999.99)
    }

    @Test
    public void testSelectWithGreaterThanCondition03() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqualOrGreaterThan(Products.PRICE, 999.99);
        queryWhere.orGreaterThan(Products.PRICE, 99.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where ((`price` >= 999.99 or `price` > 99.99))
    }

    @Test
    public void testSelectWithLessThanCondition01() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqualOrLessThan(Products.PRICE, 999.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where (`price` <= 999.99)
    }


    @Test
    public void testSelectWithLessThanAndGreaterThanCondition01() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andGreaterThan(Products.PRICE, 999.99);
        queryWhere.andLessThan(Products.PRICE, 99.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        // 输出SQL：select * from `products` where ((`price` > 999.99 and `price` < 99.99))
    }



    @Test
    public void testSelectWithLessThanAndGreaterThanCondition02() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andLessThan(Products.PRICE, 999.99);
        queryWhere.andGreaterThan(Products.PRICE, 99.99);

        List<Products> productsList = productsRepository.list(queryWhere);
        //输出SQL：select * from `products` where ((`price` < 999.99 and `price` > 99.99))
    }


    @Test
    public void testSelectWithCondition03() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqual(Users.STATUS, "active");

        QueryWhere subWhere = new QueryWhere();
        subWhere.andEqual(Users.LAST_NAME, "Doe");
        subWhere.orEqual(Users.LAST_NAME, "Wilson");

        queryWhere.put(subWhere);

        List<Users> usersList = usersRepository.list(queryWhere);
        // 输出SQL：select * from `users` where ((`status` = 'active' and (`last_name` = 'Doe' or `last_name` = 'Wilson')))
    }

    /**
     * 子查询条件
     */
    @Test
    public void testSelectWithSubSelectWhere() {
        QueryWhere queryWhere = new QueryWhere();
        queryWhere.andEqualOrGreaterThan(Orders.TOTAL_AMOUNT, 399.99);
        InlineSubQuery subQuery = SubQuery.selectOneColumn(Users.class, Users.ID, new QueryWhere(Users.STATUS, "active", WhereOperator.NEQ));
        queryWhere.andIn(Orders.USER_ID, subQuery);

        List<Orders> ordersList = ordersRepository.list(queryWhere);
        // 输出SQL：select * from `orders` where ((`total_amount` >= 399.99 and `user_id` in  (select `id` from `users` where `status` <> 'active')))
    }


    /**
     * 分页查询
     * 注：如limit方法多次调用仅最后一次有效
     */
    @Test
    public void testSelectOnPageResult() {
        // 页大小为5，获取第1页
        List<Products> page1 = productsRepository
                .limit(0, 5)
                .list();
        // 输出SQL：select * from `products` limit 0,5

        // 页大小为5，获取第2页
        List<Products> page2 = productsRepository
                .limit(5, 5)
                .list();
        // 输出SQL：select * from `products` limit 5,5
    }

    /**
     * 添加排序条件
     */
    @Test
    public void testSelectWithOrder() {
        // price字段倒序
        List<Products> productsList1 = productsRepository
                .descByColumn(Products.PRICE)
                .list();

        // 输出SQL：select * from `products` order by `price` desc
    }

    /**
     * 添加多个排序条件
     */
    @Test
    public void testSelectWithMultipleOrder() {
        // price字段倒序，created_at字段倒序，stock_quantity字段升序
        List<Products> productsList1 = productsRepository
                .descByColumn(Products.PRICE)
                .descByColumn(Products.CREATED_AT)
                .ascByColumn(Products.STOCK_QUANTITY)
                .list();

        // 输出SQL：select * from `products` order by `price` desc,`created_at` desc,`stock_quantity` asc

        productsRepository.list();
    }

    /**
     * 限定返回列
     */
    @Test
    public void testSelectWithIncludeColumn() {
        List<Products> productsList1 = productsRepository
                .include(Products.NAME, Products.PRICE)
                .list();

        // 输出SQL：select `price`,`name` from `products`
    }

    /**
     * 使用group by分组
     */
    @Test
    public void testSelectWithGroupBy() {
        List<Orders> ordersList = ordersRepository
                .include(Orders.USER_ID, String.format("SUM(%s) as %s", Orders.TOTAL_AMOUNT, Orders.TOTAL_AMOUNT))
                .groupByColumns(Orders.USER_ID)
                .list();
        // 输出SQL：select `user_id`,SUM(total_amount) as total_amount from `orders` group by `user_id`
    }

    /**
     * 使用group by分组并添加having条件
     */
    @Test
    public void testSelectWithGroupByAndHaving() {
        QueryWhere  where = new QueryWhere();
        where.andEqualOrGreaterThan(Orders.TOTAL_AMOUNT, 300);
        where.andLessThan(Orders.TOTAL_AMOUNT, 2000);

        List<Orders> ordersList = ordersRepository
                .include(Orders.USER_ID, String.format("SUM(%s) as %s", Orders.TOTAL_AMOUNT, Orders.TOTAL_AMOUNT))
                .groupByColumns(Orders.USER_ID)
//                .having(where)
                .list();
        // 输出SQL：select `user_id`,SUM(total_amount) as total_amount from `orders` group by `user_id` having (`total_amount` >=  300 and `total_amount` <  2000)
    }

    /**
     * 连表查询
     * 最多支持2个表的join连接
     * 支持使用group by和having
     */

    @Test
    public void testSelectUsingJoin() {

        List<Map<String, Object>> mapList = ordersRepository
                .leftJoin(Users.class)
                .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
                .include(ColumnDef.ofRight(Users.FIRST_NAME), ColumnDef.ofLeft(Orders.TOTAL_AMOUNT))
                .list();
        // 输出SQL；select rt.`first_name`,lt.`total_amount` from orders as lt left join users as rt on (lt.`user_id` = rt.`id`)
    }

    @Test
    public void testSelectUsingJoinWithGroupBy() {
        List<Map<String, Object>> mapList2 = ordersRepository
                .leftJoin(Users.class)
                .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
                .include(ColumnDef.ofRight(Users.FIRST_NAME), ColumnDef.countLeft(Orders.ID, "orderCount"), ColumnDef.sumLeft(Orders.TOTAL_AMOUNT, "orderAmount"))
                .groupByColumns(ColumnDef.ofRight(Users.ID))
                .list();

        // 输出SQL： select rt.`first_name`,count(lt.`id`) as orderCount,sum(lt.`total_amount`) as orderAmount from orders as lt left join users as rt on (lt.`user_id` = rt.`id`) group by rt.`id`
    }


    @Test
    public void testSelectUsingJoinWithHavingAndWhere() {

        JoinQueryWhere joinWhere = new JoinQueryWhere();
        joinWhere.andGreaterThan(ColumnDef.ofLeft(Orders.TOTAL_AMOUNT), 200);

        JoinQueryWhere havingWhere = new JoinQueryWhere();
        havingWhere.andGreaterThan(ColumnDef.plain("orderCount"), 1);

        List<Map<String, Object>> mapList3 = ordersRepository
                .leftJoin(Users.class)
                .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
                .include(ColumnDef.ofRight(Users.FIRST_NAME), ColumnDef.countLeft(Orders.ID, "orderCount"), ColumnDef.sumLeft(Orders.TOTAL_AMOUNT, "orderAmount"))
                .where(joinWhere)
                .groupByColumns(ColumnDef.ofRight(Users.ID))
                .having(havingWhere)
                .list();

        // 输出SQL；select rt.`first_name`,count(lt.`id`) as orderCount,sum(lt.`total_amount`) as orderAmount from orders as lt left join users as rt on (lt.`user_id` = rt.`id`) where lt.`total_amount` >  200 group by rt.`id` having orderCount >  1

    }

    @Test
    public void testInsert() {
        Users users = new Users();
        users.setFirstName("xxxx");
        users.setLastName("xxxx");
        usersRepository.insert(users);
    }

    @Test
    public void testInsertBatch() {
        Users users1 = new Users();
        users1.setFirstName("1");
        users1.setLastName("xxxx");
        Users users2 = new Users();
        users2.setFirstName("2");
        users2.setLastName("xxxx");
        usersRepository.insert(Arrays.asList(users1, users2));
    }

    @Test
    public void testUpdate() {
        Users users = new Users();
        users.setId(1);
        users.setFirstName("xxxx");
        users.setLastName("xxxx");
        usersRepository.update(users);
    }

    @Test
    public void testUpdateBatch() {
        Users users1 = new Users();
        users1.setId(1);
        users1.setFirstName("xxxx");
        users1.setLastName("xxxx");

        Users users2 = new Users();
        users2.setId(2);
        users2.setFirstName("xxxx");
        users2.setLastName("xxxx");

        usersRepository.update(Arrays.asList(users1, users2));
    }


    @Test
    public void testDelete() {
        usersRepository.deleteById(1);
        usersRepository.deleteByIds(Arrays.asList(2,3,4,5));
    }
}
```