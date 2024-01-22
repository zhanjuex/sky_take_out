package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    public void saveWithFavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteDishbatch(List<Long> ids);

    /**
     * 根据id查菜品
     * @param id
     * @return
     */
    Dish getDishById(Long id);

    /**
     * 编辑菜品信息
     * @param dish
     * @param dishDTO
     */
    void modifyDishInformation(Dish dish, DishDTO dishDTO);

    /**
     * 根据菜品id查询菜品口味
     * @param id
     * @return
     */
    List<DishFlavor> getDishFlavors(Long id);

    /**
     * 返回菜品分类名称
     * @param categoryId
     * @return
     */
    String getCategoryName(Long categoryId);

    /**
     * 根据分类Id获得菜品数据
     * @param categoryId
     * @return
     */
    List<Dish> getDishByCategoryId(Long categoryId);
}
