package com.radovan.spring.dto

import java.util
import scala.beans.BeanProperty
import jakarta.validation.constraints.NotNull

@SerialVersionUID(1L)
class OrderDto extends Serializable {

  @BeanProperty
  var orderId: Integer = _

  @NotNull
  @BeanProperty
  var orderPrice: Float = _

  @NotNull
  @BeanProperty
  var cartId: Integer = _

  @BeanProperty
  var orderedItemsIds: util.List[Integer] = _

  @NotNull
  @BeanProperty
  var addressId: Integer = _

  @BeanProperty
  var createdAt: String = _

}

