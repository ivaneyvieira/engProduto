package br.com.astrosoft.framework.view.vaadin

import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.onLeftClick
import com.github.mvysny.karibudsl.v10.verticalLayout
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.server.StreamResource

class SubWindowView(val filename: String, val bytesBoletos: ByteArray) : Dialog() {
  init {
    width = "100%"
    height = "100%"
    val resource = StreamResource(filename, ConverteByte(bytesBoletos))
    verticalLayout {
      isPadding = false
      horizontalLayout {
        add(Anchor(resource, "Download"))
        button("Fechar") {
          icon = VaadinIcon.CLOSE.create()
          onLeftClick {
            close()
          }
        }
      }

      if(filename.uppercase().endsWith(".PDF")) {
        addAndExpand(PDFViewer(resource))
      } else {
        addAndExpand(Image(resource, filename))
      }
    }
    isCloseOnEsc = true
  }
}
