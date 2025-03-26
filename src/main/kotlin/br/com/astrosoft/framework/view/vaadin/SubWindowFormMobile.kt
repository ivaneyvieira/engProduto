package br.com.astrosoft.framework.view.vaadin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.theme.lumo.LumoUtility

class SubWindowFormMobile(
  protected val title: String,
  val toolBar: HorizontalLayout.(SubWindowFormMobile) -> Unit = {},
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
      setSizeFull()
      //content { align(stretch, top) }
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
            //isExpand = true
          }
        }
      }
      horizontalLayout {
        content { align(left, baseline) }
        button("Fechar") {
          this.addThemeVariants(ButtonVariant.LUMO_SMALL)
          this.icon = VaadinIcon.CLOSE.create()
          onClick {
            onClose(this@SubWindowFormMobile)
            this@SubWindowFormMobile.close()
          }
        }
        toolBar(this@SubWindowFormMobile)
      }

      addAndExpand(blockForm())
    }
    isCloseOnEsc = true
  }
}

