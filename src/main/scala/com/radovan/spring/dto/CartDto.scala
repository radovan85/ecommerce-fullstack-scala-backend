package com.radovan.spring.dto

import java.util

import scala.beans.BeanProperty
import jakarta.validation.constraints.{DecimalMin, NotNull}

@SerialVersionUID(1L)
class CartDto extends Serializable {

  @BeanProperty
  var cartId: Integer = _

  @NotNull
  @BeanProperty
  var customerId: Integer = _

  @BeanProperty
  var cartItemsIds: util.List[Integer] = _

  @NotNull
  @DecimalMin(value = "0.00")
  @BeanProperty
  var cartPrice: Float = _

}

