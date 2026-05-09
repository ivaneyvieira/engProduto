package br.com.astrosoft.produto.view.estoqueCD

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.columnGrid
import br.com.astrosoft.framework.view.vaadin.helper.format
import br.com.astrosoft.framework.view.vaadin.helper.list
import br.com.astrosoft.framework.view.vaadin.helper.right
import br.com.astrosoft.produto.model.beans.ControleKardex
import br.com.astrosoft.produto.model.beans.ProdutoControle
import br.com.astrosoft.produto.model.planilha.PlanilhaKardexControle
import br.com.astrosoft.produto.viewmodel.estoqueCD.TabControleLojaViewModel
import com.github.mvysny.karibudsl.v10.textField
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import org.vaadin.stefan.LazyDownloadButton
import java.io.ByteArrayInputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DlgProdutoKardexLoja(
  val viewModel: TabControleLojaViewModel,
  val produto: ProdutoControle,
  val dataInicial: LocalDate?
) {
  private var onClose: (() -> Unit)? = null
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(ControleKardex::class.java, false)
  private lateinit var edtPesquisa: TextField

  fun showDialog(onClose: () -> Unit) {
    this.onClose = onClose
    val codigo = produto.codigo ?: 0
    val descricao = produto.descricao ?: ""
    val grade = produto.grade.let { gd ->
      if (gd.isNullOrBlank()) "" else " - $gd"
    }

    val dataInicial = produto.dataInicial ?: LocalDate.now().withDayOfMonth(1)

    form = SubWindowForm(
      title = "$codigo $descricao$grade Data Inicial: ${dataInicial.format()} Estoque: ${produto.saldo ?: 0}",
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.width = "300px"
          this.valueChangeMode = ValueChangeMode.LAZY
          this.valueChangeTimeout = 1500
          addValueChangeListener {
            update()
          }
        }

        downloadExcel(PlanilhaKardexControle())
      },
      onClose = {
        closeForm()
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
      isMultiSort = false
      selectionMode = Grid.SelectionMode.MULTI

      columnGrid(ControleKardex::loja, "Loja")
      columnGrid(ControleKardex::userLogin, "Usuário")
      columnGrid(ControleKardex::entLogin, "Entregue")
      columnGrid(ControleKardex::recLogin, "Recebido")
      columnGrid(ControleKardex::data, "Data")
      columnGrid(ControleKardex::lojaDoc, "Lj Doc").right()
      columnGrid(ControleKardex::pedido, "Pedido").right()
      columnGrid(ControleKardex::doc, "NF Fat").right()
      columnGrid(ControleKardex::docEnt, "NF Ent").right()
      columnGrid(ControleKardex::tipoDescricao, "Tipo")
      columnGrid(ControleKardex::observacao, "Observação")
      columnGrid(ControleKardex::vencimento, "Vencimento", pattern = "MM/yyyy", width = null)
      columnGrid(ControleKardex::qtde, "Qtd")
      columnGrid(ControleKardex::saldo, "Est CD")
      columnGrid(ControleKardex::saldoEmb, "Est Emb")
    }
    this.addAndExpand(gridDetail)
    update()
  }

  fun produtosSelecionados(): List<ControleKardex> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val kardex = viewModel.kardex(produto = produto, dataIncial = dataInicial).filter {
      val pesquisa = edtPesquisa.value?.trim() ?: ""
      if (pesquisa.isEmpty()) return@filter true
      val doc = it.doc ?: ""
      val data = it.data?.format() ?: ""
      val tipo = it.tipoDescricao
      doc.contains(pesquisa, ignoreCase = true) || data.startsWith(pesquisa) || tipo.contains(
        pesquisa,
        ignoreCase = true
      )
    }
    gridDetail.setItems(kardex)
 }

  private fun closeForm() {
    onClose?.invoke()
    form?.close()
  }

  private fun filename(): String {
    val sdf = DateTimeFormatter.ofPattern("yyMMddHHmmss")
    val textTime = LocalDateTime.now().format(sdf)
    return "produtoKardex$textTime.xlsx"
  }

  private fun HasComponents.downloadExcel(planilha: PlanilhaKardexControle) {
    val button = LazyDownloadButton("Planilha", VaadinIcon.TABLE.create(), { filename() }, {
      val bytes = planilha.write(gridDetail.list())
      ByteArrayInputStream(bytes)
    })
    add(button)
  }
}