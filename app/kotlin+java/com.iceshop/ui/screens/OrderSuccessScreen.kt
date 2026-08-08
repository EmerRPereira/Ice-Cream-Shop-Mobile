package com.iceshop.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iceshop.ui.navigation.Screen
import androidx.navigation.NavController

@Composable
fun OrderSuccessScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 80.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pedido Realizado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Seu sorvete está sendo preparado 🍦",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate(Screen.Menu.route) {
                    popUpTo(Screen.Menu.route) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🍦 Voltar ao Cardápio")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = {
                navController.navigate(Screen.OrderHistory.route)
            }
        ) {
            Text("📋 Ver Histórico de Pedidos")
        }
    }
}
