package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoInventarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.checkbox.Checkbox
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TabProdutoInventario(val viewModel: TabProdutoInventarioViewModel) :
  TabPanelGrid<ProdutoInventario>(ProdutoInventario::class),
  ITabProdutoInventario {
  private lateinit var edtPesquisa: TextField
  private lateinit var edtCodigo: TextField
  private lateinit var edtInventario: IntegerField
  private lateinit var edtAno: IntegerField
  private lateinit var edtMes: IntegerField
  private lateinit var edtGrade: TextField
  private lateinit var cmbCartacer: Select<ECaracter>
  private lateinit var chkOrganiza: Checkbox
  private lateinit var btnAdiciona: Button
  private lateinit var btnRemover: Button

  override fun HorizontalLayout.toolBarConfig() {
    edtPesquisa = textField("Pesquisa") {
      this.width = "300px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtCodigo = textField("Código") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtGrade = textField("Grade") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtInventario = integerField("Validade") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.TIMEOUT
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtMes = integerField("Mês") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.LAZY
      valueChangeTimeout = 500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    edtAno = integerField("Ano") {
      this.width = "100px"
      this.isClearButtonVisible = true
      valueChangeMode = ValueChangeMode.LAZY
      valueChangeTimeout = 500
      addValueChangeListener {
        viewModel.updateView()
      }
    }

    cmbCartacer = select("Caracter") {
      this.setItems(ECaracter.entries)
      this.setItemLabelGenerator { item ->
        item.descricao
      }
      this.value = ECaracter.TODOS
      addValueChangeListener {
        viewModel.updateView()
      }
    }

   btnAdiciona = button("Adicionar") {
      this.icon = VaadinIcon.PLUS.create()
      addClickListener {
        viewModel.adicionarLinha()
      }
    }

    btnRemover = button("Remover") {
      this.icon = VaadinIcon.TRASH.create()
      addClickListener {
        viewModel.removerLinha()
      }
    }

    downloadExcel(PlanilhaProdutoInventario())

    chkOrganiza = checkBox("Organiza") {
      addValueChangeListener {
        viewModel.updateView()
      }
    }
  }

  override fun Grid<ProdutoInventario>.gridPanel() {
    val user = AppConfig.userLogin() as? UserSaci

    this.addClassName("styling")
    setSelectionMode(Grid.SelectionMode.MULTI)
    this.withEditor(
      ProdutoInventario::class,
      openEditor = {
        when (user?.lojaProduto) {
          2 -> this.focusEditor(ProdutoInventario::estoqueDS)
          3 -> this.focusEditor(ProdutoInventario::estoqueMR)
          4 -> this.focusEditor(ProdutoInventario::estoqueMF)
          5 -> this.focusEditor(ProdutoInventario::estoquePK)
          8 -> this.focusEditor(ProdutoInventario::estoqueTM)
          0 -> this.focusEditor(ProdutoInventario::estoqueDS)
        }
      },
      closeEditor = {
        viewModel.salvaInventario(it.bean)
      })

    this.addColumnSeq("Seq", width = "50px")
    columnGrid(ProdutoInventario::codigo, header = "Código")
    columnGrid(ProdutoInventario::descricao, header = "Descrição").expand()
    columnGrid(ProdutoInventario::grade, header = "Grade")

    val colEstoqueTotal = when (user?.lojaProduto) {
      2 -> this.columnGrid(ProdutoInventario::estoqueTotalDS, header = "Total")
      3 -> this.columnGrid(ProdutoInventario::estoqueTotalMR, header = "Total")
      4 -> this.columnGrid(ProdutoInventario::estoqueTotalMF, header = "Total")
      5 -> this.columnGrid(ProdutoInventario::estoqueTotalPK, header = "Total")
      8 -> this.columnGrid(ProdutoInventario::estoqueTotalTM, header = "Total")
      0 -> this.columnGrid(ProdutoInventario::estoqueTotal, header = "Total")
      else -> null
    }

    if (user?.admin == true) {
      this.columnGrid(ProdutoInventario::saldo, header = "Saldo")
      this.columnGrid(ProdutoInventario::venda, header = "Saída")
      columnGrid(ProdutoInventario::vencimentoStr, header = "Venc", width = "130px") {
        this.setComparator(Comparator.comparingInt { produto -> produto.vencimento ?: 0 })
      }.mesAnoFieldEditor()
    }

    if (user?.lojaProduto == 2 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueDS, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vendasDS, "Saída", width = "80px")
      if (!user.admin || true) {
        columnGrid(ProdutoInventario::vencimentoDSStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoDS ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    if (user?.lojaProduto == 3 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueMR, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vendasMR, "Saída", width = "80px")
      if (!user.admin || true) {
        columnGrid(ProdutoInventario::vencimentoMRStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMR ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    if (user?.lojaProduto == 4 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueMF, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vendasMF, "Saída", width = "80px")
      if (!user.admin || true) {
        columnGrid(ProdutoInventario::vencimentoMFStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMF ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    if (user?.lojaProduto == 5 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoquePK, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vendasPK, "Saída", width = "80px")
      if (!user.admin || true) {
        columnGrid(ProdutoInventario::vencimentoPKStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoPK ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    if (user?.lojaProduto == 8 || user?.lojaProduto == 0) {
      columnGrid(ProdutoInventario::estoqueTM, header = "Est", width = "70px").integerFieldEditor()
      columnGrid(ProdutoInventario::vendasTM, "Saída", width = "80px")
      if (!user.admin || true) {
        columnGrid(ProdutoInventario::vencimentoTMStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoTM ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    columnGrid(ProdutoInventario::dataEntrada, header = "Data Entrada", width = "120px").dateFieldEditor() {
      it.value = LocalDate.now()
    }
    columnGrid(ProdutoInventario::validade, header = "Val")
    columnGrid(ProdutoInventario::unidade, header = "Un")
    columnGrid(ProdutoInventario::vendno, header = "For")
    //columnGrid(ProdutoInventario::fornecedorAbrev, header = "Fornecedor")

    val headerRow = prependHeaderRow()
    headerRow.join(
      this.getColumnBy(ProdutoInventario::codigo),
      this.getColumnBy(ProdutoInventario::descricao),
      this.getColumnBy(ProdutoInventario::grade),
      colEstoqueTotal,
    ).text = "Produto"
    if (user?.lojaProduto == 2 || user?.lojaProduto == 0) {
      if (user.admin) {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueDS),
          this.getColumnBy(ProdutoInventario::vendasDS),
          this.getColumnBy(ProdutoInventario::vencimentoDSStr),
        ).text = "DS"
      } else {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueDS),
          this.getColumnBy(ProdutoInventario::vendasDS),
          this.getColumnBy(ProdutoInventario::vencimentoDSStr),
        ).text = "DS"
      }
    }
    if (user?.lojaProduto == 3 || user?.lojaProduto == 0) {
      if (user.admin) {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueMR),
          this.getColumnBy(ProdutoInventario::vendasMR),
          this.getColumnBy(ProdutoInventario::vencimentoMRStr),
        ).text = "MR"
      } else {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueMR),
          this.getColumnBy(ProdutoInventario::vendasMR),
          this.getColumnBy(ProdutoInventario::vencimentoMRStr),
        ).text = "MR"
      }
    }
    if (user?.lojaProduto == 4 || user?.lojaProduto == 0) {
      if (user.admin) {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueMF),
          this.getColumnBy(ProdutoInventario::vendasMF),
          this.getColumnBy(ProdutoInventario::vencimentoMFStr),
        ).text = "MF"
      } else {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueMF),
          this.getColumnBy(ProdutoInventario::vendasMF),
          this.getColumnBy(ProdutoInventario::vencimentoMFStr),
        ).text = "MF"
      }
    }
    if (user?.lojaProduto == 5 || user?.lojaProduto == 0) {
      if (user.admin) {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoquePK),
          this.getColumnBy(ProdutoInventario::vendasPK),
          this.getColumnBy(ProdutoInventario::vencimentoPKStr),
        ).text = "PK"
      } else {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoquePK),
          this.getColumnBy(ProdutoInventario::vendasPK),
          this.getColumnBy(ProdutoInventario::vencimentoPKStr),
        ).text = "PK"
      }
    }
    if (user?.lojaProduto == 8 || user?.lojaProduto == 0) {
      if (user.admin) {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueTM),
          this.getColumnBy(ProdutoInventario::vendasTM),
          this.getColumnBy(ProdutoInventario::vencimentoTMStr),
        ).text = "TM"
      } else {
        headerRow.join(
          this.getColumnBy(ProdutoInventario::estoqueTM),
          this.getColumnBy(ProdutoInventario::vendasTM),
          this.getColumnBy(ProdutoInventario::vencimentoTMStr),
        ).text = "TM"
      }
    }
  }

  override fun filtro(): FiltroProdutoInventario {
    val user = AppConfig.userLogin() as? UserSaci
    return FiltroProdutoInventario(
      pesquisa = edtPesquisa.value ?: "",
      codigo = edtCodigo.value ?: "",
      validade = edtInventario.value ?: 0,
      grade = edtGrade.value ?: "",
      caracter = cmbCartacer.value ?: ECaracter.TODOS,
      ano = edtAno.value ?: 0,
      mes = edtMes.value ?: 0,
      loja = user?.lojaProduto ?: 0,
      organiza = chkOrganiza.value ?: false
    )
  }

  override fun updateProdutos(produtos: List<ProdutoInventario>) {
    updateGrid(produtos)
    val organiza = chkOrganiza.value ?: false
    btnAdiciona.isEnabled = !organiza
    btnRemover.isEnabled = !organiza
  }

  override fun produtosSelecionados(): List<ProdutoInventario> {
    return itensSelecionados()
  }

  override fun isAuthorized(): Boolean {
    val username = AppConfig.userLogin() as? UserSaci
    return username?.produtoInventario == true
  }

  override val label: String
    get() = "Inventário"

  override fun updateComponent() {
    viewModel.updateView()
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaProdutoInventario) {
    val button = LazyDownloadButton(VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(itensSelecionados())
      ByteArrayInputStream(bytes)
    })
    button.text = "Planilha"
    button.addThemeVariants(ButtonVariant.LUMO_SMALL)
    add(button)
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produto$textTime.xlsx"
  }
}