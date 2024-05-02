package com.radovan.spring.service

import java.util
import com.radovan.spring.dto.OrderAddressDto

trait OrderAddressService {

  def listAll: util.List[OrderAddressDto]
}
