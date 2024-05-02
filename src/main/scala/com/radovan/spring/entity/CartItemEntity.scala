package com.radovan.spring.entity

import jakarta.persistence.{Entity, Table, Id, GeneratedValue, GenerationType, Column, ManyToOne, JoinColumn}
import scala.beans.BeanProperty

@Entity
@Table(name = "cart_items")
@SerialVersionUID(1L)
class CartItemEntity extends Serializable{
  
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "id")
  @BeanProperty
  var cartItemId: Integer = _

  @Column(nullable = false)
  @BeanProperty
  var quantity: Integer = _

  @Column(nullable = false)
  @BeanProperty
  var price: Float = _

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  @BeanProperty
  var product: ProductEntity = _

  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false)
  @BeanProperty
  var cart: CartEntity = _
}

