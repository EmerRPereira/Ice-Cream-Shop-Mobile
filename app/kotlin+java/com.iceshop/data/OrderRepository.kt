package com.iceshop.data

import com.iceshop.model.Order
import com.iceshop.model.OrderItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Statement

/**
 * Repositório para operações com pedidos no banco de dados
 */
class OrderRepository {

    /**
     * Cria um novo pedido no banco de dados
     * @return ID do pedido criado ou -1 em caso de erro
     */
    suspend fun createOrder(order: Order): Int = withContext(Dispatchers.IO) {
        var orderId = -1
        val connection = DatabaseClient.getConnection()

        try {
            // Inicia transação
            connection?.autoCommit = false

            // 1. Inserir o pedido
            val orderStatement = connection?.prepareStatement(
                "INSERT INTO orders (customer_id, status, total_amount) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            )
            orderStatement?.setInt(1, order.customerId)
            orderStatement?.setString(2, order.status)
            orderStatement?.setDouble(3, order.totalAmount)
            orderStatement?.executeUpdate()

            val generatedKeys = orderStatement?.generatedKeys
            if (generatedKeys?.next() == true) {
                orderId = generatedKeys.getInt(1)
                println("✅ Pedido criado com ID: $orderId")
            }

            // 2. Inserir os itens do pedido
            if (orderId > 0) {
                order.items.forEach { item ->
                    val itemStatement = connection?.prepareStatement(
                        """
                        INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) 
                        VALUES (?, ?, ?, ?, ?)
                        """
                    )
                    itemStatement?.setInt(1, orderId)
                    itemStatement?.setInt(2, item.productId)
                    itemStatement?.setInt(3, item.quantity)
                    itemStatement?.setDouble(4, item.unitPrice)
                    itemStatement?.setDouble(5, item.subtotal)
                    itemStatement?.executeUpdate()
                }
                println("✅ ${order.items.size} itens adicionados ao pedido")
            }

            // Commit da transação
            connection?.commit()
            println("✅ Pedido finalizado com sucesso!")

        } catch (e: Exception) {
            println("❌ Erro ao criar pedido: ${e.message}")
            e.printStackTrace()
            // Rollback em caso de erro
            try {
                connection?.rollback()
            } catch (rollbackError: Exception) {
                rollbackError.printStackTrace()
            }
            orderId = -1
        } finally {
            connection?.autoCommit = true
            DatabaseClient.closeConnection()
        }
        orderId
    }

    /**
     * Busca o histórico de pedidos de um cliente
     */
    suspend fun getOrdersByCustomer(customerId: Int): List<Order> = withContext(Dispatchers.IO) {
        val ordersMap = mutableMapOf<Int, MutableList<OrderItem>>()
        val orderHeaders = mutableMapOf<Int, Pair<String, String>>()

        val connection = DatabaseClient.getConnection()

        try {
            val statement = connection?.prepareStatement(
                """
                SELECT 
                    o.order_id, 
                    o.order_date, 
                    o.status, 
                    o.total_amount,
                    oi.product_id, 
                    oi.quantity, 
                    oi.unit_price, 
                    oi.subtotal,
                    p.name as product_name
                FROM orders o
                LEFT JOIN order_items oi ON o.order_id = oi.order_id
                LEFT JOIN products p ON oi.product_id = p.product_id
                WHERE o.customer_id = ?
                ORDER BY o.order_date DESC, o.order_id DESC
                """
            )
            statement?.setInt(1, customerId)
            val resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                val currentOrderId = resultSet.getInt("order_id")

                // Salva o cabeçalho do pedido
                if (!orderHeaders.containsKey(currentOrderId)) {
                    orderHeaders[currentOrderId] = Pair(
                        resultSet.getString("status") ?: "Preparing",
                        resultSet.getString("order_date") ?: ""
                    )
                }

                // Salva os itens
                if (resultSet.getInt("product_id") > 0) {
                    val item = OrderItem(
                        productId = resultSet.getInt("product_id"),
                        productName = resultSet.getString("product_name") ?: "Produto",
                        quantity = resultSet.getInt("quantity"),
                        unitPrice = resultSet.getDouble("unit_price"),
                        subtotal = resultSet.getDouble("subtotal")
                    )
                    ordersMap.getOrPut(currentOrderId) { mutableListOf() }.add(item)
                }
            }

            println("✅ ${ordersMap.size} pedidos carregados do cliente ID: $customerId")

        } catch (e: Exception) {
            println("❌ Erro ao buscar pedidos: ${e.message}")
            e.printStackTrace()
        } finally {
            DatabaseClient.closeConnection()
        }

        // Converter para lista de Orders
        ordersMap.map { (orderId, items) ->
            val header = orderHeaders[orderId] ?: Pair("Preparing", "")
            Order(
                orderId = orderId,
                customerId = customerId,
                status = header.first,
                totalAmount = items.sumOf { it.subtotal },
                items = items
            )
        }
    }
}
