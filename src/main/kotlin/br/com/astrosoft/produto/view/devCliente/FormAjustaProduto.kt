package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.AjusteProduto
import br.com.astrosoft.produto.model.beans.EntradaDevCli
import com.github.mvysny.karibudsl.v10.checkBox
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.formlayout.FormLayout

class FormAjustaProduto(val nota: EntradaDevCli) : FormLayout() {
  private val listCheck: MutableList<Checkbox> = mutableListOf()

  init {
    verticalLayout {
      nota.produtos().forEach { produto ->
        horizontalLayout {
          textField("Código") {
            this.isReadOnly = true
            this.tabIndex = 0
            this.value = produto.codigo
          }

          textField("Descrição") {
            this.isReadOnly = true
            this.tabIndex = 0
            this.value = produto.descricao
          }

          textField("Grade") {
            this.isReadOnly = true
            this.tabIndex = 0
            this.value = produto.grade
          }

          val check = checkBox("Tem Produto") {
            this.style.set("prdno", produto.prdno)
            this.style.set("grade", produto.grade)
            this.value = produto.tipoPrd?.startsWith("TROCA P") ?: false
          }

          listCheck.add(check)
        }
      }
    }
  }

  fun listAjustes(): List<AjusteProduto> {
    return listCheck.map { check ->
      AjusteProduto(
        prdno = check.style.get("prdno"),
        grade = check.style.get("grade"),
        temProduto = check.value ?: false
      )
    }
  }
}
