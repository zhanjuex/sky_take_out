package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annoatation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询相应套餐
     * @param ids
     * @return
     */
    List<Long> getSetmealDishIds(List<Long> ids);

    /**
     * 根据套餐Id插入套餐中
     * @param setmealId
     * @param setmealDishes
     */
    void insert(Long setmealId, List<SetmealDish> setmealDishes);
}
