package com.example.testeableapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.testeableapp.model.MenuData
import org.junit.Rule
import org.junit.Test

class RestaurantOrderTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    // 1. Mensaje de pedido vacío visible al inicio //
    @Test
    fun testEmptyOrderMessageVisibleAtStart() {
        composeTestRule.onNodeWithTag("emptyOrderMessage", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    // 2. Todos los items del menú visibles //
    @Test
    fun testAllMenuItemsVisible() {
        MenuData.items.forEach { item ->
            // Try to scroll to it if the list is long
            composeTestRule.onNodeWithTag("menuItemName_${item.id}", useUnmergedTree = true)
                .assertExists()
                .assertIsDisplayed()
                .assertTextEquals(item.name)
        }
    }

    // 3. El total general se actualiza //
    @Test
    fun testTotalUpdatesOnAddItem() {
        val item = MenuData.items.first()

        composeTestRule.onNodeWithTag("addButton_${item.id}", useUnmergedTree = true)
            .performClick()

        composeTestRule.waitForIdle()

        val expectedPricePart = "%.2f".format(item.price)
        composeTestRule.onNodeWithTag("totalValue", useUnmergedTree = true)
            .assertIsDisplayed()
            .assertTextContains(expectedPricePart)
    }

    // Pruebas adicionales de la UI //

    // 4. Incrementar cantidad de un item directamente en el pedido //
    // Justificación del test: Es relevante verificar que el usuario puede ajustar cantidades directamente en el resumen del pedido y que el subtotal por ítem se actualice correctamente. //
    @Test
    fun testIncrementItemQuantityInOrder() {
        val item = MenuData.items.first()
        
        // Añadir un producto //
        composeTestRule.onNodeWithTag("addButton_${item.id}", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()
            
        // Incrementar //
        composeTestRule.onNodeWithTag("incrementOrderItem_${item.id}", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()
            
        // Verificar la cantidad, en este caso 2 //
        composeTestRule.onNodeWithTag("orderItemQuantity_${item.id}", useUnmergedTree = true)
            .assertTextEquals("2")
            
        val expectedSubtotal = "%.2f".format(item.price * 2)
        composeTestRule.onNodeWithTag("orderItemSubtotal_${item.id}", useUnmergedTree = true)
            .assertTextContains(expectedSubtotal)
    }

    // 5. Flujo de confirmación de pedido y reset del estado //
    // Justificación del test: Es crítico asegurar que el proceso de pedido sea de extremo a extremo, validando el diálogo de confirmación y el reinicio automático del estado de la app.
    @Test
    fun testOrderConfirmationFlow() {
        val item = MenuData.items.first()
        
        // Añadir un item //
        composeTestRule.onNodeWithTag("addButton_${item.id}", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
        
        // Confirma el producto //
        composeTestRule.onNodeWithTag("placeOrderButton", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
            
        // Aparece un cuadro de diálogo exitoso //
        composeTestRule.onNodeWithTag("confirmationDialog", useUnmergedTree = true).assertIsDisplayed()
        
        // Presion "OK"
        composeTestRule.onNodeWithTag("confirmationOkButton", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()
            
        // Se vacía el carrito de items //
        composeTestRule.onNodeWithTag("emptyOrderMessage", useUnmergedTree = true).assertIsDisplayed()
    }
}
