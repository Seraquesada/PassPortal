package com.example.msevent.controller;

import com.example.msevent.exception.ResourceNotFoundException;
import com.example.msevent.model.Category;
import com.example.msevent.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/all")
    public ResponseEntity<List<Category>> findAll(){
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findByID(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findByID(id).get());
    }

    @PostMapping
    public ResponseEntity<Category> save(@RequestPart("data") Category category, @RequestParam("file") MultipartFile multipartFile){
        return ResponseEntity.ok().body(service.save(category,multipartFile));
    }

    @PutMapping
    public ResponseEntity<Category> put(@RequestBody Category category) throws ResourceNotFoundException {
        return service.put(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        Optional<Category> category = service.findByID(id);
        if (category.isPresent()){
            service.delete(id);
            return ResponseEntity.ok("Deleted successfully");}
        return ResponseEntity.notFound().build();
    }
}
