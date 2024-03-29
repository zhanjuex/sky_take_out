package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional
    public void saveWithFavor(DishDTO dishDTO) {
        // 向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dishMapper.insert(dish);
        // 获取insert主键插入生成的返回值
        Long id = dish.getId();

        // 往菜品口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Transactional
    public void deleteDishbatch(List<Long> ids) {
        log.info("批量删除菜品，{}", ids);
        // 起售中的菜品不能删除
        List<Dish> dishs = dishMapper.getDishByIds(ids);
        for (Dish dish:dishs)
            if (dish.getStatus().equals(StatusConstant.ENABLE))
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);

        // 套餐中的菜品不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty())
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        // 删除菜品
        dishMapper.deleteByDishIds(ids);

        // 删除口味
        dishFlavorMapper.deleteByIds(ids);
    }

    /**
     * 根据id查菜品
     * @param id
     * @return
     */
    public Dish getDishById(Long id) {
        List<Long> list = new ArrayList<>();
        list.add(id);
        return dishMapper.getDishByIds(list).get(0);
    }

    /**
     * 编辑菜品信息
     * @param dish
     * @param dishDTO
     */
    @Transactional
    public void modifyDishInformation(Dish dish, DishDTO dishDTO) {
        log.info("修改菜品信息，{}，{}", dish, dishDTO);
        dish.setName(dishDTO.getName());
        dish.setCategoryId(dishDTO.getCategoryId());
        dish.setPrice(dishDTO.getPrice());
        dish.setImage(dish.getImage());
        dish.setDescription(dish.getDescription());
        dish.setStatus(dishDTO.getStatus());
        dishMapper.update(dish);

        List<Long> ids = new ArrayList<>();
        ids.add(dishDTO.getId());
        dishFlavorMapper.deleteByIds(ids);

        // 口味可能是新增的，所以没有dishId
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {dishFlavor.setDishId(dishDTO.getId());});
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据菜品id查询菜品口味
     * @param id
     * @return
     */
    public List<DishFlavor> getDishFlavors(Long id) {
        return dishFlavorMapper.getDishFlavors(id);
    }

    /**
     * 返回菜品分类名称
     * @param categoryId
     * @return
     */
    public String getCategoryName(Long categoryId) {
        return categoryMapper.getCategoryName(categoryId);
    }

    /**
     * 根据分类Id获得菜品数据
     * @param categoryId
     * @return
     */
    public List<Dish> getDishByCategoryId(Long categoryId) {
        return dishMapper.getDishByCategoryId(categoryId);
    }
}
