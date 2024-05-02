package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{NotEmpty, NotNull, Size}

@SerialVersionUID(1L)
class OrderAddressDto extends Serializable {

  @BeanProperty
  var orderAddressId: Integer = _

  @NotEmpty
  @Size(max = 75)
  @BeanProperty
  var address: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty
  var city: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty
  var state: String = _

  @NotEmpty
  @Size(max = 40)
  @BeanProperty
  var country: String = _

  @NotEmpty
  @Size(max = 10)
  @BeanProperty
  var postcode: String = _

  @NotNull
  @BeanProperty
  var orderId: Integer = _

}

