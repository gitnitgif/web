package com.fjdialect.controller;

import com.fjdialect.entity.DialectResource;
import com.fjdialect.service.DialectResourceService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dialect")
@CrossOrigin(origins = "*")
public class DialectResourceController {

    @Autowired
    private DialectResourceService dialectResourceService;

    @GetMapping("/list")
    public Result<List<DialectResource>> getAllResources() {
        List<DialectResource> resources = dialectResourceService.getAllResources();
        return Result.success("查询成功", resources);
    }

    @GetMapping("/city/{cityCode}")
    public Result<List<DialectResource>> getResourcesByCity(@PathVariable String cityCode) {
        List<DialectResource> resources = dialectResourceService.getResourcesByCityCode(cityCode);
        return Result.success("查询成功", resources);
    }

    @GetMapping("/category/{category}")
    public Result<List<DialectResource>> getResourcesByCategory(@PathVariable String category) {
        List<DialectResource> resources = dialectResourceService.getResourcesByCategory(category);
        return Result.success("查询成功", resources);
    }

    @GetMapping("/difficulty/{difficulty}")
    public Result<List<DialectResource>> getResourcesByDifficulty(@PathVariable String difficulty) {
        List<DialectResource> resources = dialectResourceService.getResourcesByDifficulty(difficulty);
        return Result.success("查询成功", resources);
    }

    @GetMapping("/{id}")
    public Result<DialectResource> getResourceById(@PathVariable Long id) {
        DialectResource resource = dialectResourceService.getResourceById(id);
        if (resource != null) {
            dialectResourceService.incrementViewCount(id);
            return Result.success("查询成功", resource);
        }
        return Result.error(404, "资源不存在");
    }

    @PostMapping("/add")
    public Result<String> addResource(@RequestBody DialectResource resource) {
        dialectResourceService.addResource(resource);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> updateResource(@RequestBody DialectResource resource) {
        dialectResourceService.updateResource(resource);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        dialectResourceService.deleteResource(id);
        return Result.success("删除成功");
    }
}
