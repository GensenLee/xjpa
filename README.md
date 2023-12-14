# devops-data-xjpa
## xjpa实现

## JPA要求每一个实体Entity,必须有且只有一个主键

## 开始使用
### 模块引入
先引入parent
```xml
<parent>
    <groupId>org.devops</groupId>
    <artifactId>devops-parent</artifactId>
    <version>6.0.0-SNAPSHOT</version>
</parent>
```
再引入相应的模块
```xml
<dependency>
  <groupId>com.devops.data</groupId>
  <artifactId>devops-data-xjpa</artifactId>
</dependency>
```

配置数据源
```java
public class Config{
    
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

```java
import javax.persistence.GeneratedValue;

@Entity
@Table(name = "test_user")
@Data
@EqualsAndHashCode(callSuper = false)
public class TestUser extends BaseBean {

   @Column(name = "id", columnDefinition = "bigint(20)")
   @Id
   // 如果不使用数据库自增主键， generator 可以指定为 org.devops.data.xjpa.table.identifier.IdentifierGeneratorType 枚举中的值
   // 默认为值 SnowflakeTo36
   @GeneratedValue(generator = "SnowflakeTo32")
   private Long id;

   @Column(name = "name", columnDefinition = "varchar(100)")
   private String name;

}


```

## 相关文档API
假设有一张表 user

|字段名|类型|是否主键|
|:----|:--|:--|
|id|bigint(20)|Y|
|user_name|varchar(20)|N|
|status|int(11)|N|
```java

@Entity
@Table(name = "user")
public class User {

   public final static String ID = "id";
   public final static String USER_NAME = "user_name";
   public final static String STATUS = "status";

   @Column(name = "id", columnDefinition = "bigint(20)")
   @Id
   private Long id;
   @Column(name = "user_name", columnDefinition = "varchar(20)")
   private String userName;
   @Column(name = "status", columnDefinition = "int(11)")
   private Integer status;
}
```
### 初始化model类
```java
@Repository
public interface UserRepository extends StandardJpaRepository<Long,User>{
}
```

private UserRepository userRepository;

1. 增

单个插入，返回主键ID
```java
User user = new User();
userRepository.insert(user);
```
批量插入，返回主键ID
 ```java
User user = new User();
List<User> list = new ArrayList<User>();
list.add(user);
userRepository.insert(list);
```
2. 删（没有删除条件的时候会报错，为了防止全表删除）

单个删,根据主键删除
```java
// 方法1
userRepository.deleteById(1);
// 方法2
userRepository
  .where(User.ID,1)
  .delete();
```
批量删,根据主键删除
```java
// 方法1
userRepository.deleteByIds(new ArrayList<Long>(){{add(1);}});
// 方法2
userRepository
  .where(User.ID,new ArrayList<Long>(){{add(1);}},WhereOperator.IN)
  .delete();
```
3. 改

改单个,根据主键改
```java
User user = new User();
// 方法1
userRepository.update(user);
// 方法2
userRepository
  .where(User.ID,1)
  .update(User.NAME, "updated");
// 只更改其中某些字段
userRepository
  .include(User.STATUS,User.USER_NAME)
  .update(user);
```
 批量改,根据主键改
 ```java
User user = new User();
List<User> list = new ArrayList<User>();
list.add(user);
userRepository.update(list);
    ```
4. 查

使用where条件
```java
//单个
userRepository
  .where(User.ID,1)
  .get();
//列表
userRepository
  .where(User.ID,1)
  .list();
//获取数量
userRepository
  .where(User.ID,1)
  .count();
//带其他条件
userRepository
  .where(User.ID,1)
  .orderByColumn(User.ID,"DESC")
  .orderByColumn(User.STATUS,"ASC")
  .limit(0,20)
  .list();
```
使用QueryWhere
```java
QueryWhere mw = new QueryWhere();
mw.add(User.ID,1);
//单个
userRepository
  .where(mw)
  .get();
//列表
userRepository
  .where(mw)
  .list();
//获取数量
userRepository
  .where(mw)
  .count();
//带其他条件
userRepository
  .where(mw)
  .orderByColumn(User.ID,"DESC")
  .orderByColumn(User.STATUS,"ASC")
  .limit(0,20)
  .list();
```
其他复杂情况
```java
//其他条件
userRepository
  .where(User.ID,1,WhereOperator.xx)
  .get();
//or 条件
userRepository
  .where(User.ID,1)
  .where(User.status,1,Condition.OR)
  .get();
//自定义语句(能不用,就不要用)
userRepository
  .where("(select * from user)",WhereOperator.PLAIN)
  .get();
//返回一个其他class
userRepository
  .where(User.ID,1,WhereOperator.xx)
  .get(XXXX.class);
```