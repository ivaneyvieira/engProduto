package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.DialogHelper
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.produto.model.beans.EmailDevolucao
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.viewmodel.devForRecebe.TabNotaPedidoViewModel
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.onClick
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout

class DlgEnviaEmail(val viewModel: TabNotaPedidoViewModel, var nota: NotaRecebimentoDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(EmailDevolucao::class.java, false)

  fun showDialog(onClose: () -> Unit) {
    form = SubWindowForm(
      title = "E-mails enviados para o fornecedor ${nota.fornecedor}",
      toolBar = {
        button("Novo Email") {
          this.icon = VaadinIcon.ENVELOPE.create()

          onClick {
            val email = viewModel.emailDevolucao(nota)
            val form = FormEmail(viewModel, email)
            DialogHelper.showForm("Nova mensagem", form){
              val email = form.emailDevolucao()
              viewModel.enviaEmail(email)
            }
          }
        }
        button("Remover Email") {
          this.icon = VaadinIcon.TRASH.create()
        }
        button("Reenviar") {
          this.icon = VaadinIcon.MAILBOX.create()
        }
      },
      onClose = {
        onClose()
      }) {
      HorizontalLayout().apply {
        setSizeFull()
        createGridProdutos()
      }
    }
    form?.open()
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      this.format()
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      removeThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(EmailDevolucao::toEmail, "E-mail")
      columnGrid(EmailDevolucao::dataEmail, "Data")
      columnGrid(EmailDevolucao::subject, "Assunto")
      columnGrid(EmailDevolucao::temAnexos, "Anexos")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<EmailDevolucao> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val chave = nota.chaveEmail
    val listEmail = EmailDevolucao.findAll(chave)
    gridDetail.setItems(listEmail)
  }

  fun updateEmail(): NotaRecebimentoDev? {
    nota = nota.refreshProdutosDev() ?: return null
    update()
    return nota
  }
}
