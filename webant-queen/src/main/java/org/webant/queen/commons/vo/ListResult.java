package org.webant.queen.commons.vo;

import java.util.List;

public class ListResult<T> {
    private long total = 0;
    private List<T> list;

    public ListResult(long total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
