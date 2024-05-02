package com.radovan.spring.dto

import scala.beans.BeanProperty
import jakarta.validation.constraints.{Min, NotNull}

@SerialVersionUID(1L)
class CartItemDto extends Serializable {

  @BeanProperty
  var cartItemId: Integer = _

  @NotNull
  @Min(value = 1)
  @BeanProperty
  var quantity: Integer = _

  @BeanProperty
  var price: Float = _

  @NotNull
  @BeanProperty
  var productId: Integer = _

  @NotNull
  @BeanProperty
  var cartId: Integer = _

}
