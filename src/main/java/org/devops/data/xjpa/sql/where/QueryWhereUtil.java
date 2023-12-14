package org.devops.data.xjpa.sql.where;

import lombok.extern.slf4j.Slf4j;
import org.devops.core.utils.constant.CommonConstant;
import org.devops.core.utils.util.DateUtil;
import org.devops.core.utils.util.StringUtil;
import org.devops.data.xjpa.exception.XjpaExecuteException;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereNodes;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.operate.WhereOperator;
import org.devops.data.xjpa.sql.where.subquery.InlineSubQuery;
import org.devops.data.xjpa.sql.where.usermodel.XQueryWhereValues;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author GENSEN
 * @date 2022/11/23
 * @description where工具
 */
@Slf4j
public class QueryWhereUtil {

    /**
     * source 和 value 同级连接， { source...（and|or） value}
     *
     * @param source
     * @param value
     * @return 连接后的值
     */
    public static IQueryWhereObject attach(IQueryWhereObject source, IQueryWhereObject value) {
        if (value == null || value.isEmpty()) {
            return source;
        }
        if (source.isEmpty()) {
            source = value;
            return source;
        }
        return doAttach(source, value);
    }

    private static IQueryWhereObject doAttach(IQueryWhereObject source, IQueryWhereObject value) {
        if (source instanceof IQueryWhereNodes) {
            ((IQueryWhereNodes) source).children().add(value);
            return source;
        }

        List<IQueryWhereObject> values = new ArrayList<>();
        values.add(source);
        values.add(value);
        return new XQueryWhereValues(values);
    }


    /**
     * 构造where语句
     *
     * @param whereObject
     * @return
     */
    public static String toWhereString(IQueryWhereObject whereObject) {
        return toWhereString(whereObject, false);
    }

    /**
     * 构造where语句
     *
     * @param whereObject
     * @param explicit
     * @return
     */
    public static String toWhereString(IQueryWhereObject whereObject, boolean explicit) {
        XQueryWhereExplorer explorer = new XQueryWhereExplorer(explicit);
        whereObject.accept(explorer);
        return explorer.getWhereString();
    }

    /**
     * 导出参数
     *
     * @param operator
     * @param rawValue
     * @param index
     * @return
     */
    public static Map<Integer, Object> exportParameters(final WhereOperator operator, final Object rawValue, final AtomicInteger index) {
        if (rawValue instanceof InlineSubQuery) {
            return ((InlineSubQuery) rawValue).indexWhereValues(index);
        }

        switch (operator) {
            case IN:
            case NOT_IN: {
                Map<Integer, Object> values = new HashMap<>();
                Collection<Object> formatInValues = formatInValues(rawValue);
                for (Object formatInValue : formatInValues) {
                    values.put(index.getAndIncrement(), formatInValue);
                }
                return values;
            }

            case BETWEEN:
            case NOT_BETWEEN: {
                Map<Integer, Object> values = new HashMap<>();
                Object[] betweenValues = formatBetweenValues(rawValue);
                values.put(index.getAndIncrement(), betweenValues[0]);
                values.put(index.getAndIncrement(), betweenValues[1]);
                return values;
            }
            case IS_NOT_NULL:
            case IS_NULL:
                return Collections.emptyMap();
            default:
//            case EQ:case NEQ:case GT:case EGT:case LT:case ELT:
//            case LIKE:case LIKE_LEFT:case LIKE_RIGHT:
                return Collections.singletonMap(index.getAndIncrement(), operator.wrap(rawValue));
        }
    }

    private static void errorBetween() {
        log.error("between operate require 2 parameters, supported pattern: String('1,3'),Array([1,3]),Collection([1,3])");
        throw new XjpaExecuteException("between operate require 2 parameters");
    }


    /**
     * @param rawValue
     * @return
     */
    private static Collection<Object> formatInValues(final Object rawValue) {
        List<Object> result = new ArrayList<>();
        if (rawValue instanceof Iterable) {
            for (Object v : ((Iterable<?>) rawValue)) {
                result.add(v);
            }
        } else if (rawValue.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(rawValue); i++) {
                result.add(Array.get(rawValue, i));
            }
        } else if (rawValue instanceof String) {
            result.addAll(Arrays.asList(((String) rawValue).split(CommonConstant.COMMA_MARK)));
        } else {
            result.add(rawValue);
        }
        return result;
    }

    /**
     * @param rawValue
     * @return
     */
    private static Object[] formatBetweenValues(final Object rawValue) {
        Object[] result = new Object[2];
        if (rawValue instanceof String) {
            if (StringUtil.isEmpty(rawValue) || !((String) rawValue).contains(CommonConstant.COMMA_MARK)) {
                errorBetween();
            }
            String[] split = ((String) rawValue).split(CommonConstant.COMMA_MARK);
            result[0] = split[0];
            result[1] = split[1];
        } else if (rawValue instanceof Collection) {
            if (((Collection<?>) rawValue).size() != 2) {
                errorBetween();
            }
            Iterator<?> iterator = ((Collection<?>) rawValue).iterator();
            result[0] = iterator.next();
            result[1] = iterator.next();
        } else if (rawValue.getClass().isArray()) {
            if (Array.getLength(rawValue) != 2) {
                errorBetween();
            }
            result[0] = Array.get(rawValue, 0);
            result[1] = Array.get(rawValue, 1);
        } else {
            errorBetween();
        }
        return result;
    }


    /**
     * 构建单个条件where语句
     *
     * @param operator
     * @param rawValue
     * @return
     */
    public static String formatOperationString(final WhereOperator operator, final Object rawValue) {
        StringBuilder valueStringBuilder = new StringBuilder(operator.getOperator());
        valueStringBuilder.append(" ");
        // 内联子查询
        if (rawValue instanceof InlineSubQuery) {
            valueStringBuilder.append(((InlineSubQuery) rawValue).inlineSql(false));
            return valueStringBuilder.toString();
        }
        switch (operator) {
            case IN:
            case NOT_IN: {
                valueStringBuilder.append("(");
                valueStringBuilder.append(formatInValues(rawValue).stream()
                        .map(v -> "?")
                        .collect(Collectors.joining(",")));
                valueStringBuilder.append(")");
                break;
            }
            case BETWEEN:
            case NOT_BETWEEN: {
                valueStringBuilder.append("? and ?");
                break;
            }
            case IS_NOT_NULL:
            case IS_NULL:
                break;
            default: {
                valueStringBuilder.append("?");
            }
        }
        return valueStringBuilder.toString();
    }

    /**
     * 构建单个条件where语句
     *
     * @param operator
     * @param rawValue
     * @return
     */
    public static String formatOperationExplicitString(final WhereOperator operator, final Object rawValue) {
        StringBuilder valueStringBuilder = new StringBuilder(operator.getOperator());
        valueStringBuilder.append(" ");
        // 内联子查询
        if (rawValue instanceof InlineSubQuery) {
            valueStringBuilder.append(((InlineSubQuery) rawValue).inlineSql(true));
            return valueStringBuilder.toString();
        }
        switch (operator) {
            case IN:
            case NOT_IN: {
                valueStringBuilder
                        .append("(")
                        .append(formatInValues(rawValue).stream()
                                .map(QueryWhereUtil::valueToString)
                                .collect(Collectors.joining(",")))
                        .append(")");
                break;
            }
            case BETWEEN:
            case NOT_BETWEEN: {
                Object[] values = formatBetweenValues(rawValue);
                valueStringBuilder
                        .append(valueToString(values[0]))
                        .append(" and ")
                        .append(valueToString(values[1]));
                break;
            }
            case IS_NOT_NULL:
            case IS_NULL:
                break;
            default: {
                valueStringBuilder.append(" ").append(valueToString(operator.wrap(rawValue)));
            }
        }
        return valueStringBuilder.toString();
    }
    
    public static String valueToString(Object rawValue) {
        if (rawValue instanceof Number) {
            return String.valueOf(rawValue);
        }
        if (rawValue instanceof Date) {
            return "'" + DateUtil.getDateTimeFormat((Date) rawValue) + "'";
        }
        if (rawValue instanceof String) {
            return "'" + rawValue + "'";
        }

        return Objects.toString(rawValue);
    }
}
