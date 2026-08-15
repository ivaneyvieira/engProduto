package br.com.astrosoft.produto.view.devCliente

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.SubWindowForm
import br.com.astrosoft.framework.view.vaadin.helper.*
import br.com.astrosoft.produto.model.beans.*
import br.com.astrosoft.produto.viewmodel.devCliente.TabDevDadosViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.fetchAll
import com.github.mvysny.kaributools.getColumnBy
import com.vaadin.flow.component.Html
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgProdutosDadosDev(val viewModel: TabDevDadosViewModel, val nota: DadosDev) {
  private var form: SubWindowForm? = null
  private val gridDetail = Grid(DadosDevProduto::class.java, false)

  private var edtPesquisa: TextField? = null
  private var edtTipo: Select<ESolicitacaoTroca>? = null
  private var edtProduto: Select<EProdutoTroca>? = null
  private var edtNotaEntRet: IntegerField? = null

  fun showDialog(onClose: () -> Unit) {
    val readOnly = false
    val espaco = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0"
    val linha1 =
        "Loja: ${nota.loja.format("00")}${espaco}NF: ${nota.nfDevolucao}${espaco}Data: ${nota.dataDevolucao.format()}${espaco}Vendedor: ${nota.vendedor}"
    //val linha2 = "Tipo NF: ${nota.tipoNf}${espaco}Tipo Pgto: ${nota.tipoPgto}${espaco}Cliente: ${nota.cliente} - $nomeCliente"
    form = SubWindowForm(
      title = linha1,
      toolBar = {
        edtPesquisa = textField("Pesquisa") {
          this.valueChangeMode = ValueChangeMode.LAZY

          addValueChangeListener {
            update()
          }
        }
        edtTipo = select("Tipo") {
          this.isReadOnly = readOnly
          val tipos = ESolicitacaoTroca.entries
          this.setItems(tipos)
          this.value = nota.tipoDevEnum
          this.isReadOnly = true
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        edtProduto = select("Produto") {
          this.isReadOnly = readOnly
          val produtoTrocas = EProdutoTroca.entries
          this.setItems(produtoTrocas)
          this.value = nota.produtoTrocaEnum
          this.isReadOnly = true
          this.setItemLabelGenerator { item -> item.descricao }
          this.width = "10rem"
        }

        if (nota.nfTipo == 4 /*"ENTRE FUT"*/) {
          edtNotaEntRet = integerField("NF Ent Fut") {
            this.isReadOnly = readOnly
            this.width = "6rem"
            this.isAutoselect = true
            val nfNumero = nota.notaEntrega?.split("/")?.getOrNull(0)?.toIntOrNull() ?: 0
            this.value = nfNumero
            this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
            this.valueChangeMode = ValueChangeMode.LAZY

            viewModel.salvaNfEntRet(nota, nfNumero)

            addValueChangeListener {
              if (it.isFromClient) {
                viewModel.salvaNfEntRet(nota, this.value)
              }
            }
          }
        }

        button("Assina Troca") {
          this.icon = VaadinIcon.SIGN_IN.create()
          this.isEnabled = !readOnly

          onClick {
            nota.tipoDevEnum = edtTipo?.value
            nota.produtoTrocaEnum = edtProduto?.value
            nota.nfEntRet = edtNotaEntRet?.value ?: 0
            val produtos: List<DadosDevProduto> = gridDetail.dataProvider.fetchAll().filterNotNull()

            val user = AppConfig.userLogin() as? UserSaci

            val validacao = viewModel.validaProcesamento(user = user, nota = nota, produtos = produtos)

            if (validacao) {
              val formAutoriza = FormAutoriza()
              DialogHelper.showForm(caption = "Autoriza Devolução", form = formAutoriza) {
                viewModel.autorizaNotaVenda(nota, produtos, formAutoriza.login, formAutoriza.senha)
              }
            }
          }
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

  private fun updateNota() {
    edtTipo?.value = nota.tipoDevEnum
    edtProduto?.value = nota.produtoTrocaEnum
    edtNotaEntRet?.value = nota.nfEntRet
  }

  private fun HorizontalLayout.createGridProdutos() {
    gridDetail.apply {
      this.addClassName("styling")
      setSizeFull()
      addThemeVariants(GridVariant.LUMO_COMPACT)
      isMultiSort = false
      selectionMode = Grid.SelectionMode.NONE

      this.addItemClickListener {e ->
        val key = e.column.key
        val item = e.item ?: return@addItemClickListener
        when (key) {
          DadosDevProduto::dev.name                                   -> {
            item.dev = !(item.dev ?: false)
            if (item.dev == true) {
              item.temProduto = true
            } else {
              item.temProduto = null
              item.quantidadeTipo = item.quantidadeDev
            }
            this.dataProvider.refreshAll()
          }

          DadosDevProduto::temProduto.name if item.dev == true     -> {
            item.temProduto = !(item.temProduto ?: false)
            this.dataProvider.refreshAll()
          }

          DadosDevProduto::quantidadeTipo.name if item.dev == true -> {
            this.editor.editItem(item)
            this.focusEditor(DadosDevProduto::quantidadeTipo)
          }
        }
      }

      this.editor.addCloseListener {
        val bean = it.item
        val itens = gridDetail.dataProvider.fetchAll().filterNotNull()
        val quantDev = itens.filter { item ->
          item.prdno == bean.prdno && item.grade == bean.grade
        }.sumOf { item ->
          item.quantidadeTipo ?: 0
        }

        val quantidade = bean.quantidadeDev ?: 0
        val dif = quantidade - quantDev
        if (dif > 0) {
          val copyBean = bean.copy(
            quantidadeTipo = dif,
            dev = false,
          )
          copyBean.temProduto = false

          val pos = itens.indexOf(bean).let { index ->
            if (index == -1) {
              itens.size
            } else {
              index + 1
            }
          }
          val novoItens = itens.toMutableList()
          novoItens.add(pos, copyBean)

          this.setItems(novoItens)
        }
      }

      val user = AppConfig.userLogin() as? UserSaci

      columnGrid(DadosDevProduto::dev, header = "Dev")
      columnGrid(DadosDevProduto::temProduto, header = "Com Produto")
      columnGrid(DadosDevProduto::quantidadeTipo, header = "Qtd Dev").integerFieldEditor()
      if (user?.desautorizaDev == true) {
        addColumnButton(iconButton = VaadinIcon.TRASH, tooltip = "Desfaz troca", header = "Desfaz") { produto ->
          viewModel.desautorizaTroca(nota, produto)
        }
      }
      columnGrid(DadosDevProduto::codigo, header = "Código")
      columnGrid(DadosDevProduto::descricao, header = "Descrição")
      columnGrid(DadosDevProduto::grade, header = "Grade")
      columnGrid(DadosDevProduto::quantidadeDev, header = "Quant")
      columnGrid(DadosDevProduto::valorUnitario, header = "Preço") {
        this.setFooter(Html("\"<b><span style=\"font-size: medium; \">Total</span></b>\""))
      }
      columnGrid(DadosDevProduto::valorTotal, header = "Total")
    }
    this.addAndExpand(gridDetail)

    update()

    gridDetail.setPartNameGenerator {
      if (it.dev == true) {
        "amarelo"
      } else {
        null
      }
    }
  }

  fun itensSelecionados(): List<DadosDevProduto> {
    return gridDetail.selectedItems.toList()
  }

  fun update() {
    val pesquisa = edtPesquisa?.value.orEmpty()
    val listProdutos = nota.produtos.expande().filter { prd ->
      pesquisa == "" || (prd.codigo ?: "") == pesquisa ||
      (prd.descricao ?: "").contains(pesquisa, ignoreCase = true) ||
      (prd.ni == pesquisa.toIntOrNull())
    }
    gridDetail.setItems(listProdutos)
    updateNota()

    val totalValor = listProdutos.sumOf { it.valorTotal }
    val totalCol = gridDetail.getColumnBy(DadosDevProduto::valorTotal)
    totalCol.setFooter(Html("<b><font size=4>${totalValor.format()}</font></b>"))
  }

  fun produtos(): List<DadosDevProduto> {
    return gridDetail.list()
  }

  fun fecha() {
    form?.close()
  }
}