package com.example.testeableapp

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RestaurantViewModelTest {

    private lateinit var viewModel: RestaurantViewModel

    @Before
    fun setup() {
        viewModel = RestaurantViewModel()
    }

    // 1. Agregar item al pedido //
    @Test
    fun testAddItem() {
        viewModel.addItem(1)
        assertEquals(1, viewModel.quantities.value[1])
        assertFalse(viewModel.isEmpty)
    }


    // 2. Incrementar/Decrementar cantidad //
    @Test
    fun testIncrementDecrementQuantity() {
        viewModel.addItem(2)
        viewModel.incrementItem(2)
        assertEquals(2, viewModel.quantities.value[2])
        
        viewModel.decrementItem(2)
        assertEquals(1, viewModel.quantities.value[2])
    }

    // 3. Eliminar item al decrementar desde 1 //
    @Test
    fun testRemoveItemOnDecrementFromOne() {
        viewModel.addItem(3)
        viewModel.decrementItem(3)
        assertNull(viewModel.quantities.value[3])
        assertTrue(viewModel.isEmpty)
    }

    // 4. Cálculo del total a pagar //
    @Test
    fun testCalculateTotal() {
        viewModel.addItem(1) // 5.50
        viewModel.addItem(5) // 1.50
        viewModel.incrementItem(5) // Total: 5.50 + 1.50 * 2 = 8.50
        
        assertEquals(8.50, viewModel.total, 0.001)
    }

    // Pruebas unitarias adicionales //

    // 5. El estado se resetea al descartar la confirmación //
    // Justificación del test: Es fundamental garantizar que tras completar un pedido y cerrar la confirmación, la aplicación limpie todos los datos previos para permitir un nuevo pedido sin errores. //
    @Test
    fun testDismissConfirmationResetsState() {
        viewModel.addItem(1)
        viewModel.placeOrder()
        viewModel.dismissConfirmation()
        
        assertTrue(viewModel.quantities.value.isEmpty())
        assertNull(viewModel.confirmation.value)
    }

    // 6. Robustez al decrementar un ítem que no existe //
    // Justificación: Valida la robustez del código al manejar casos de borde o errores de usuario, asegurando que la aplicación no intente procesar datos inexistentes ni corrompa el estado. //
    @Test
    fun testDecrementNonExistentItem() {
        viewModel.decrementItem(99)
        assertTrue(viewModel.quantities.value.isEmpty())
    }
}
