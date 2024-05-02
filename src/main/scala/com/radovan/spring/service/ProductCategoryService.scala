package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.ProductCategoryDto

trait ProductCategoryService {

  def addCategory(category: ProductCategoryDto): ProductCategoryDto

  def getCategoryById(categoryId: Integer): ProductCategoryDto

  def updateCategory(category: ProductCategoryDto, categoryId: Integer): ProductCategoryDto

  def deleteCategory(categoryId: Integer): Unit

  def listAll: util.List[ProductCategoryDto]
}
