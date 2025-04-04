package br.com.astrosoft.produto.view.recebimento

import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.p
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormDevoucao(val produtos: List<NotaRecebimentoProduto>) : FormLayout() {
  init {
    this.width = "420px"
    verticalLayout {
      this.isSpacing = false
      this.isMargin = false
      this.isPadding = false

      produtos.forEach { produto ->
        horizontalLayout {
          this.isMargin = false
          this.isPadding = false
          p("${produto.codigo} - ${produto.descricao}") {
            width = "300px"
          }
          integerField {
            width = "60px"
            value = produto.quant
            addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.isAutoselect = true
            produto.quantDevolucao = produto.quant
            addValueChangeListener {
              produto.quantDevolucao = this.value ?: 0
            }
          }
        }
      }
    }
  }
}