package com.iceshop.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceshop.data.ProductRepository
import com.iceshop.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Estado da tela de menu
 */
data class MenuUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para a tela de menu
 * Gerencia o estado do cardápio e do carrinho de compras
 */
class MenuViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    // Estado da UI (produtos, loading, erro)
    private val _uiState = MutableStateFlow(MenuUiState(isLoading = true))
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    // Carrinho de compras: lista de (produto, quantidade)
    private val _cartItems = MutableStateFlow<List<Pair<Product, Int>>>(emptyList())
    val cartItems: StateFlow<List<Pair<Product, Int>>> = _cartItems.asStateFlow()

    // Contador de itens no carrinho
    val cartItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.second } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = 0
        )

    /**
     * Inicializa carregando os produtos
     */
    init {
        loadProducts()
    }

    /**
     * Carrega a lista de produtos do banco de dados
     */
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val products = productRepository.getAllProducts()
                _uiState.update {
                    it.copy(
                        products = products,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar produtos"
                    )
                }
            }
        }
    }

    /**
     * Adiciona um produto ao carrinho
     */
    fun addToCart(product: Product) {
        _cartItems.update { currentItems ->
            val existing = currentItems.find { it.first.productId == product.productId }
            if (existing != null) {
                // Aumenta a quantidade se o produto já estiver no carrinho
                currentItems.map {
                    if (it.first.productId == product.productId) {
                        it.copy(second = it.second + 1)
                    } else it
                }
            } else {
                // Adiciona o produto com quantidade 1
                currentItems + (product to 1)
            }
        }
    }

    /**
     * Remove uma unidade de um produto do carrinho
     * Se a quantidade chegar a 0, remove o produto
     */
    fun removeFromCart(product: Product) {
        _cartItems.update { currentItems ->
            currentItems.mapNotNull {
                if (it.first.productId == product.productId) {
                    if (it.second > 1) {
                        // Diminui a quantidade
                        it.copy(second = it.second - 1)
                    } else {
                        // Remove o produto
                        null
                    }
                } else it
            }
        }
    }

    /**
     * Limpa todo o carrinho
     */
    fun clearCart() {
        _cartItems.update { emptyList() }
    }
}
