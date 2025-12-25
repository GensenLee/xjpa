# XJPA

### 介绍
#### xjpa是一个使用条件对象构造SQL查询语句的ORM实现。在项目中可以不用编写任何sql语句即可实现绝大部分的增删改查操作。
#### 支持条件对象列表，条件对象嵌套，子查询等条件构造方式。能够应对项目中的绝大部分where条件构造需求，当有特殊条件构造需求是xjpa也支持传入字符串条件对象，该方式允许用户输入任何类型的条件，例如包含函数的条件。
#### 支持多数据源，xjpa根据包路径管理数据源。例如可配置com.demo.dao1包使用数据源datasource1，com.demo.dao2包使用数据源datasource2。
#### 支持连表查询，目前仅支持2个表使用join方式连接，2个表以上的连接以后可能考虑实现。
#### 支持执行用户编写的sql语句。
#### 支持实体类和储存层类的生成。根据数据库表一键生成到执行的包路径中。
#### 支持全局或部分表开启逻辑删除。xjpa自动处理逻辑删除字段，将delete操作转变为update操作。

## 开始使用

### pom引入xjpa的模块

```xml

<dependency>
    <groupId>com.glee</groupId>
    <artifactId>xjpa</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Entity类与Repository类生成
#### 借助XjpaAutoCoder和VMCreator工具类一键生成，生成方式参照如下逻辑
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
                "vm/xjpa/JpaEntity.vm",
                "",
                null,
                true)); // model

        add(new ProjectInfo(projectDir + suffix,
                "com.demo.dao.repository",
                "vm/xjpa/JpaRepository.vm",
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
#### 以上逻辑将在com.demo.dao.entity包中生成实体类，在com.demo.dao.repository包中生成repository类

### 配置数据源

#### bean配置方式，将包命名和数据源名称绑定

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

#### 注解配置方式，使用@XjpaDataSource注解，配置的forPackages的com.demo.repository包将会使用druidDataSource作为连接数据源

```java
public class Config {

    @XjpaDataSource(forPackages = "com.demo.repository")
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

}

```
#### 如果repository所属的包未配置数据源，将向父级包查找数据源，直至找到可用数据源

### xjpa使用方式

#### 以下为部分用法示例
```java
@SpringBootTest
public class XjpaSelectRepositoryTest {

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
     * 连表查询
     */
    @Test
    public void testSelectWithJoin() {

        List<Map<String, Object>> mapList = ordersRepository
                .leftJoin(Users.class)
                .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
                .include(ColumnDef.ofRight(Users.FIRST_NAME), ColumnDef.ofLeft(Orders.TOTAL_AMOUNT))
                .list();
        // 输出SQL；select rt.`first_name`,lt.`total_amount` from orders as lt left join users as rt on (lt.`user_id` = rt.`id`)

        List<Map<String, Object>> mapList2 = ordersRepository
                .leftJoin(Users.class)
                .on(ColumnDef.ofLeft(Orders.USER_ID), ColumnDef.ofRight(Users.ID))
                .include(ColumnDef.ofRight(Users.FIRST_NAME), ColumnDef.countLeft(Orders.ID, "orderCount"), ColumnDef.sumLeft(Orders.TOTAL_AMOUNT, "orderAmount"))
                .groupByColumns(ColumnDef.ofRight(Users.ID))
                .list();

        // 输出SQL： select rt.`first_name`,count(lt.`id`) as orderCount,sum(lt.`total_amount`) as orderAmount from orders as lt left join users as rt on (lt.`user_id` = rt.`id`) group by rt.`id`
    }
    
    @Test
    public void testInsert01() {
        Users users = new Users();
        users.setFirstName("xxxx");
        users.setLastName("xxxx");
        usersRepository.insert(users);
    }

    @Test
    public void testInsert02() {
        Users users1 = new Users();
        users1.setFirstName("1");
        users1.setLastName("xxxx");
        Users users2 = new Users();
        users2.setFirstName("2");
        users2.setLastName("xxxx");
        usersRepository.insert(Arrays.asList(users1, users2));
    }

    @Test
    public void testUpdate01() {
        Users users = new Users();
        users.setId(1);
        users.setFirstName("xxxx");
        users.setLastName("xxxx");
        usersRepository.update(users);
    }


    @Test
    public void testDelete01() {
        usersRepository.deleteById(1);
    }
}
```