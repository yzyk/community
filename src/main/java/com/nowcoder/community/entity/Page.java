package com.nowcoder.community.entity;

/**
 * information for pages
 */
public class Page {

    // current page
    private int current = 1;
    // max per page
    private int limit = 10;
    // total amount of data, for calculate total number of pages
    private int rows;
    // query path, for reusing link for page
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // get offset row for current page
    public int getOffset() {
        // current * limit - limit
        return (current - 1) * limit;
    }

    // get total number of pages
    public int getTotal() {
        // rows / limit
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    // get start page number
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1: from;
    }

    // get end page number
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total: to;
    }




}
