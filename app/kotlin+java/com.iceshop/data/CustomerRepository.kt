package com.iceshop.data

import com.iceshop.model.Customer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Statement

/**
 * Repositório para operações com clientes no banco de dados
 */
class CustomerRepository {

    /**
     * Busca um cliente por telefone ou cria um novo
     */
    suspend fun getOrCreateCustomer(name: String, phone: String?): Customer = withContext(Dispatchers.IO) {
        var customer: Customer? = null
        val connection = DatabaseClient.getConnection()

        try {
            // Primeiro, tenta buscar o cliente existente
            val checkStmt = connection?.prepareStatement(
                "SELECT customer_id, name, phone, email, address FROM customers WHERE phone = ? OR (name = ? AND phone IS NULL)"
            )
            checkStmt?.setString(1, phone ?: "")
            checkStmt?.setString(2, name)
            val resultSet = checkStmt?.executeQuery()

            if (resultSet?.next() == true) {
                // Cliente encontrado
                customer = Customer(
                    customerId = resultSet.getInt("customer_id"),
                    name = resultSet.getString("name"),
                    phone = resultSet.getString("phone"),
                    email = resultSet.getString("email"),
                    address = resultSet.getString("address")
                )
                println("✅ Cliente encontrado: ${customer.name}")
            } else {
                // Cliente não encontrado - criar novo
                val insertStmt = connection?.prepareStatement(
                    "INSERT INTO customers (name, phone) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                )
                insertStmt?.setString(1, name)
                insertStmt?.setString(2, phone)
                insertStmt?.executeUpdate()

                val generatedKeys = insertStmt?.generatedKeys
                if (generatedKeys?.next() == true) {
                    customer = Customer(
                        customerId = generatedKeys.getInt(1),
                        name = name,
                        phone = phone,
                        email = null,
                        address = null
                    )
                    println("✅ Novo cliente criado: ${customer.name}")
                }
            }
        } catch (e: Exception) {
            println("❌ Erro ao buscar/criar cliente: ${e.message}")
            e.printStackTrace()
        } finally {
            DatabaseClient.closeConnection()
        }

        customer ?: Customer(0, name, phone, null, null)
    }
}
