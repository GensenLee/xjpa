package org.devops.data.xjpa.sql.where.handler;

import lombok.extern.slf4j.Slf4j;
import org.devops.data.xjpa.repository.impl.RepositoryContext;
import org.devops.data.xjpa.repository.impl.enhance.EnhanceCurdBound;
import org.devops.data.xjpa.sql.where.objects.IQueryWhereObject;
import org.devops.data.xjpa.sql.where.usermodel.XQueryWhereValue;
import org.devops.data.xjpa.sql.where.QueryWhereUtil;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author GENSEN
 * @date 2022/11/19
 * @description 逻辑删除控制
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class SoftDeleteQueryWhereHandler implements IQueryWhereHandler {

    private final RepositoryContext context;

    private final IQueryWhereObject whereValue;
    public SoftDeleteQueryWhereHandler(RepositoryContext context) {
        this.context = context;
        this.whereValue = recombine(context);
    }

    /**
     * @param context
     * @return
     */
    private IQueryWhereObject recombine(RepositoryContext context) {

        IQueryWhereObject combine = context.localQueryWhere().combine(context);

        EnhanceCurdBound enhanceCurdBound = context.getSingleton(EnhanceCurdBound.class);
        // 本次忽略逻辑删除条件
        if (enhanceCurdBound.isIgnoreSoftDelete()) {
            return combine;
        }

        // 逻辑删除
        SoftDeleteConfig softDeleteConfig = context.getSingleton(SoftDeleteConfig.class);
        XQueryWhereValue notDeleteWhere = new XQueryWhereValue(softDeleteConfig.getColumn(), softDeleteConfig.getNotDeleteValue());
        if (combine.isEmpty()) {
            return notDeleteWhere;
        }

        return QueryWhereUtil.attach(combine, notDeleteWhere);
    }

    @Override
    public String toWhereString() {
        return QueryWhereUtil.toWhereString(whereValue);
    }

    @Override
    public boolean isEmpty() {
        return whereValue.isEmpty();
    }

    @Override
    public Map<Integer, Object> whereValues() {
        final AtomicInteger index = new AtomicInteger(1);
        return whereValue.indexValues(index);
    }
}
