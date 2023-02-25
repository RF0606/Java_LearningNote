package com.peter.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.peter.common.utils.PageUtils;
import com.peter.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author Runbo Fang
 * @email fangrunbo0606@gmail.com
 * @date 2023-02-16 23:02:36
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

