package org.cxq.infrastructure.persistent.dao;


import org.apache.ibatis.annotations.Mapper;
import org.cxq.infrastructure.persistent.po.Award;

import java.util.List;

@Mapper
public interface IAwardDao {

    List<Award>queryAwardList();
}
