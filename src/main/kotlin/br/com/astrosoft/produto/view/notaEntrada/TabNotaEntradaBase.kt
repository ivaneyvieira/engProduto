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
import br.com.astrosoft.produto.viewmodel.notaEntrada.ITabNotaEntradaBase
import br.com.astrosoft.produto.viewmodel.notaEntrada.TabNotaEntradaBaseViewModel
import com.github.mvysny.karibudsl.v10.integerField
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode

class TabNotaEntradaBase(val viewModel: TabNotaEntradaBaseViewModel) : TabPanelGrid<NotaEntrada>(NotaEntrada::class),
  ITabNotaEntradaBase {
  private lateinit var edtFornecedor: IntegerField
  private lateinit var edtNota: TextField
  private lateinit var edtNI: IntegerField
  private lateinit var edtLoja: IntegerField
  private lateinit var edtChave: TextField
  private var dlgProduto: DlgProdutosBase? = null

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
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtNota = textField("Nota") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtFornecedor = integerField("Fornecedor") {
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
    edtChave = textField("Chave") {
      width = "400px"
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1500
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<NotaEntrada>.gridPanel() {
    colunaNFELoja()
    addColumnButton(VaadinIcon.FILE_TABLE, "Produtos", "Produtos") { nota ->
      dlgProduto = DlgProdutosBase(viewModel, nota)
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
    setPartNameGenerator {
      when (it.marca) {
        0    -> "azul"
        1    -> "amarelo"
        else -> null
      }
    }
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
    return username?.notaEntradaBase == true
  }

  override val label: String
    get() = "Base"

  override fun updateComponent() {
    viewModel.updateView()
  }
}