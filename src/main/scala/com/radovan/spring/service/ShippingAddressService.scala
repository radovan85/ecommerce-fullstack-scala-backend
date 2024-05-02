package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.ShippingAddressDto

trait ShippingAddressService {

  def getAddressById(addressId: Integer): ShippingAddressDto

  def updateAddress(address: ShippingAddressDto, addressId: Integer): ShippingAddressDto

  def listAll: util.List[ShippingAddressDto]
}
