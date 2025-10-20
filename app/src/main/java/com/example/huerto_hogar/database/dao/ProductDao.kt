package com.example.huerto_hogar.database.dao

import androidx.room.*
import com.example.huerto_hogar.database.entities.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    
    @Query("SELECT * FROM products WHERE isActive = 1")
    fun getActiveProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?
    
    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1")
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE isOrganic = 1 AND isActive = 1")
    fun getOrganicProducts(): Flow<List<ProductEntity>>
    
    @Query("SELECT * FROM products WHERE stock <= :threshold AND isActive = 1")
    fun getLowStockProducts(threshold: Int = 5): Flow<List<ProductEntity>>
    
    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateProductStock(productId: String, newStock: Int)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)
    
    @Update
    suspend fun updateProduct(product: ProductEntity)
    
    @Delete
    suspend fun deleteProduct(product: ProductEntity)
    
    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: String)
}