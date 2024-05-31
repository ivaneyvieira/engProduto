package br.com.astrosoft.produto.view.produto

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.view.vaadin.TabPanelGrid
import br.com.astrosoft.framework.view.vaadin.columnGrid
import br.com.astrosoft.framework.view.vaadin.columnGroup
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.ECaracter
import br.com.astrosoft.produto.model.beans.FiltroProdutoInventario
import br.com.astrosoft.produto.model.beans.ProdutoInventario
import br.com.astrosoft.produto.model.beans.UserSaci
import br.com.astrosoft.produto.model.planilha.PlanilhaProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.ITabProdutoInventario
import br.com.astrosoft.produto.viewmodel.produto.TabProdutoInventarioViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.desc
import com.github.mvysny.kaributools.sort
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
    columnGroup("Produtos") {
      columnGrid(ProdutoInventario::codigo, header = "Código")
      columnGrid(ProdutoInventario::descricao, header = "Descrição").expand()
      columnGrid(ProdutoInventario::grade, header = "Grade")

      when (user?.lojaProduto) {
        2 -> this.columnGrid(ProdutoInventario::estoqueTotalDS, header = "Total")
        3 -> this.columnGrid(ProdutoInventario::estoqueTotalMR, header = "Total")
        4 -> this.columnGrid(ProdutoInventario::estoqueTotalMF, header = "Total")
        5 -> this.columnGrid(ProdutoInventario::estoqueTotalPK, header = "Total")
        8 -> this.columnGrid(ProdutoInventario::estoqueTotalTM, header = "Total")
        0 -> this.columnGrid(ProdutoInventario::estoqueTotal, header = "Total")
      }
      /*
            when (user?.lojaProduto) {
              2 -> this.columnGrid(ProdutoInventario::saldoDS, header = "Saldo")
              3 -> this.columnGrid(ProdutoInventario::saldoMR, header = "Saldo")
              4 -> this.columnGrid(ProdutoInventario::saldoMF, header = "Saldo")
              5 -> this.columnGrid(ProdutoInventario::saldoPK, header = "Saldo")
              8 -> this.columnGrid(ProdutoInventario::saldoTM, header = "Saldo")
              0 -> this.columnGrid(ProdutoInventario::saldo, header = "Saldo")
            }
      */
      if (user?.admin == true) {
        this.columnGrid(ProdutoInventario::venda, header = "Saída")
        columnGrid(ProdutoInventario::vencimentoStr, header = "Venc", width = "130px") {
          this.setComparator(Comparator.comparingInt { produto -> produto.vencimento ?: 0 })
        }.mesAnoFieldEditor()
      }
    }
    if (user?.lojaProduto == 2 || user?.lojaProduto == 0) {
      columnGroup("DS") {
        if (user.admin) {
          columnGrid(ProdutoInventario::estoqueDS, header = "Est", width = "70px").integerFieldEditor()
          columnGrid(ProdutoInventario::saidaDS, "Saída", width = "80px")
          columnGrid(ProdutoInventario::vencimentoDSStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoDS ?: 0 })
          }.mesAnoFieldEditor()
        } else {
          columnGrid(ProdutoInventario::vencimentoDSStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoDS ?: 0 })
          }.mesAnoFieldEditor()
          columnGrid(ProdutoInventario::estoqueDS, header = "Est", width = "70px").integerFieldEditor()
          //columnGrid(ProdutoInventario::saidaDS, "Saída", width = "80px")
        }
      }
    }
    if (user?.lojaProduto == 3 || user?.lojaProduto == 0) {
      columnGroup("MR") {
        if (user.admin) {
          columnGrid(ProdutoInventario::estoqueMR, header = "Est", width = "70px").integerFieldEditor()
          columnGrid(ProdutoInventario::saidaMR, "Saída", width = "80px")
          columnGrid(ProdutoInventario::vencimentoMRStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMR ?: 0 })
          }.mesAnoFieldEditor()
        } else {
          columnGrid(ProdutoInventario::vencimentoMRStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMR ?: 0 })
          }.mesAnoFieldEditor()
          columnGrid(ProdutoInventario::estoqueMR, header = "Est", width = "70px").integerFieldEditor()
          //columnGrid(ProdutoInventario::saidaMR, "Saída", width = "80px")
        }
      }
    }
    if (user?.lojaProduto == 4 || user?.lojaProduto == 0) {
      columnGroup("MF") {
        if (user.admin) {
          columnGrid(ProdutoInventario::estoqueMF, header = "Est", width = "70px").integerFieldEditor()
          columnGrid(ProdutoInventario::saidaMF, "Saída", width = "80px")
          columnGrid(ProdutoInventario::vencimentoMFStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMF ?: 0 })
          }.mesAnoFieldEditor()
        } else {
          columnGrid(ProdutoInventario::vencimentoMFStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoMF ?: 0 })
          }.mesAnoFieldEditor()
          columnGrid(ProdutoInventario::estoqueMF, header = "Est", width = "70px").integerFieldEditor()
          //columnGrid(ProdutoInventario::saidaMF, "Saída", width = "80px")
        }
      }
    }
    if (user?.lojaProduto == 5 || user?.lojaProduto == 0) {
      columnGroup("PK") {
        if (user.admin) {
          columnGrid(ProdutoInventario::estoquePK, header = "Est", width = "70px").integerFieldEditor()
          columnGrid(ProdutoInventario::saidaPK, "Saída", width = "80px")
          columnGrid(ProdutoInventario::vencimentoPKStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoPK ?: 0 })
          }.mesAnoFieldEditor()
        } else {
          columnGrid(ProdutoInventario::vencimentoPKStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoPK ?: 0 })
          }.mesAnoFieldEditor()
          columnGrid(ProdutoInventario::estoquePK, header = "Est", width = "70px").integerFieldEditor()
          //columnGrid(ProdutoInventario::saidaPK, "Saída", width = "80px")
        }
      }
    }
    if (user?.lojaProduto == 8 || user?.lojaProduto == 0) {
      columnGroup("TM") {
        if (user.admin) {
          columnGrid(ProdutoInventario::estoqueTM, header = "Est", width = "70px").integerFieldEditor()
          columnGrid(ProdutoInventario::saidaTM, "Saída", width = "80px")
          columnGrid(ProdutoInventario::vencimentoTMStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoTM ?: 0 })
          }.mesAnoFieldEditor()
        } else {
          columnGrid(ProdutoInventario::vencimentoTMStr, header = "Venc", width = "130px") {
            this.setComparator(Comparator.comparingInt { produto -> produto.vencimentoTM ?: 0 })
          }.mesAnoFieldEditor()
          columnGrid(ProdutoInventario::estoqueTM, header = "Est", width = "70px").integerFieldEditor()
          //columnGrid(ProdutoInventario::saidaTM, "Saída", width = "80px")
        }
      }
    }
    columnGrid(ProdutoInventario::dataEntrada, header = "Data Entrada", width = "120px").dateFieldEditor() {
      it.value = LocalDate.now()
    }
    columnGrid(ProdutoInventario::validade, header = "Val")
    columnGrid(ProdutoInventario::unidade, header = "Un")
    columnGrid(ProdutoInventario::vendno, header = "For")
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
    if (organiza) {
      val order = ProdutoInventario::vencimentoStr.desc
      gridPanel.sort(order)
    }
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