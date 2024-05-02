package com.radovan.spring.service

import java.util
import org.springframework.web.multipart.MultipartFile
import com.radovan.spring.dto.ProductImageDto

trait ProductImageService {

  def addImage(file: MultipartFile, productId: Integer): ProductImageDto

  def deleteImage(imageId: Integer): Unit

  def listAll: util.List[ProductImageDto]
}