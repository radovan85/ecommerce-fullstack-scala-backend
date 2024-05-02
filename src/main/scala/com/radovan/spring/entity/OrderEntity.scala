package com.radovan.spring.entity

import java.util
import java.sql.Timestamp
import jakarta.persistence.{Column, Entity, FetchType, GeneratedValue, GenerationType, Id, JoinColumn, ManyToOne, OneToMany, OneToOne, Table}

import scala.beans.BeanProperty
import org.hibernate.annotations.{Fetch, FetchMode}

@Entity
@Table(name = "orders")
@SerialVersionUID(1L)
class OrderEntity extends Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  @BeanProperty
  var orderId: Integer = _

  @Column(name = "order_price", nullable = false)
  @BeanProperty
  var orderPrice: Float = _

  @ManyToOne
  @JoinColumn(name = "cart_id", nullable = false)
  @BeanProperty
  var cart: CartEntity = _

  @Column(name = "created_at", nullable = false)
  @BeanProperty
  var createdAt: Timestamp = _

  @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, orphanRemoval = true)
  @Fetch(value = FetchMode.SUBSELECT)
  @BeanProperty
  var orderedItems: util.List[OrderItemEntity] = _

  @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id", nullable = false)
  @BeanProperty
  var address: OrderAddressEntity = _
}

