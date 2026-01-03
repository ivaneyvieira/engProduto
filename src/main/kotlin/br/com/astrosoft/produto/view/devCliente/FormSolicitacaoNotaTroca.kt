package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.*
import com.github.mvysny.karibudsl.v10.horizontalLayout
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.nativeLabel
import com.github.mvysny.karibudsl.v10.select
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoNotaTroca(val nota: NotaVenda) : FormLayout() {
  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null
  private var edtMotivo: Select<EMotivoTroca>? = null

  init {
    val readOnly = !nota.nameSolicitacao.isNullOrBlank()
    val user = AppConfig.userLogin() as? UserSaci
    edtTipo = select("Tipo") {
      this.isReadOnly = readOnly
      val tipos = ESolicitacaoTroca.entries
      this.setItems(tipos)
      this.setItemLabelGenerator { item -> item?.descricao ?: "" }
      this.width = "300px"
      this.value = nota.solicitacaoTrocaEnnum
    }

    edtProduto = select("Produto") {
      this.isReadOnly = readOnly
      val entries = EProdutoTroca.entries
      this.setItems(entries)
      this.setItemLabelGenerator { item -> item?.descricao ?: "" }
      this.width = "300px"
      this.value = nota.produtoTrocaEnnum
    }

    if (nota.tipoNf == "ENTRE FUT") {
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

    edtMotivo = select("Motivo:") {
      this.isReadOnly = readOnly
      this.setItems(EMotivoTroca.entries)
      this.setItemLabelGenerator { item -> item?.descricao ?: "" }
      this.value = nota.setMotivoTroca.firstOrNull()
      this.width = "10rem"
    }
  }

  val solicitacaoTroca: SolicitacaoTroca?
    get() {
      val solicitacaoTrocaEnnum = edtTipo?.value ?: return null
      val produtoTrocaEnnum = edtProduto?.value ?: return null
      val nfEntRet = edtNotaEntRet?.value
      val motivo = edtMotivo?.value ?: return null
      return SolicitacaoTroca(
        solicitacaoTrocaEnnum,
        produtoTrocaEnnum,
        nfEntRet,
        motivo,
      )
    }
}