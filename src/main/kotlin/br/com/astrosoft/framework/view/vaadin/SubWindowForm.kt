package br.com.astrosoft.framework.view.vaadin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class SubWindowForm(
  protected val title: String = "",
  val header: VerticalLayout.(SubWindowForm) -> Unit = {},
  val toolBar: HorizontalLayout.(SubWindowForm) -> Unit = {},
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
        this.header(this@SubWindowForm)
      }

      verticalLayout {
        isSpacing = false
        isPadding = false
        if (fullSize) {
          setWidthFull()
        }

        if (title.isNotBlank()) {
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
      }
      horizontalLayout {
        content { align(left, baseline) }
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          onClick {
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

