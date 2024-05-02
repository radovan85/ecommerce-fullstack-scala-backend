package com.radovan.spring.repository

import com.radovan.spring.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait ProductRepository extends JpaRepository[ProductEntity, Integer]{

}
