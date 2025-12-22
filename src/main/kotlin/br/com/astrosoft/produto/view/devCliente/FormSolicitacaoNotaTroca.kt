package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import br.com.astrosoft.produto.model.beans.NotaVenda
import br.com.astrosoft.produto.model.beans.SolicitacaoTroca
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoNotaTroca(val nota: NotaVenda) : FormLayout() {
  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null
  private var edtNotaEntRet: IntegerField? = null

  init {
    val readOnly = ! nota.nameSolicitacao.isNullOrBlank()
    edtTipo = select("Tipo") {
      this.isReadOnly = readOnly
      this.setItems(ESolicitacaoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota.solicitacaoTrocaEnnum ?: ESolicitacaoTroca.Troca
    }

    edtProduto = select("Produto") {
      this.isReadOnly = readOnly
      this.setItems(EProdutoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota.produtoTrocaEnnum ?: EProdutoTroca.Com
    }

    if (nota?.tipoNf == "ENTRE FUT") {
      horizontalLayout {
        nativeLabel("NF Ent/Ret:")
        edtNotaEntRet = integerField {
          this.isReadOnly = readOnly
          this.value = nota.nfEntRet
          this.width = "6rem"
          this.isAutoselect = true
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
      }
    }

    edtLogin = textField("Login") {
      this.isVisible = !readOnly
      this.isReadOnly = readOnly
      this.width = "300px"
    }

    edtSenha = passwordField("Senha") {
      this.isVisible = !readOnly
      this.isReadOnly = readOnly
      this.width = "300px"
    }
  }

  val solicitacaoTroca: SolicitacaoTroca?
    get() {
      val solicitacaoTrocaEnnum = edtTipo?.value ?: return null
      val produtoTrocaEnnum = edtProduto?.value ?: return null
      val nfEntRet = edtNotaEntRet?.value
      val login: String = edtLogin?.value ?: ""
      val senha: String = edtSenha?.value ?: ""
      return SolicitacaoTroca(
        solicitacaoTrocaEnnum,
        produtoTrocaEnnum,
        nfEntRet,
        login,
        senha
      )
    }
}