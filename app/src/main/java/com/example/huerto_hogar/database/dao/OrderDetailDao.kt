package com.example.huerto_hogar.database.dao

import androidx.room.*
import com.example.huerto_hogar.database.entities.OrderDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDetailDao {
    
    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    suspend fun getDetailsByOrderId(orderId: Long): List<OrderDetailEntity>
    
    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    fun getDetailsByOrderIdFlow(orderId: Long): Flow<List<OrderDetailEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetail(orderDetail: OrderDetailEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderDetails(orderDetails: List<OrderDetailEntity>)
    
    @Update
    suspend fun updateOrderDetail(orderDetail: OrderDetailEntity)
    
    @Delete
    suspend fun deleteOrderDetail(orderDetail: OrderDetailEntity)
    
    @Query("DELETE FROM order_details WHERE orderId = :orderId")
    suspend fun deleteDetailsByOrderId(orderId: Long)
}