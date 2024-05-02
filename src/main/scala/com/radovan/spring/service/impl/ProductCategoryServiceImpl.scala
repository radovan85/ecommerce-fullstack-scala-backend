package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductCategoryDto
import com.radovan.spring.entity.ProductCategoryEntity
import com.radovan.spring.exceptions.{ExistingInstanceException, InstanceUndefinedException}
import com.radovan.spring.repository.ProductCategoryRepository
import com.radovan.spring.service.{ProductCategoryService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class ProductCategoryServiceImpl extends ProductCategoryService {

  private var categoryRepository: ProductCategoryRepository = _
  private var tempConverter: TempConverter = _
  private var productService: ProductService = _

  @Autowired
  private def injectAll(categoryRepository: ProductCategoryRepository, tempConverter: TempConverter, productService: ProductService): Unit = {
    this.categoryRepository = categoryRepository
    this.tempConverter = tempConverter
    this.productService = productService
  }

  @Transactional
  override def addCategory(category: ProductCategoryDto): ProductCategoryDto = {
    categoryRepository.findByName(category.getName) match {
      case Some(_) => throw new ExistingInstanceException(new Error("This category already exists!"))
      case None =>
        val categoryEntity = tempConverter.categoryDtoToEntity(category)
        val storedCategory = categoryRepository.save(categoryEntity)
        tempConverter.categoryEntityToDto(storedCategory)
    }
  }

  @Transactional(readOnly = true)
  override def getCategoryById(categoryId: Integer): ProductCategoryDto = {
    val categoryEntity = categoryRepository.findById(categoryId).orElseThrow(() => new InstanceUndefinedException(new Error("The category has not been found")))
    tempConverter.categoryEntityToDto(categoryEntity)
  }

  @Transactional
  override def updateCategory(category: ProductCategoryDto, categoryId: Integer): ProductCategoryDto = {
    val currentCategory = getCategoryById(categoryId)
    val categoryOpt = categoryRepository.findByName(category.getName)

    categoryOpt.filter(_.getProductCategoryId != currentCategory.getProductCategoryId).foreach { _ =>
      throw new ExistingInstanceException(new Error("This category already exists!"))
    }

    category.setProductCategoryId(currentCategory.getProductCategoryId)
    val updatedCategory = categoryRepository.saveAndFlush(tempConverter.categoryDtoToEntity(category))
    tempConverter.categoryEntityToDto(updatedCategory)
  }

  @Transactional
  override def deleteCategory(categoryId: Integer): Unit = {
    getCategoryById(categoryId)
    productService.deleteProductsByCategoryId(categoryId)
    categoryRepository.deleteById(categoryId)
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[ProductCategoryDto] = {
    val allCategories: util.List[ProductCategoryEntity] = categoryRepository.findAll
    allCategories.stream.map[ProductCategoryDto](tempConverter.categoryEntityToDto).collect(Collectors.toList[ProductCategoryDto])
  }
}
