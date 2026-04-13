package com.fjdialect.controller;

import com.fjdialect.entity.CityResource;
import com.fjdialect.service.CityResourceService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/resource")
@CrossOrigin(origins = "*")
public class CityResourceController {
    
    @Autowired
    private CityResourceService cityResourceService;
    
    /**
     * 获取所有资源
     */
    @GetMapping("/list")
    public Result<List<CityResource>> getAllResources() {
        List<CityResource> list = cityResourceService.getAllResources();
        return Result.success("获取成功", list);
    }
    
    /**
     * 根据城市代码获取资源
     */
    @GetMapping("/{code}")
    public Result<CityResource> getResource(@PathVariable String code) {
        CityResource resource = cityResourceService.getResourceByCode(code);
        return resource != null ? Result.success("获取成功", resource) : Result.error("资源不存在");
    }
    
    /**
     * 保存资源
     */
    @PostMapping("/save")
    public Result<String> saveResource(@RequestBody CityResource resource) {
        boolean success = cityResourceService.saveResource(resource);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    /**
     * 更新资源
     */
    @PostMapping("/update")
    public Result<String> updateResource(@RequestBody CityResource resource) {
        boolean success = cityResourceService.updateResource(resource);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    /**
     * 删除资源
     */
    @PostMapping("/delete/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        boolean success = cityResourceService.deleteResource(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
