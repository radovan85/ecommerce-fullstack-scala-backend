package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductDto
import com.radovan.spring.entity.ProductEntity
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repository.{CartItemRepository, ProductRepository}
import com.radovan.spring.service.{CartItemService, CartService, ProductCategoryService, ProductService}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors
import scala.collection.JavaConverters._

@Service
class ProductServiceImpl extends ProductService{

  private var productRepository: ProductRepository = _
  private var tempConverter: TempConverter = _
  private var categoryService: ProductCategoryService = _
  private var cartItemService: CartItemService = _
  private var cartService: CartService = _
  private var cartItemRepository: CartItemRepository = _

  @Autowired
  private def injectAll(productRepository: ProductRepository, tempConverter: TempConverter, categoryService: ProductCategoryService,
                        cartItemService: CartItemService, cartService: CartService, cartItemRepository: CartItemRepository): Unit = {

    this.productRepository = productRepository
    this.tempConverter = tempConverter
    this.categoryService = categoryService
    this.cartItemService = cartItemService
    this.cartService = cartService
    this.cartItemRepository = cartItemRepository
  }

  @Transactional
  override def addProduct(product: ProductDto): ProductDto = {
    categoryService.getCategoryById(product.getProductCategoryId)
    val storedProduct = productRepository.save(tempConverter.productDtoToEntity(product))
    tempConverter.productEntityToDto(storedProduct)
  }

  @Transactional(readOnly = true)
  override def getProductById(productId: Integer): ProductDto = {
    val productEntity = productRepository.findById(productId).orElseThrow(() => new InstanceUndefinedException(new Error("The product has not been found!")))
    tempConverter.productEntityToDto(productEntity)
  }

  @Transactional
  override def updateProduct(product: ProductDto, productId: Integer): ProductDto = {
    categoryService.getCategoryById(product.getProductCategoryId)
    val currentProduct = getProductById(productId)
    val allCartItems = cartItemService.listAllByProductId(productId).asScala
    product.setProductId(currentProduct.getProductId)
    if (currentProduct.getImageId != null) product.setImageId(currentProduct.getImageId)
    val updatedProduct = productRepository.saveAndFlush(tempConverter.productDtoToEntity(product))
    if (allCartItems.nonEmpty){
      allCartItems.foreach(item => {
        val itemEntity = tempConverter.cartItemDtoToEntity(item)
        cartItemRepository.saveAndFlush(itemEntity)
      })
    }

    cartService.refreshAllCarts()
    tempConverter.productEntityToDto(updatedProduct)
  }

  @Transactional
  override def deleteProduct(productId: Integer): Unit = {
    getProductById(productId)
    cartItemService.removeAllByProductId(productId)
    cartService.refreshAllCarts()
    productRepository.deleteById(productId)
    productRepository.flush()
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[ProductDto] = {
    val allProducts: util.List[ProductEntity] = productRepository.findAll
    allProducts.stream.map[ProductDto](tempConverter.productEntityToDto).collect(Collectors.toList[ProductDto])
  }

  @Transactional(readOnly = true)
  override def listAllByCategoryId(categoryId: Integer): util.List[ProductDto] = {
    val allProducts = listAll.asScala
    allProducts.filter(_.getProductCategoryId == categoryId).asJava
  }

  @Transactional
  override def deleteProductsByCategoryId(categoryId: Integer): Unit = {
    val allProducts = listAllByCategoryId(categoryId).asScala
    allProducts.foreach(product => deleteProduct(product.getProductId))
  }
}
