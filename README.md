# Ice Cream Shop - Mobile App Integration

## Overview

As a software engineer, I developed this mobile application to extend my understanding of full-stack development by integrating a Kotlin-based Android app with a PostgreSQL database. This project represents the culmination of my learning journey, demonstrating how to build a complete end-to-end system that connects a modern mobile frontend with a robust relational database backend.

The software is an **Android Mobile Application** for the Ice Cream Shop Management System that allows customers to browse products, manage a shopping cart, place orders, and view order history directly from their mobile devices. The app communicates with a PostgreSQL database via JDBC, creating a seamless experience that combines the power of SQL with the interactivity of mobile computing.

**Purpose:** To master mobile application development with Kotlin and Jetpack Compose while integrating with a PostgreSQL database, implementing professional mobile patterns like MVVM architecture, reactive state management with StateFlow, and direct JDBC database connectivity.

---

## Key Features

- 📱 **Modern Android UI** built with Jetpack Compose and Material Design 3
- 🍦 **Product Catalog** - Browse available ice cream products from the database
- 🛒 **Shopping Cart** - Add/remove products, adjust quantities, calculate totals
- ✅ **Order Processing** - Place orders with customer information
- 📋 **Order History** - View past orders with status tracking
- 🔗 **Database Integration** - Direct JDBC connection to PostgreSQL
- 🔄 **Reactive State Management** with Kotlin StateFlow
- 📊 **MVVM Architecture** - Clean separation of concerns


## Development Environment

### Tools
- **Android Studio** - Primary IDE for Android development
- **PostgreSQL 18** - Relational database management system
- **pgAdmin 4** - Database administration tool
- **Git & GitHub** - Version control
- **Gradle** - Build automation tool
- **Android Emulator** - Testing environment (API 34 / Android 14)

### Programming Languages & Frameworks
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern declarative UI framework
- **PostgreSQL JDBC Driver** - Database connectivity (42.7.3)
- **Kotlin Coroutines** - Asynchronous operations
- **Material Design 3** - UI components and theming

### Key Dependencies
```kotlin
// Jetpack Compose
implementation(platform("androidx.compose:compose-bom:2024.10.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// ViewModel & State Management
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// PostgreSQL JDBC
implementation("org.postgresql:postgresql:42.7.3")
```

---

## Project Structure

```
app/src/main/java/com/iceshop/
├── MainActivity.kt                 # Application entry point
├── data/
│   ├── DatabaseClient.kt           # JDBC connection management
│   ├── CustomerRepository.kt       # Customer CRUD operations
│   ├── ProductRepository.kt        # Product CRUD operations
│   └── OrderRepository.kt          # Order CRUD operations with transactions
├── model/
│   ├── Customer.kt                 # Customer data class
│   ├── Product.kt                  # Product data class
│   ├── Order.kt                    # Order data class
│   └── OrderItem.kt                # OrderItem data class
├── ui/
│   ├── theme/
│   │   └── Theme.kt                # Material Design 3 theming
│   ├── navigation/
│   │   └── NavGraph.kt             # Screen navigation configuration
│   ├── screens/
│   │   ├── MenuScreen.kt           # Product catalog screen
│   │   ├── CartScreen.kt           # Shopping cart screen
│   │   ├── OrderHistoryScreen.kt   # Order history screen
│   │   └── OrderSuccessScreen.kt   # Order confirmation screen
│   └── components/
│       ├── ProductCard.kt          # Reusable product card component
│       └── CartItemRow.kt          # Reusable cart item component
└── viewmodels/
    └── MenuViewModel.kt            # Menu and cart state management
```

---

## Database Schema

The application uses a normalized PostgreSQL database with four main tables:

```sql
-- Customers table
CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT
);

-- Products table
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE
);

-- Orders table
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES customers(customer_id),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Preparing',
    total_amount DECIMAL(10,2)
);

-- Order items table
CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INTEGER REFERENCES orders(order_id),
    product_id INTEGER REFERENCES products(product_id),
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10,2),
    subtotal DECIMAL(10,2)
);
```

---

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        ANDROID APP (Kotlin)                     │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │               UI LAYER (Jetpack Compose)                │   │
│  │  ┌──────────┐ ┌──────────┐ ┌───────────────────────┐  │   │
│  │  │MenuScreen│ │CartScreen│ │OrderSuccessScreen     │  │   │
│  │  └────┬─────┘ └────┬─────┘ └───────────────────────┘  │   │
│  │       │           │                                    │   │
│  │       ▼           ▼                                    │   │
│  │  ┌───────────────────────────────────────────────┐    │   │
│  │  │         ViewModels (StateFlow)                │    │   │
│  │  └───────────────────────────────────────────────┘    │   │
│  │       │           │                                    │   │
│  │       ▼           ▼                                    │   │
│  │  ┌───────────────────────────────────────────────┐    │   │
│  │  │         Repositories (Coroutines)             │    │   │
│  │  │  ProductRepository  OrderRepository  Customer │    │   │
│  │  └───────────────────────────────────────────────┘    │   │
│  └─────────────────────────┬──────────────────────────────┘   │
│                            │                                   │
│                            ▼                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │           DatabaseClient (JDBC Connection)             │   │
│  │            (PostgreSQL Driver - JDBC)                  │   │
│  └─────────────────────────┬──────────────────────────────┘   │
└────────────────────────────┼────────────────────────────────────┘
                             │
                             ▼ (Network)
┌─────────────────────────────────────────────────────────────────┐
│                      POSTGRESQL DATABASE                        │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────────┐   │
│  │  customers  │  │   products  │  │       orders          │   │
│  └─────────────┘  └─────────────┘  └───────────┬───────────┘   │
│                                                │                │
│                                                ▼                │
│                                   ┌────────────────────────────┐│
│                                   │      order_items           ││
│                                   └────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

---

## Setup Instructions

### 1. Prerequisites
- Android Studio installed (latest version)
- PostgreSQL 18+ installed and running
- JDK 17 or 21 configured
- Android emulator or physical device (API 21+)

### 2. Database Setup

Execute the database schema on your PostgreSQL server:

```bash
psql -U postgres -f sql/create_database.sql
```

Or manually create and populate the database using the SQL scripts provided in the `sql/` folder.

### 3. Configure Database Connection

Open `app/src/main/java/com/iceshop/data/DatabaseClient.kt` and update:

```kotlin
private const val DB_HOST = "10.0.2.2"  // For Android emulator
// private const val DB_HOST = "192.168.1.X"  // For physical device (your IP)
private const val DB_USER = "postgres"
private const val DB_PASSWORD = "your_password"  // Update with your password
```

**Important:** 
- **Emulator:** Use `10.0.2.2` to connect to the host machine's localhost
- **Physical device:** Use your computer's actual IP address

### 4. Run the Application

1. Open the project in Android Studio
2. Sync Gradle files
3. Start an Android emulator or connect a physical device
4. Click **Run** (▶)

---

## Database Integration Details

The app connects directly to PostgreSQL using JDBC with the following features:

### Connection Management
- Singleton `DatabaseClient` object manages the connection lifecycle
- Connection pooling for efficient database access
- Automatic reconnection on connection loss

### Repository Pattern
- `ProductRepository`: Fetches available products
- `CustomerRepository`: Manages customer data (get or create)
- `OrderRepository`: Handles order creation and history retrieval

### Transaction Support
- Orders are created within **database transactions**
- If any part fails, the entire transaction is **rolled back**
- Ensures data consistency across tables

### Async Operations
- All database operations use Kotlin **Coroutines**
- Executed on `Dispatchers.IO` background threads
- UI remains responsive during database operations

### Sample Code - Product Repository
```kotlin
class ProductRepository {
    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        val products = mutableListOf<Product>()
        val connection = DatabaseClient.getConnection()
        
        try {
            val statement = connection?.prepareStatement(
                "SELECT * FROM products WHERE is_available = true ORDER BY name"
            )
            val resultSet = statement?.executeQuery()
            while (resultSet?.next() == true) {
                products.add(
                    Product(
                        productId = resultSet.getInt("product_id"),
                        name = resultSet.getString("name"),
                        description = resultSet.getString("description"),
                        price = resultSet.getDouble("price"),
                        isAvailable = resultSet.getBoolean("is_available")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            DatabaseClient.closeConnection()
        }
        products
    }
}
```

---

## Mobile Architecture Highlights

### MVVM Pattern
- **Model:** Data classes and repositories
- **View:** Composable functions displaying UI
- **ViewModel:** Manages state and business logic

### State Management with StateFlow
```kotlin
class MenuViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState(isLoading = true))
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()
    
    private val _cartItems = MutableStateFlow<List<Pair<Product, Int>>>(emptyList())
    val cartItems: StateFlow<List<Pair<Product, Int>>> = _cartItems.asStateFlow()
    
    val cartItemCount: StateFlow<Int> = _cartItems
        .map { items -> items.sumOf { it.second } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)
}
```

### Navigation
- `NavHost` with `NavController` for screen navigation
- `Screen` sealed class for route management
- Composable navigation destinations

---

## Testing

### Manual Testing
1. Launch the app on an emulator or physical device
2. **Menu Screen:** Verify products load from the database
3. **Add to Cart:** Click "Add" button on any product
4. **Cart Screen:** Verify items appear with correct quantities
5. **Checkout:** Enter customer name and place order
6. **Order History:** Verify order appears with correct status

### Database Verification
After placing an order, verify in PostgreSQL:
```sql
SELECT * FROM orders ORDER BY order_id DESC;
SELECT * FROM order_items WHERE order_id = (SELECT MAX(order_id) FROM orders);
SELECT * FROM customers ORDER BY customer_id DESC;
```

---

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| **Connection refused** | Check PostgreSQL is running; verify DB_HOST setting |
| **NetworkOnMainThreadException** | All DB operations use Coroutines with Dispatchers.IO |
| **ClassNotFoundException (JDBC)** | Verify PostgreSQL JDBC driver dependency in build.gradle.kts |
| **Permission denied** | Add `<uses-permission android:name="android.permission.INTERNET" />` to AndroidManifest.xml |
| **Emulator can't connect** | Use `10.0.2.2` as DB_HOST for Android emulator |
| **Physical device can't connect** | Use your computer's actual IP address; check firewall settings |

---

## Useful Websites

* [Android Developers - Jetpack Compose](https://developer.android.com/jetpack/compose) - Official Compose documentation
* [Android Developers - Kotlin](https://developer.android.com/kotlin) - Kotlin for Android development
* [PostgreSQL Documentation](https://www.postgresql.org/docs/) - PostgreSQL reference guide
* [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/documentation/head/) - JDBC driver documentation
* [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) - Asynchronous programming guide
* [Material Design 3](https://m3.material.io/) - Design system and components
* [W3Schools SQL Tutorial](https://www.w3schools.com/sql/) - SQL reference and tutorials
* [Stack Overflow - PostgreSQL & Kotlin](https://stackoverflow.com/questions/tagged/postgresql+kotlin) - Community support

---

## Future Improvements

- 🔐 Implement user authentication and login
- 📱 Add push notifications for order status updates
- 🎨 Offline mode with local Room database caching
- 🌐 Replace JDBC with REST API for better security
- 📊 Analytics dashboard for store owners
- 💳 Integration with payment gateways
- 🌍 Multi-language support

---

**Author:** Emerson Ronald Pereira