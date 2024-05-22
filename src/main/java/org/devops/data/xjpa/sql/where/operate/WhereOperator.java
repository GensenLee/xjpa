package org.devops.data.xjpa.sql.where.operate;

public enum WhereOperator {
    /**
     * 等于
     */
    EQ("="),
    /**
     * 不等于
     */
    NEQ("<>"),
    /**
     * 大于
     */
    GT(">"),
    /**
     * 大于等于
     */
    EGT(">="),
    /**
     * 小于
     */
    LT("<"),
    /**
     * 小于等于
     */
    ELT("<="),
    /**
     * 模糊查询
     */
    LIKE("like"){
        @Override
        public Object wrap(Object value) {
            return "%" + value + "%";
        }
    },
    LIKE_LEFT("like"){
        @Override
        public Object wrap(Object value) {
            return "%" + value;
        }
    },
    LIKE_RIGHT("like"){
        @Override
        public Object wrap(Object value) {
            return value + "%";
        }
    },
    /**
     * IN
     */
    IN("in"),
    /**
     * NOT IN
     */
    NOT_IN("not in"),
    /**
     * BETWEEN
     */
    BETWEEN("between"),
    /**
     * NOT BETWEEN
     */
    NOT_BETWEEN("not between"),
    /**
     * 判断非空
     */
    IS_NOT_NULL("is not null"),
    /**
     * 判断为空
     */
    IS_NULL("is null");

    private final String operator;

    WhereOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return this.operator;
    }

    public Object wrap(Object value){
        return value;
    }
}
