package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品，{}", dishDTO);
        dishService.saveWithFavor(dishDTO);
        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，参数为：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品删除
     * @return
     */
    @DeleteMapping
    @ApiOperation("菜品删除")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("菜品删除，参数为：{}", ids);
        dishService.deleteDishbatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品信息")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品信息，参数为：{}", id);
        // 根据id查询菜品信息
        Dish dish = dishService.getDishById(id);
        if (dish != null) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            List<DishFlavor> dishFlavors = dishService.getDishFlavors(id);
            dishVO.setFlavors(dishFlavors);
            dishVO.setCategoryName(dishService.getCategoryName(dish.getCategoryId()));
            return Result.success(dishVO);
        }
        return Result.error(MessageConstant.DISH_NOT_FOUND);
    }

    /**
     * 编辑菜品信息
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑菜品信息")
    public Result modifyDishInformation(@RequestBody DishDTO dishDTO) {
        log.info("根据id查询菜品信息，参数为：{}", dishDTO);
        Dish dish = dishService.getDishById(dishDTO.getId());
        dishService.modifyDishInformation(dish, dishDTO);
        return Result.success();
    }
}
