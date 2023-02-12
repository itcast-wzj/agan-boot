package com.agan.boot.customer;

import org.apache.ibatis.annotations.SelectProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

@RegisterMapper
public interface SelectByBusinessIdMapper<T> {
    @SelectProvider(type = BaseSelectProviderWithBusinessId.class, method = "dynamicSQL")
    T  selectByBusinessId(Object businessId);
}
