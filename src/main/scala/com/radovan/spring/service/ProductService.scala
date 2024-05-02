package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.ProductDto

trait ProductService {

  def addProduct(product: ProductDto): ProductDto

  def getProductById(productId: Integer): ProductDto

  def updateProduct(product: ProductDto, productId: Integer): ProductDto

  def deleteProduct(productId: Integer): Unit

  def listAll: util.List[ProductDto]

  def listAllByCategoryId(categoryId: Integer): util.List[ProductDto]

  def deleteProductsByCategoryId(categoryId: Integer): Unit
}
