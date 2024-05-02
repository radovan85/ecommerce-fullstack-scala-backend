package com.radovan.spring.controller

import com.radovan.spring.dto.ProductDto
import com.radovan.spring.dto.ProductImageDto
import com.radovan.spring.exceptions.DataNotValidatedException
import com.radovan.spring.service.ProductImageService
import com.radovan.spring.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.Errors
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._
import org.springframework.web.multipart.MultipartFile

import java.util

@RestController
@RequestMapping(value = Array("/api/products"))
class ProductController {

  @Autowired private var productService: ProductService = _
  @Autowired private var imageService: ProductImageService = _

  @GetMapping(value = Array("/allProducts"))
  def listAllProducts(): ResponseEntity[util.List[ProductDto]] = {
    new ResponseEntity(productService.listAll, HttpStatus.OK)
  }

  @GetMapping(value = Array("/allProducts/{categoryId}"))
  def listAllProductsByCategoryId(@PathVariable("categoryId") categoryId: Integer): ResponseEntity[util.List[ProductDto]] = {
    new ResponseEntity(productService.listAllByCategoryId(categoryId), HttpStatus.OK)
  }

  @GetMapping(value = Array("/productDetails/{productId}"))
  def getProductDetails(@PathVariable("productId") productId: Integer): ResponseEntity[ProductDto] = {
    new ResponseEntity(productService.getProductById(productId), HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/storeProduct"))
  def storeProduct(@Validated @RequestBody product: ProductDto, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated!"))
    }

    val storedProduct: ProductDto = productService.addProduct(product)
    new ResponseEntity(s"The product with id ${storedProduct.getProductId()} has been stored!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PutMapping(value = Array("/updateProduct/{productId}"))
  def updateProduct(@Validated @RequestBody product: ProductDto, @PathVariable("productId") productId: Integer, errors: Errors): ResponseEntity[String] = {
    if (errors.hasErrors) {
      throw new DataNotValidatedException(new Error("The data has not been validated!"))
    }

    val updatedProduct: ProductDto = productService.updateProduct(product, productId)
    new ResponseEntity(s"The product with id ${updatedProduct.getProductId()} has been updated without any issues!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @DeleteMapping(value = Array("/deleteProduct/{productId}"))
  def deleteProduct(@PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    productService.deleteProduct(productId)
    new ResponseEntity(s"The product with id $productId has been permanently deleted!", HttpStatus.OK)
  }

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @PostMapping(value = Array("/storeImage/{productId}"))
  def storeImage(@RequestPart("file") file: MultipartFile, @PathVariable("productId") productId: Integer): ResponseEntity[String] = {
    imageService.addImage(file, productId)
    new ResponseEntity("The image has been added without any issues!", HttpStatus.OK)
  }

  @GetMapping(value = Array("/allImages"))
  def getAllImages: ResponseEntity[util.List[ProductImageDto]] = {
    val allImages: util.List[ProductImageDto] = imageService.listAll
    new ResponseEntity(allImages, HttpStatus.OK)
  }
}
