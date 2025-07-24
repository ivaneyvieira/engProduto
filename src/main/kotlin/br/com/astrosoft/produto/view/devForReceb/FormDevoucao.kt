package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.EMotivoDevolucao
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProduto
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormDevoucao(val motivo: EMotivoDevolucao, val produtos: List<NotaRecebimentoProduto>) : FormLayout() {
  private var edtNumero: IntegerField? = null
  private var chkNumero: Checkbox? = null

  init {
    this.width = "460px"
    verticalLayout {
      this.isSpacing = false
      this.isMargin = false
      this.isPadding = false

      verticalBlock {
        this.isSpacing = true
        if (motivo.notasMultiplas) {
          chkNumero = checkBox("Informar o número de outra nota") {
            this.value = false
            addValueChangeListener {
              val value = it.value
              edtNumero?.isVisible = value
              edtNumero?.value = null
              if (value) {
                edtNumero?.focus()
              }
            }
          }
          edtNumero = integerField("Número") {
            this.isVisible = false
            this.width = "6rem"
            this.value = null
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          }
        }

        produtos.forEach { produto ->
          horizontalLayout {
            this.isMargin = false
            this.isPadding = false
            p("${produto.codigo} - ${produto.descricao}") {
              width = "260px"
            }
            p(produto.grade ?: "") {
              width = "80px"
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

  fun numero(): Int? {
    return edtNumero?.value
  }

  fun numeroInformado(): Boolean {
    return chkNumero?.value ?: false
  }
}