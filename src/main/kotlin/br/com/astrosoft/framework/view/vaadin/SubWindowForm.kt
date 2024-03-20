package br.com.astrosoft.framework.view.vaadin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon

class SubWindowForm(
  protected val title: String,
  val toolBar: HasComponents.(SubWindowForm) -> Unit = {},
  val onClose: (Dialog) -> Unit = {},
  val fullSize: Boolean = true,
  val blockForm: () -> Component,
) : Dialog() {
  init {
    if (fullSize) {
      width = "100%"
      height = "100%"
    }

    verticalLayout {
      content { align(stretch, top) }
      isPadding = false
      verticalLayout {
        isSpacing = false
        isPadding = false
        if (fullSize) {
          setWidthFull()
        }

        title.split("|").forEach { linha ->
          p(linha) {
            this.style["margin"] = "0"
            this.style["padding"] = "0"
            this.style["font-size"] = "1em"
            this.style["font-weight"] = "bold"
            isExpand = true
          }
        }
      }
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

      addAndExpand(blockForm())
    }
    isCloseOnEsc = true
  }
}

