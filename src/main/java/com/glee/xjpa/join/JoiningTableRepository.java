package com.glee.xjpa.join;

/**
 *
 * 连表查询用法示例：
 *
 *         JoinModel joinModel = aAbsTestUserRepository
 *                 .leftJoin(TestUser.class);
 *
 *         JoinQueryWhere joinQueryWhere = new JoinQueryWhere();
 *         joinQueryWhere.and(ColumnDef.ofLeft("id"), 9, WhereOperator.GT);
 *         joinQueryWhere.and(ColumnDef.ofRight("id"), 9000, WhereOperator.LT);
 *         joinQueryWhere.put(new XQueryWhereString("rt.name not like 'xxxx'"));
 *
 *         List<TestUser> list = joinModel
 * //                .include(ColumnDef.aliasLeft("id", "id"),
 * //                        ColumnDef.aliasLeft("name", "name"),
 * //                        ColumnDef.aliasRight("id", "rid"),
 * //                        ColumnDef.aliasRight("name", "rname"))
 *                 .include(ColumnDef.aliasRight("id", "id"))
 *                 .include(ColumnDef.countRight("id", "count"))
 * //                .distinct(ColumnDef.aliasRight("id", "id"))
 * //                .distinct(ColumnDef.aliasRight("id", "name"))
 *                 .on(TestUser.ID, "id")
 *                 .where(ColumnDef.ofLeft("id"), 90, WhereOperator.GT)
 *                 .where(joinQueryWhere)
 *
 *                 .orderByColumn(ColumnDef.ofLeft("updated_time"), SortType.desc)
 * //                .orderByColumn("updated_time", SortType.desc)
 * //                .orderString("rt.updated_time desc")
 *                 .limit(2)
 *                 .groupByColumns(ColumnDef.ofRight("id"))
 *                 .list(TestUser.class);
 *
 *输出sql：
 * select rt.`id` as id,count(rt.`id`) as count
 *  from test_user as lt left join test_user as rt on (lt.`id` = rt.`id`)
 *  where (lt.`id` > 90 and (lt.`id` > 9 and rt.`id` < 9000 and (rt.name not like 'xxxx')))
 *  group by rt.`id`
 *  order by lt.`updated_time` desc
 *  limit 0,2
 *
 * @author GENSEN
 * @date 2023/6/20
 * @description 连表支持
 */
public interface JoiningTableRepository {

    JoinModel leftJoin(Class<?> rightEntityType);

    JoinModel rightJoin(Class<?> rightEntityType);

    JoinModel innerJoin(Class<?> rightEntityType);

}
