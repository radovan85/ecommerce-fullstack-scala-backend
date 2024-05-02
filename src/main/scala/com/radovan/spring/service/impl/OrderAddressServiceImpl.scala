package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.OrderAddressDto
import com.radovan.spring.entity.OrderAddressEntity
import com.radovan.spring.repository.OrderAddressRepository
import com.radovan.spring.service.OrderAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class OrderAddressServiceImpl extends OrderAddressService{

  private var addressRepository: OrderAddressRepository = _
  private var tempConverter: TempConverter = _

  @Autowired
  private def injectAll(addressRepository: OrderAddressRepository, tempConverter: TempConverter): Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
  }

  @Transactional(readOnly = true)
  override def listAll: util.List[OrderAddressDto] = {
    val allAddresses: util.List[OrderAddressEntity] = addressRepository.findAll
    allAddresses.stream.map[OrderAddressDto](tempConverter.orderAddressEntityToDto).collect(Collectors.toList[OrderAddressDto])
  }
}
