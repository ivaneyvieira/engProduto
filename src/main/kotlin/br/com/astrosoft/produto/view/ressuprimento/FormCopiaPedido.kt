package br.com.astrosoft.produto.view.ressuprimento

import br.com.astrosoft.produto.model.beans.BeanCopia
import com.github.mvysny.karibudsl.v10.integerField
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField

class FormCopiaPedido() : FormLayout() {
  private var edtLojaOrigem: IntegerField? = null
  private var edtPedidoOrigem: IntegerField? = null
  private var edtLojaDestino: IntegerField? = null
  private var edtPedidoDestino: IntegerField? = null

  init {
    setResponsiveSteps(ResponsiveStep("0", 3))
    edtLojaOrigem = integerField("Loja Origem") {
      this.isClearButtonVisible = true
      this.isAutofocus = true
    }
    edtPedidoOrigem = integerField("Pedido Origem") {
      this.isClearButtonVisible = true
      setColspan(this, 2)
    }
    edtLojaDestino = integerField("Loja Destino") {
      this.isClearButtonVisible = true
    }
    edtPedidoDestino = integerField("Pedido Destino") {
      this.isClearButtonVisible = true
      setColspan(this, 2)
    }
  }

  fun getBeanCopia(): BeanCopia? {
    val lojaOrigem = edtLojaOrigem?.value ?: return null
    val pedidoOrigem = edtPedidoOrigem?.value ?: return null
    val lojaDestino = edtLojaDestino?.value ?: return null
    val pedidoDestino = edtPedidoDestino?.value ?: return null
    return BeanCopia(lojaOrigem, pedidoOrigem, lojaDestino, pedidoDestino)
  }
}