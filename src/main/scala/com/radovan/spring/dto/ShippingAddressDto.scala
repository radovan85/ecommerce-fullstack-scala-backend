package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{NotEmpty, Size}

@SerialVersionUID(1L)
class ShippingAddressDto extends Serializable {

  @BeanProperty
  var shippingAddressId: Integer = _

  @BeanProperty
  @NotEmpty
  @Size(max = 75)
  var address: String = _

  @BeanProperty
  @NotEmpty
  @Size(max = 40)
  var city: String = _

  @BeanProperty
  @NotEmpty
  @Size(max = 40)
  var state: String = _

  @BeanProperty
  @NotEmpty
  @Size(max = 40)
  var country: String = _

  @BeanProperty
  @NotEmpty
  @Size(max = 10, min = 5)
  var postcode: String = _

  @BeanProperty
  var customerId: Integer = _

}

