package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.produto.model.beans.*
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant

class FormSolicitacaoDevDados(val nota: DadosDev) : FormLayout() {
  private var edtTipoCredito: Select<ESolicitacaoTroca>? = null
  private var edtTipoDevolucao: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null

  init {
    val readOnly = !nota.loginSolicitacao.isNullOrBlank()
    val user = AppConfig.userLogin() as? UserSaci
    edtTipoCredito = select("Tipo do Crédito") {
      this.isReadOnly = readOnly
      val tipos = buildList {
        if (user?.autorizaTrocaP == true || user?.autorizaTroca == true) {
          add(ESolicitacaoTroca.Troca)
        }

        if (user?.autorizaEstorno == true) {
          add(ESolicitacaoTroca.Estorno)
        }

        if (user?.autorizaReembolso == true) {
          add(ESolicitacaoTroca.Reembolso)
        }

        if (user?.autorizaMuda == true) {
          add(ESolicitacaoTroca.MudaCliente)
        }
      }
      this.setItems(tipos)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota.tipoDevEnum

      this.addValueChangeListener {
        if (it.isFromClient) {
          this.isInvalid = false
          this.errorMessage = ""
          val value = it.value
          if (value != null) {
            try {
              nota.validaTipoCredito(value)
            } catch (e: Exception) {
              val message = e.message
              if (message != null) {
                DialogHelper.showWarning(message)
                this.focus()
                this.isInvalid = true
              }
            }
          }
        }
      }
    }

    edtTipoDevolucao = select("Tipo da Devolução") {
      this.isReadOnly = readOnly
      val entries = buildList {
        val comProduto = user?.autorizaTrocaP == true
        val semProduto = user?.autorizaTroca == true
        if (comProduto) add(EProdutoTroca.Com)
        if (semProduto) add(EProdutoTroca.Sem)
        if (comProduto && semProduto) add(EProdutoTroca.Misto)
      }
      this.setItems(entries)
      this.setItemLabelGenerator { item -> item.descricao }
      this.width = "300px"
      this.value = nota.produtoTrocaEnum

      this.addValueChangeListener {
        if (it.isFromClient) {
          this.isInvalid = false
          this.errorMessage = ""
          val value = it.value
          if (value != null) {
            try {
              nota.validaTipoDevolucao(value)
            } catch (e: Exception) {
              val message = e.message
              if (message != null) {
                DialogHelper.showWarning(message)
                this.focus()
                this.isInvalid = true
              }
            }
          }
        }
      }
    }

    if (nota.nfTipo == 4) /*"ENTRE FUT"*/{
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
  }

  fun validaFiltro(): Result<SolicitacaoTrocaSimples> {
    return if (edtTipoDevolucao?.isInvalid == true || edtTipoCredito?.isInvalid == true) {
      Result.failure(Exception("Filtro Inválido"))
    } else {
      val solicitacao = solicitacaoTroca()
      if (solicitacao == null) {
        Result.failure(Exception("Filtro Inválido"))
      } else {
        Result.success(solicitacao)
      }
    }
  }

  private fun solicitacaoTroca(): SolicitacaoTrocaSimples? {
    val solicitacaoTrocaEnum = edtTipoCredito?.value ?: return null
    val produtoTrocaEnum = edtTipoDevolucao?.value ?: return null
    val nfEntRet = edtNotaEntRet?.value

    return SolicitacaoTrocaSimples(
      solicitacaoTrocaEnnum = solicitacaoTrocaEnum,
      produtoTrocaEnum = produtoTrocaEnum,
      nfEntRet = nfEntRet,
    )
  }
}