package com.peter.mall.product.dao;

import com.peter.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品属性
 * 
 * @author Runbo Fang
 * @email fangrunbo0606@gmail.com
 * @date 2023-02-16 23:02:36
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
