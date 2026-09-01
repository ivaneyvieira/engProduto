package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.nativeLabel
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textArea
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import javax.swing.Spring.height

class DlgObservacaoNotaPedido(val nota: NotaRecebimentoDev, val salvaObservacao: (nota: NotaRecebimentoDev) -> Unit) {
  private var form: SubWindowForm? = null
  private lateinit var edtObservacao: TextField

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      fullSize = false,
      header = {
        this.nativeLabel("Loja: ${nota.lojaSigla} Nota: ${nota.notaDevolucao}")
      },
      toolBar = {
        button("Gravar") {
          onClick {
            nota.obsNF = edtObservacao.value
            salvaObservacao(nota)
          }
        }
      },
      onClose = {
        onClose()
      }) {

      HorizontalLayout().apply {
        setSizeFull()
        edtObservacao = textField("Observação") {
          this.setWidthFull()
          this.maxLength = 40
          this.value = nota.obsNF
        }
      }
    }
    form?.width = "40%"
    form?.height = "40%"
    form?.open()
  }
}



