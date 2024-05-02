package com.radovan.spring.service.impl

import com.radovan.spring.converter.TempConverter
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.entity.ShippingAddressEntity
import com.radovan.spring.exceptions.InstanceUndefinedException
import com.radovan.spring.repository.ShippingAddressRepository
import com.radovan.spring.service.ShippingAddressService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util
import java.util.stream.Collectors

@Service
class ShippingAddressServiceImpl extends ShippingAddressService{

  private var addressRepository: ShippingAddressRepository = _
  private var tempConverter: TempConverter = _

  @Autowired
  private def injectAll(addressRepository: ShippingAddressRepository, tempConverter: TempConverter): Unit = {
    this.addressRepository = addressRepository
    this.tempConverter = tempConverter
  }

  @Transactional(readOnly = true)
  override def getAddressById(addressId: Integer): ShippingAddressDto = {
    val addressEntity = addressRepository.findById(addressId).orElseThrow(() => new InstanceUndefinedException(new Error("The address has not been found!")))
    tempConverter.shippingAddressEntityToDto(addressEntity)
  }

  @Transactional
  override def updateAddress(address: ShippingAddressDto, addressId: Integer): ShippingAddressDto = {
    val currentAddress = getAddressById(addressId)
    address.setShippingAddressId(currentAddress.getShippingAddressId)
    address.setCustomerId(currentAddress.getCustomerId)
    val updatedAddress = addressRepository.saveAndFlush(tempConverter.shippingAddressDtoToEntity(address))
    tempConverter.shippingAddressEntityToDto(updatedAddress)
  }

  @Transactional(readOnly = true)
  def listAll: util.List[ShippingAddressDto] = {
    val allAddresses: util.List[ShippingAddressEntity] = addressRepository.findAll
    allAddresses.stream.map[ShippingAddressDto](tempConverter.shippingAddressEntityToDto).collect(Collectors.toList[ShippingAddressDto])
  }
}
