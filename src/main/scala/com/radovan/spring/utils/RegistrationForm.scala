package com.radovan.spring.utils

import com.radovan.spring.dto.{CustomerDto, ShippingAddressDto, UserDto}

import scala.beans.BeanProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

@SerialVersionUID(1L)
class RegistrationForm extends Serializable {

  @BeanProperty
  @Valid
  @NotNull
  var user: UserDto = _

  @BeanProperty
  @Valid
  @NotNull
  var customer: CustomerDto = _

  @BeanProperty
  @Valid
  @NotNull
  var shippingAddress: ShippingAddressDto = _

}
