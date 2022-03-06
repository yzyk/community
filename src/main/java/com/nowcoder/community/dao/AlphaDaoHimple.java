package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alphah")
public class AlphaDaoHimple implements AlphaDao{
    @Override
    public String select() {
        return "HH";
    }
}
