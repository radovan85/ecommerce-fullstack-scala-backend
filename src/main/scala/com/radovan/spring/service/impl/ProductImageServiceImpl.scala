package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ProductImageDto
import com.radovan.spring.entity.ProductImageEntity
import com.radovan.spring.exceptions.FileUploadException
import com.radovan.spring.repository.ProductImageRepository
import com.radovan.spring.service.{ProductImageService, ProductService}
import com.radovan.spring.utils.FileValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile

import java.util
import java.util.stream.Collectors
import java.util.{Objects, Optional}

@Service
class ProductImageServiceImpl extends ProductImageService {

  private var imageRepository: ProductImageRepository = _
  private var productService: ProductService = _
  private var tempConverter: TempConverter = _
  private var fileValidator: FileValidator = _

  @Autowired
  private def injectAll(imageRepository: ProductImageRepository, productService: ProductService, tempConverter: TempConverter,
                        fileValidator: FileValidator): Unit = {
    this.imageRepository = imageRepository
    this.productService = productService
    this.tempConverter = tempConverter
    this.fileValidator = fileValidator
  }

  @Transactional
  override def addImage(file: MultipartFile, productId: Integer): ProductImageDto = {
    val product = productService.getProductById(productId)
    fileValidator.validateFile(file)
    imageRepository.findByProductId(productId) match {
      case Some(image) =>
        imageRepository.deleteById(image.getId)
        imageRepository.flush()
      case None =>
    }
    try {
      val image = new ProductImageDto
      image.setProductId(productId)
      image.setName(StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename)))
      image.setContentType(file.getContentType)
      image.setSize(file.getSize)
      image.setData(file.getBytes)
      val imageIdOptional = Optional.ofNullable(product.getImageId)
      if (imageIdOptional.isPresent) image.setId(imageIdOptional.get)
      val imageEntity = tempConverter.productImageDtoToEntity(image)
      val storedImage = imageRepository.save(imageEntity)
      tempConverter.productImageEntityToDto(storedImage)
    } catch {
      case e: Exception =>
        throw new FileUploadException(new Error("Failed to upload file: " + e.getMessage))
    }
  }

  @Transactional
  override def deleteImage(imageId: Integer): Unit = {
    imageRepository.deleteById(imageId)
    imageRepository.flush()
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[ProductImageDto] = {
    val allImages: util.List[ProductImageEntity] = imageRepository.findAll
    allImages.stream.map[ProductImageDto](tempConverter.productImageEntityToDto).collect(Collectors.toList[ProductImageDto])
  }
}
