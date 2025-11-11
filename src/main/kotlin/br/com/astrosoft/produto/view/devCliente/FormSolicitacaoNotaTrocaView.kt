package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EntradaDevCli
import com.github.mvysny.karibudsl.v10.textArea
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.formlayout.FormLayout

class FormSolicitacaoNotaTrocaView(val nota: EntradaDevCli) : FormLayout() {
  init {
    textField("Tipo") {
      this.isReadOnly = true
      this.width = "300px"
      this.value = nota.solicitacaoTrocaEnnum?.descricao ?: ""
    }

    textField("Produto") {
      this.isReadOnly = true
      this.width = "300px"
      this.value = nota.produtoTrocaEnnum?.descricao ?: ""
    }

    textArea("Motivos") {
      this.isReadOnly = true
      this.width = "300px"
      this.minRows = 1
      this.value = nota.strMotivoTroca
    }
  }
}