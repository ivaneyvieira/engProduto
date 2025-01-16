package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.ProdutoNFE
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEChave
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEDataEmissao
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaReceber
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaReceberViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEntradaReceber(val viewModel: TabNotaEntradaReceberViewModel) :
  TabPanelGrid<NotaEntrada>(NotaEntrada::class), ITabNotaEntradaReceber {
  private var dlgProduto: DlgProdutosReceber? = null
  private var edtChave: TextField? = null

  val userSaci
    get() = AppConfig.userLogin() as? UserSaci

  override fun HorizontalLayout.toolBarConfig() {
    edtChave = textField("Chave") {
      width = "400px"
      valueChangeMode = ValueChangeMode.ON_CHANGE
      addValueChangeListener {
        if (it.isFromClient) {
          viewModel.adicionaChave(it.value)
        }
      }
    }
  }

  override fun Grid<NotaEntrada>.gridPanel() {
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosReceber(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    addColumnButton(VaadinIcon.TRASH, "Remover", "Remover") { nota ->
      viewModel.removeNota(nota)
    }
    colunaNFEChave()
    colunaNFEDataEmissao()
    setClassNameGenerator {
      if (userSaci?.receberProcessar == true) when (it.marca) {
        0 -> "azul"
        1 -> "amarelo"
        else -> null
      }
      else null
    }
  }

  override fun updateNotas(notas: List<NotaEntrada>) {
    updateGrid(notas)
    edtChave?.clear()
    edtChave?.focus()
  }

  override fun notaSelecionada(): NotaEntrada? {
    return dlgProduto?.nota
  }

  override fun updateViewProduto() {
    dlgProduto?.update()
  }

  override fun produtosNota(): List<ProdutoNFE> {
    return dlgProduto?.produtosNota().orEmpty()
  }

  override fun produtosSelecionados(): List<ProdutoNFE> {
    return dlgProduto?.produtosSelecionados().orEmpty()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaEntradaReceber == true
  }

  override val label: String
    get() = "Receber"

  override fun updateComponent() {
    viewModel.updateView()
  }
}