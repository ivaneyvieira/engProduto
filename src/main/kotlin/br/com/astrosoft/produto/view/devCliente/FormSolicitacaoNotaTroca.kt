package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.produto.model.beans.EMotivoTroca
import br.com.astrosoft.produto.model.beans.EProdutoTroca
import br.com.astrosoft.produto.model.beans.ESolicitacaoTroca
import br.com.astrosoft.produto.model.beans.NotaVenda
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.checkbox.CheckboxGroup
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoNotaTroca(val nota: NotaVenda?, val readOnly: Boolean) : FormLayout() {
  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtLogin: TextField? = null
  private var edtSenha: PasswordField? = null
  private var edtMotivo: CheckboxGroup<EMotivoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null

  init {
    edtTipo = select("Tipo") {
      this.isReadOnly = readOnly
      this.setItems(ESolicitacaoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota?.solicitacaoTrocaEnnum ?: ESolicitacaoTroca.Troca
    }

    edtProduto = select("Produto") {
      this.isReadOnly = readOnly
      this.setItems(EProdutoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota?.produtoTrocaEnnum ?: EProdutoTroca.Com
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

    edtMotivo = checkBoxGroup("Motivo:") {
      this.isReadOnly = readOnly
      this.setItems(EMotivoTroca.entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.value = nota?.setMotivoTroca ?: emptySet()
      this.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL)
      this.width = "100%"
    }

    edtLogin = textField("Login") {
      this.isVisible = ! readOnly
      this.isReadOnly = readOnly
      this.width = "300px"
    }

    edtSenha = passwordField("Senha") {
      this.isVisible = ! readOnly
      this.isReadOnly = readOnly
      this.width = "300px"
    }
  }

  val solicitacao: ESolicitacaoTroca?
    get() = edtTipo?.value
  val produto: EProdutoTroca?
    get() = edtProduto?.value
  val notaEntRet
    get() = edtNotaEntRet?.value
  val motivo
    get() = edtMotivo?.value ?: emptySet()
  val login: String
    get() = edtLogin?.value ?: ""
  val senha: String
    get() = edtSenha?.value ?: ""
}