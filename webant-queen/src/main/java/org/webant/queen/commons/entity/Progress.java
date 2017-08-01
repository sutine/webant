package org.webant.queen.commons.entity;

public class Progress {
    private Long total;
    private Long init;
    private Long pending;
    private Long success;
    private Long fail;

    public Progress(Long total, Long init, Long pending, Long success, Long fail) {
        this.total = total;
        this.init = init;
        this.pending = pending;
        this.success = success;
        this.fail = fail;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getInit() {
        return init;
    }

    public void setInit(Long init) {
        this.init = init;
    }

    public Long getPending() {
        return pending;
    }

    public void setPending(Long pending) {
        this.pending = pending;
    }

    public Long getSuccess() {
        return success;
    }

    public void setSuccess(Long success) {
        this.success = success;
    }

    public Long getFail() {
        return fail;
    }

    public void setFail(Long fail) {
        this.fail = fail;
    }
}
