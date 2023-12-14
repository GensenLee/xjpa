package org.devops.data.xjpa.sql.executor;

import java.util.Collections;
import java.util.List;

/**
 * @author GENSEN
 * @date 2022/10/31
 * @description 排序指定
 */
public interface SortHandler {

    List<SortSet> sortRequestList();

    /**
     * @return 是否需要排序
     */
    boolean requiredSort();

    class SortSet {
        private final String column;
        private final SortType sortType;

        public SortSet(String column, SortType sortType) {
            this.column = column;
            this.sortType = sortType;
        }

        public String getColumn() {
            return column;
        }

        public SortType getSortType() {
            return sortType;
        }
    }

    static SortHandler empty() {
        return new EmptySort();
    }

    class EmptySort implements SortHandler {
        @Override
        public List<SortSet> sortRequestList() {
            return Collections.emptyList();
        }

        @Override
        public boolean requiredSort() {
            return false;
        }
    }

}
