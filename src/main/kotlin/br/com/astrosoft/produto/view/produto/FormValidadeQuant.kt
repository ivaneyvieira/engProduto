package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.view.vaadin.helper.mesAnoFieldComponente
import br.com.astrosoft.produto.model.beans.Produtos
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.textfield.IntegerField

class FormValidadeQuant(val produto: Produtos) : FormLayout() {
  init {
    verticalLayout {
      horizontalLayout {
        integerFieldEditor("Qtd 1") {
          this.value = produto.qtty01
          addValueChangeListener {
            produto.qtty01 = it.value
          }
        }

        mesAnoFieldEditor("Venc 1") {
          this.value = produto.venc01
          addValueChangeListener {
            produto.venc01 = it.value
          }
        }
      }

      horizontalLayout {
        integerFieldEditor("Qtd 2") {
          this.value = produto.qtty02
          addValueChangeListener {
            produto.qtty02 = it.value
          }
        }

        mesAnoFieldEditor("Venc 2") {
          this.value = produto.venc02
          addValueChangeListener {
            produto.venc02 = it.value
          }
        }
      }

      horizontalLayout {
        integerFieldEditor("Qtd 3") {
          this.value = produto.qtty03
          addValueChangeListener {
            produto.qtty03 = it.value
          }
        }

        mesAnoFieldEditor("Venc 3") {
          this.value = produto.venc03
          addValueChangeListener {
            produto.venc03 = it.value
          }
        }
      }

      horizontalLayout {
        integerFieldEditor("Qtd 4") {
          this.value = produto.qtty04
          addValueChangeListener {
            produto.qtty04 = it.value
          }
        }

        mesAnoFieldEditor("Venc 4") {
          this.value = produto.venc04
          addValueChangeListener {
            produto.venc04 = it.value
          }
        }
      }
    }
  }

  private fun HasComponents.integerFieldEditor(label: String, block: IntegerField.() -> Unit): IntegerField {
    val component = IntegerField(label)
    component.isAutoselect = true
    component.isClearButtonVisible = true
    component.block()
    add(component)
    return component
  }

  private fun HasComponents.mesAnoFieldEditor(label: String, block: ComboBox<String>.() -> Unit): ComboBox<String> {
    val component = mesAnoFieldComponente()
    component.label = label
    component.setOverlayWidth("100px")
    component.block()
    add(component)
    return component
  }
}
