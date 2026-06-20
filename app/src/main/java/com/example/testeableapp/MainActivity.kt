package com.example.testeableapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.testeableapp.model.MenuData

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: RestaurantViewModel = viewModel()
            val quantities by viewModel.quantities.collectAsState()
            val confirmation by viewModel.confirmation.collectAsState()
            
            val total = quantities.entries.sumOf { (id, qty) -> 
                MenuData.items.find { it.id == id }?.price?.times(qty) ?: 0.0 
            }
            val orderedItems = MenuData.items.filter { (quantities[it.id] ?: 0) > 0 }

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                Text("MENU", style = MaterialTheme.typography.headlineSmall)
                MenuData.items.forEach { item ->
                    Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.name, modifier = Modifier.testTag("menuItemName_${item.id}"))
                        Button(onClick = { viewModel.addItem(item.id) }, modifier = Modifier.testTag("addButton_${item.id}")) { Text("+") }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("ORDER", style = MaterialTheme.typography.headlineSmall)
                if (quantities.isEmpty()) {
                    Text("Empty", modifier = Modifier.testTag("emptyOrderMessage"))
                } else {
                    orderedItems.forEach { item ->
                        val q = quantities[item.id] ?: 0
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(item.name, modifier = Modifier.weight(1f))
                            Button(onClick = { viewModel.incrementItem(item.id) }, modifier = Modifier.testTag("incrementOrderItem_${item.id}")) { Text("+") }
                            Text("$q", modifier = Modifier.testTag("orderItemQuantity_${item.id}"))
                            Text("%.2f".format(q * item.price), modifier = Modifier.testTag("orderItemSubtotal_${item.id}"))
                        }
                    }
                    Text("Total: %.2f".format(total), modifier = Modifier.testTag("totalValue"))
                    Button(onClick = { viewModel.placeOrder() }, modifier = Modifier.testTag("placeOrderButton")) { Text("Confirm") }
                }
            }

            if (confirmation != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissConfirmation() },
                    confirmButton = { Button(onClick = { viewModel.dismissConfirmation() }, modifier = Modifier.testTag("confirmationOkButton")) { Text("OK") } },
                    title = { Text("Done", modifier = Modifier.testTag("confirmationDialog")) }
                )
            }
        }
    }
}
