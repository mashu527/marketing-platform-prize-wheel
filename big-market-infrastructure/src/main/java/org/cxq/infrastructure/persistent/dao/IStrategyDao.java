package org.cxq.infrastructure.persistent.dao;


import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.Strategy;

import java.util.List;

@Mapper
public interface IStrategyDao {

    List<Strategy>queryStrategyList();
}
