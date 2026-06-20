package com.example.testeableapp

import androidx.lifecycle.ViewModel
import com.example.testeableapp.model.MenuData
import com.example.testeableapp.model.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class OrderConfirmation(
    val itemCount: Int,
    val total: Double
)

class RestaurantViewModel : ViewModel() {

    private val _quantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val quantities: StateFlow<Map<Int, Int>> = _quantities.asStateFlow()

    private val _confirmation = MutableStateFlow<OrderConfirmation?>(null)
    val confirmation: StateFlow<OrderConfirmation?> = _confirmation.asStateFlow()

    // Derived states simplified to direct properties for easier testing/UI binding
    val orderedItems: List<MenuItem>
        get() = MenuData.items.filter { (_quantities.value[it.id] ?: 0) > 0 }

    val total: Double
        get() = _quantities.value.entries.sumOf { (id, qty) -> 
            (MenuData.items.find { it.id == id }?.price ?: 0.0) * qty 
        }

    val isEmpty: Boolean
        get() = _quantities.value.isEmpty()

    fun addItem(itemId: Int) {
        _quantities.update { current ->
            current + (itemId to ((current[itemId] ?: 0) + 1))
        }
    }

    fun incrementItem(itemId: Int) {
        addItem(itemId)
    }

    fun decrementItem(itemId: Int) {
        _quantities.update { current ->
            val currentQty = current[itemId] ?: return@update current
            if (currentQty <= 1) current - itemId
            else current + (itemId to (currentQty - 1))
        }
    }

    fun placeOrder() {
        val q = _quantities.value
        if (q.isEmpty()) return
        
        val totalValue = total
        val count = q.values.sum()
        
        _confirmation.value = OrderConfirmation(
            itemCount = count,
            total = totalValue
        )
    }

    fun dismissConfirmation() {
        _confirmation.value = null
        _quantities.value = emptyMap()
    }
}
