package br.com.astrosoft.produto.view.notaEntrada

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.addColumnButton
import br.com.astrosoft.produto.model.beans.FiltroNotaEntrada
import br.com.astrosoft.produto.model.beans.NotaEntrada
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEDataEmissao
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEDataEntrada
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEFornecedor
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFELoja
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENI
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENomeFornecedor
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFENota
import br.com.astrosoft.produto.view.notaEntrada.columns.NotaEColumns.colunaNFEValor
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaRecebido
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaRecebidoViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEntradaRecebido(val viewModel: TabNotaEntradaRecebidoViewModel) :
  TabPanelGrid<NotaEntrada>(NotaEntrada::class), ITabNotaEntradaRecebido {
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtNota: TextField
  private lateinit var edtNI: IntegerField
  private lateinit var edtLoja: IntegerField
  private lateinit var edtChave: TextField
  private var dlgProduto: DlgProdutosRecebido? = null

  override fun HorizontalLayout.toolBarConfig() {
    edtLoja = integerField("Loja") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      val user = AppConfig.userLogin() as? UserSaci
      value = user?.storeno
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtNI = integerField("NI") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtNota = textField("Nota") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtFornecedor = integerField("Fornecedor") {
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtChave = textField("Chave") {
      width = "400px"
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<NotaEntrada>.gridPanel() {
    colunaNFELoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosRecebido(viewModel, nota)
      dlgProduto?.showDialog {
        viewModel.updateView()
      }
    }
    colunaNFENI()
    colunaNFENota()
    colunaNFEDataEmissao()
    colunaNFEDataEntrada()
    colunaNFEFornecedor()
    colunaNFENomeFornecedor()
    colunaNFEValor()
  }

  override fun filtro() = FiltroNotaEntrada(
    loja = edtLoja.value ?: 0,
    ni = edtNI.value ?: 0,
    nota = edtNota.value ?: "",
    vendno = edtFornecedor.value ?: 0,
    chave = edtChave.value ?: "",
  )

  override fun updateNotas(notas: List<NotaEntrada>) {
    updateGrid(notas)
  }

  override fun notaSelecionada(): NotaEntrada? {
    return dlgProduto?.nota
  }

  override fun updateViewProduto() {
    dlgProduto?.update()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.notaEntradaRecebido == true
  }

  override val label: String
    get() = "Recebido"

  override fun updateComponent() {
    viewModel.updateView()
  }
}