package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.NotaSaidaDev
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.nativeLabel
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField

class DlgObservacaoNotaSaida(val nota: NotaSaidaDev, val salvaObservacao: (nota: NotaSaidaDev) -> Unit) {
  private var form: SubWindowForm? = null
  private lateinit var edtObservacao: TextField
  
  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(fullSize = false, header = {
      this.nativeLabel("Loja: ${nota.loja} Nota: ${nota.nota}")
    }, toolBar = {
      button("Gravar") {
        onClick {
          nota.observacaoNota = edtObservacao.value
          salvaObservacao(nota)
          form?.close()
          onClose()
        }
      }
    }, onClose = {
      onClose()
    }) {
      HorizontalLayout().apply {
        setSizeFull()
        edtObservacao = textField("Observação") {
          this.setWidthFull()
          this.maxLength = 40
          this.value = nota.observacaoNota
        }
      }
    }
    
    form?.width = "40%"
    form?.height = "40%"
    form?.open()
    
    val observacaoNotaPedido = nota.observacaoPadrao()
    
    if (observacaoNotaPedido.isNotEmpty()) {
      if (nota.obsNfVazia() || nota.observacaoNota == observacaoNotaPedido) {
        edtObservacao.value = observacaoNotaPedido
      } else {
        val dialog = DialogHelper.showQuestion("Padroniza a observação: '$observacaoNotaPedido'") {
          edtObservacao.value = observacaoNotaPedido
        }
      }
    }
  }
}



