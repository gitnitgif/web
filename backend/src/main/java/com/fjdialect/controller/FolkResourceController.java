package com.fjdialect.controller;

import com.fjdialect.entity.FolkResource;
import com.fjdialect.service.FolkResourceService;
import com.fjdialect.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/folk")
@CrossOrigin(origins = "*")
public class FolkResourceController {

    @Autowired
    private FolkResourceService folkResourceService;

    @GetMapping("/list")
    public Result<List<FolkResource>> getAllResources() {
        List<FolkResource> resources = folkResourceService.getAllResources();
        return Result.success("查询成功", resources);
    }

    @GetMapping("/city/{cityCode}")
    public Result<List<FolkResource>> getResourcesByCity(@PathVariable String cityCode) {
        List<FolkResource> resources = folkResourceService.getResourcesByCityCode(cityCode);
        return Result.success("查询成功", resources);
    }

    @GetMapping("/type/{type}")
    public Result<List<FolkResource>> getResourcesByType(@PathVariable String type) {
        List<FolkResource> resources = folkResourceService.getResourcesByType(type);
        return Result.success("查询成功", resources);
    }

    @GetMapping("/{id}")
    public Result<FolkResource> getResourceById(@PathVariable Long id) {
        FolkResource resource = folkResourceService.getResourceById(id);
        if (resource != null) {
            folkResourceService.incrementViewCount(id);
            return Result.success("查询成功", resource);
        }
        return Result.error(404, "资源不存在");
    }

    @PostMapping("/add")
    public Result<String> addResource(@RequestBody FolkResource resource) {
        folkResourceService.addResource(resource);
        return Result.success("添加成功");
    }

    @PutMapping("/update")
    public Result<String> updateResource(@RequestBody FolkResource resource) {
        folkResourceService.updateResource(resource);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteResource(@PathVariable Long id) {
        folkResourceService.deleteResource(id);
        return Result.success("删除成功");
    }
}
