package com.radovan.spring.controller

import com.radovan.spring.dto.ProductCategoryDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.service.ProductCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._

import java.util

@RestController
@RequestMapping(value = Array("/api/categories"))
class ProductCategoryController {

  @Autowired
  private var categoryService:ProductCategoryService = _

  @GetMapping(value = Array("/allCategories"))
  def getAllCategories: ResponseEntity[util.List[ProductCategoryDto]] = new ResponseEntity(categoryService.listAll, HttpStatus.OK)

  @GetMapping(value = Array("/categoryDetails/{categoryId}"))
  def getCategoryDetails(@PathVariable("categoryId") categoryId: Integer): ResponseEntity[ProductCategoryDto] = new ResponseEntity(categoryService.getCategoryById(categoryId), HttpStatus.OK)

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/storeCategory"))
  def storeCategory(@Validated @RequestBody category: ProductCategoryDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) throw new DataNotValidatedException(new Error("The data has not been validated!"))
    val storedCategory = categoryService.addCategory(category)
    new ResponseEntity("The category with id " + storedCategory.getProductCategoryId + " has been stored!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/updateCategory/{categoryId}"))
  def updateCategory(@PathVariable("categoryId") categoryId: Integer, @Validated @RequestBody category: ProductCategoryDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) throw new DataNotValidatedException(new Error("The data has not been validated!"))
    val updatedCategory = categoryService.updateCategory(category, categoryId)
    new ResponseEntity("The category with id " + updatedCategory.getProductCategoryId + " has been updated without any issues!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteCategory/{categoryId}"))
  def deleteCategory(@PathVariable("categoryId") categoryId: Integer): ResponseEntity[String] = {
    categoryService.deleteCategory(categoryId)
    new ResponseEntity("The category with id " + categoryId + " has been permanently deleted!", HttpStatus.OK)
  }
}
