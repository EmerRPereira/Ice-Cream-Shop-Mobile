package com.iceshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.iceshop.data.CustomerRepository
import com.iceshop.data.OrderRepository
import com.iceshop.model.Order
import com.iceshop.model.OrderItem
import com.iceshop.ui.components.CartItemRow
import com.iceshop.ui.viewmodels.MenuViewModel
import com.iceshop.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: MenuViewModel = viewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val scope = rememberCoroutineScope()

    var isPlacingOrder by remember { mutableStateOf(false) }
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Seu Carrinho") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Text("← Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒 Carrinho vazio", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { navController.navigateUp() }) {
                        Text("Continuar comprando")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(cartItems) { (product, quantity) ->
                    CartItemRow(
                        productName = product.name,
                        price = product.price,
                        quantity = quantity,
                        onRemove = { viewModel.removeFromCart(product) },
                        onAdd = { viewModel.addToCart(product) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    val total = cartItems.sumOf { it.first.price * it.second }

                    Text(
                        text = "Total: $${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dados do cliente
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Seu nome") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text("Telefone (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (customerName.isNotBlank()) {
                                isPlacingOrder = true
                                scope.launch {
                                    try {
                                        val customerRepo = CustomerRepository()
                                        val orderRepo = OrderRepository()

                                        // Busca ou cria o cliente
                                        val customer = customerRepo.getOrCreateCustomer(
                                            name = customerName,
                                            phone = customerPhone.ifBlank { null }
                                        )

                                        // Cria os itens do pedido
                                        val orderItems = cartItems.map { (product, quantity) ->
                                            OrderItem(
                                                productId = product.productId,
                                                productName = product.name,
                                                quantity = quantity,
                                                unitPrice = product.price,
                                                subtotal = product.price * quantity
                                            )
                                        }

                                        val totalAmount = orderItems.sumOf { it.subtotal }

                                        val order = Order(
                                            customerId = customer.customerId,
                                            status = "Preparing",
                                            totalAmount = totalAmount,
                                            items = orderItems
                                        )

                                        val orderId = orderRepo.createOrder(order)

                                        if (orderId > 0) {
                                            viewModel.clearCart()
                                            navController.navigate(Screen.OrderSuccess.route) {
                                                popUpTo(Screen.Cart.route) { inclusive = true }
                                            }
                                        } else {
                                            // Mostrar erro
                                        }
                                    } catch (e: Exception) {
                                        // Mostrar erro
                                    } finally {
                                        isPlacingOrder = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = customerName.isNotBlank() && !isPlacingOrder
                    ) {
                        if (isPlacingOrder) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        } else {
                            Text("✅ Finalizar Pedido")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { viewModel.clearCart() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🗑️ Limpar carrinho")
                    }
                }
            }
        }
    }
}
