package br.com.astrosoft.framework.view

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon

class SubWindowForm(
  protected val labelTitle: String,
  val toolBar: HasComponents.(SubWindowForm) -> Unit = {},
  val onClose: (Dialog) -> Unit = {},
  val blockForm: () -> Component,
                   ) : Dialog() {
  init {
    width = "100%"
    height = "100%"

    verticalLayout {
      content { align(stretch, top) }
      isPadding = false
      horizontalLayout {
        content { align(left, baseline) }
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          onLeftClick {
            onClose(this@SubWindowForm)
            this@SubWindowForm.close()
          }
        }
        toolBar(this@SubWindowForm)
      }
      horizontalLayout {
        isSpacing = true
        isPadding = false
        setWidthFull()
        labelTitle.split("|").forEach { linha ->
          h4(linha) {
            isExpand = true
          }
        }
      }

      addAndExpand(blockForm())
    }
    isCloseOnEsc = true
  }
}

