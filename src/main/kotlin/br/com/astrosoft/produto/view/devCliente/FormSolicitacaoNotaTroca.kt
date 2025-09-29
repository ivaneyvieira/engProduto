package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select

class FormSolicitacaoNotaTroca : FormLayout() {
  private var edtSolitacao: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null

  init {
    edtSolitacao = select("Solicitacao") {
      this.setItems(ESolicitacaoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = ESolicitacaoTroca.Troca
    }
    edtProduto = select("Produto") {
      this.setItems(EProdutoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = EProdutoTroca.Com
    }
  }

  val solicitacao: ESolicitacaoTroca?
    get() = edtSolitacao?.value
  val produto: EProdutoTroca?
    get() = edtProduto?.value
}