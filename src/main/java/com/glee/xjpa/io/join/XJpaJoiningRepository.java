package com.glee.xjpa.io.join;

import com.glee.xjpa.exception.XJpaException;
import com.glee.xjpa.io.JoiningQuery;

import javax.swing.table.TableColumn;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author GENSEN
 * @date 2026/1/8
 * @description
 */
@SuppressWarnings("rawtypes")
public class XJpaJoiningRepository implements JoiningRepository, JoiningContext{

    private final Map<JoinPoint, JoiningTableIndex> joiningTableIndexMap;

    private final InitJoinPoint initJoinPoint;


    public XJpaJoiningRepository(InitJoinPoint initJoinPoint) {
        this.initJoinPoint = initJoinPoint;
        this.joiningTableIndexMap = new HashMap<>();
        joinPointRegister(initJoinPoint);
    }


    @Override
    public JoinPoint join(AbstractJoinOn joinOn) {


//        ChainedStyleXJpaRepository registeredRepositoryOfEntity = repositoryRegister.findRegisteredRepositoryOfEntity(joinOn.getJoinEntity());
//
//        RepositoryPlugin repositoryPlugin = new RepositoryPlugin(joinOn.getJoinEntity());
//
//        XJpaJoinPoint joinPoint = new XJpaJoinPoint(this, entityTable, joinOn.isSoftDeleteEnabled());



        return null;
    }

    @Override
    public List<Map<String, Object>> list(JoiningQuery query) {
        return null;
    }

    @Override
    public long count(JoiningQuery query) {
        return 0;
    }

    @Override
    public long count(TableColumn countColumn, JoiningQuery query) {
        return 0;
    }

    @Override
    public <T> List<T> list(JoiningQuery query, Class<T> resultType) {
        return null;
    }

    @Override
    public void joinPointRegister(JoinPoint joinPoint) {
        int index = joiningTableIndexMap.size() + 1;
        JoiningTableIndex joiningTableIndex = new JoiningTableIndex(index, createTableAlias(joinPoint, index), joinPoint.isSoftDeleteEnabled());
        joiningTableIndexMap.put(joinPoint, joiningTableIndex);
    }

    private String createTableAlias(JoinPoint joinPoint, int index) {
        return "t%d".formatted(index);
    }

    @Override
    public String getTableAlias(JoinPoint joinPoint) {
        return Optional.ofNullable(joiningTableIndexMap.get(joinPoint))
                .map(JoiningTableIndex::tableAlias)
                .orElseThrow(() -> new XJpaException("join point does not exist"));
    }
}
