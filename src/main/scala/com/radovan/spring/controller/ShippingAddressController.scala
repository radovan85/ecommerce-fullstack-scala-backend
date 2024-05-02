package com.radovan.spring.controller

import java.util
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.radovan.spring.dto.ShippingAddressDto
import com.radovan.spring.service.ShippingAddressService


@RestController
@RequestMapping(value = Array("/api/addresses"))
class ShippingAddressController {

  @Autowired
  private var addressService: ShippingAddressService = _

  @PreAuthorize(value = "hasAuthority('ADMIN')")
  @GetMapping(value = Array("/allAddresses"))
  def getAllAddresses: ResponseEntity[util.List[ShippingAddressDto]] = {
    val addresses = addressService.listAll
    new ResponseEntity(addresses, HttpStatus.OK)
  }
}
