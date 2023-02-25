package com.peter.mall.product.dao;

import com.peter.mall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author Runbo Fang
 * @email fangrunbo0606@gmail.com
 * @date 2023-02-16 23:02:36
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {
	
}
