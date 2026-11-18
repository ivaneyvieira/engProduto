package br.com.astrosoft.produto.view.reposicao

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.produto.model.beans.Movimentacao
import br.com.astrosoft.produto.model.beans.PrdGrade
import br.com.astrosoft.produto.model.beans.ProdutoMovimentacao
import br.com.astrosoft.produto.viewmodel.reposicao.TabReposicaoPedViewModel
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.select.Select
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

class DlgAdicionaMovimentacao(
    val viewModel: TabReposicaoPedViewModel,
    val pedido: Movimentacao,
    val localizacaoMov: String,
    val onClose: (DlgAdicionaMovimentacao) -> Unit = {}) : Dialog() {
  private val produtosAdd = mutableListOf<ProdutoMovimentacao>()
  private val produtoLinha: List<LinhaProduto> = buildList {
    repeat(10) { num ->
      val linha = LinhaProduto(viewModel = viewModel, acerto = pedido, index = num) { linha ->
        selecionaLinha(linha)
      }
      add(linha)
    }
  }
  
  fun selecionaLinha(linha: Int) {
    val linhaProduto = produtoLinha.getOrNull(linha)
    linhaProduto?.focus()
  }
  
  init {
    this.isModal = true
    this.headerTitle = headerTitle()
    this.footer.toolBar()
    
    verticalLayout {
      this.isMargin = false
      this.isPadding = false
      this.isSpacing = false
      
      setSizeFull()
      
      this.h4("Localização: $localizacaoMov")
      
      produtoLinha.forEach { linha ->
        this.add(linha)
      }
      produtoLinha.firstOrNull()?.focus()
    }
    this.width = "60%"
    this.height = "80%"
  }
  
  fun HasComponents.toolBar() {
    horizontalLayout {
      this.justifyContentMode = FlexComponent.JustifyContentMode.END
      button("Confirma") {
        this.setPrimary()
        onClick {
          closeForm()
        }
      }
      
      button("Cancelar") {
        this.addThemeVariants(ButtonVariant.LUMO_ERROR)
        onClick {
          this@DlgAdicionaMovimentacao.close()
        }
      }
    }
  }
  
  private fun headerTitle(): String {
    return "Adiciona Produto"
  }
  
  private fun closeForm() {
    val user = AppConfig.userLogin()
    val produtos: List<ProdutoMovimentacao> = produtoLinha.mapNotNull { linha ->
      linha.prdno ?: return@mapNotNull null
      
      val produto = ProdutoMovimentacao()
      produto.apply {
        this.numero = pedido.numero
        this.numloja = pedido.numloja
        this.data = pedido.data
        this.hora = pedido.hora
        this.noLogin = user?.no
        this.login = pedido.login
        this.usuario = pedido.usuario
        this.noRota = pedido.noRota
        
        
        this.localizacao = localizacaoMov
        
        this.locApp = linha.locApp
        this.prdno = linha.prdno
        this.grade = linha.grade
        this.barcode = linha.codigoBarras
        this.noGravado = pedido.noGravado
        this.gravadoLogin = pedido.gravadoLogin
        
        this.noEntregue = pedido.noEntregue
        this.entregue = pedido.entregue
        this.entregueNome = pedido.entregueNome
        
        this.noRecebido = pedido.noRecebido
        this.recebido = pedido.recebido
        this.recebidoNome = pedido.recebidoNome
        
        this.movimentacao = linha.movimentacao ?: 0
      }
    }
    
    produtosAdd.clear()
    produtosAdd.addAll(produtos)
    viewModel.updateProduto(localizacaoMov = localizacaoMov, pedido = pedido, produtos)
    
    onClose.invoke(this)
    this.close()
  }
  
  fun produtos(): List<ProdutoMovimentacao> {
    return produtosAdd.toList()
  }
}

class LinhaProduto(
    val viewModel: TabReposicaoPedViewModel,
    val acerto: Movimentacao,
    val index: Int,
    val selecionaLinha: (linha: Int) -> Unit) : HorizontalLayout() {
  private val listaProdutos = mutableListOf<PrdGrade>()
  private var edtCodigo: TextField
  private var edtCodigoBarras: TextField
  private var edtDescricao: TextField
  private var edtLoc: TextField
  private var edtGrade: Select<String>
  private var edtMovimentacao: IntegerField
  
  private fun produtoGrade(grade: String): PrdGrade? {
    val prdGrade = listaProdutos.firstOrNull {
      it.grade == grade
    }
    return prdGrade
  }
  
  init {
    this.isPadding = false
    this.isMargin = false
    this.isSpacing = true
    this.setWidthFull()
    
    edtCodigo = textField("Código") {
      if (index > 0) this.label = ""
      this.width = "6rem"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      
      this.addValueChangeListener { e ->
        if (e.isFromClient) {
          val lista = viewModel.findProdutos(codigo = this.value, acerto.numloja)
          listaProdutos.clear()
          listaProdutos.addAll(lista)
          
          val produto = listaProdutos.firstOrNull()
          
          edtGrade.setItems(listaProdutos.map { it.grade })
          edtGrade.value = produto?.grade
          edtDescricao.value = produto?.descricao
          edtLoc.value = produto?.locApp
          edtCodigoBarras.value = produto?.codigoBarras
          edtGrade.isEnabled = listaProdutos.size > 1
          
          if (listaProdutos.isNotEmpty()) {
            if (listaProdutos.size > 1) {
              edtGrade.focus()
            } else {
              edtMovimentacao.focus()
            }
          }
        }
      }
    }
    
    edtCodigoBarras = textField("Código de Barras") {
      if (index > 0) this.label = ""
      this.width = "8.5rem"
      this.isReadOnly = false
      this.tabIndex = -1
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      
      this.addValueChangeListener { e ->
        if (e.isFromClient) {
          val lista = viewModel.findProdutos(codigo = this.value, loja = acerto.numloja)
          listaProdutos.clear()
          listaProdutos.addAll(lista)
          
          val produto = listaProdutos.firstOrNull()
          
          edtGrade.setItems(listaProdutos.map { it.grade })
          edtGrade.value = listaProdutos.firstOrNull()?.grade
          edtDescricao.value = produto?.descricao
          edtLoc.value = produto?.locApp
          edtCodigo.value = produto?.codigo
          edtGrade.isEnabled = listaProdutos.size > 1
          
          if (listaProdutos.isNotEmpty()) {
            if (listaProdutos.size > 1) {
              edtGrade.focus()
            } else {
              edtMovimentacao.focus()
            }
          }
        }
      }
    }
    
    edtGrade = select("Grade") {
      if (index > 0) this.label = ""
      this.width = "8rem"
      
      this.addValueChangeListener { event ->
        if (event.isFromClient) {
          val grade = event.value ?: ""
          val produto = produtoGrade(grade)
          edtCodigoBarras.value = produto?.codigoBarras ?: ""
          edtLoc.value = produto?.locApp?.trim() ?: ""
        }
      }
    }
    
    edtMovimentacao = integerField("Quant") {
      if (index > 0) this.label = ""
      this.width = "5rem"
      this.isClearButtonVisible = true
      this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
      this.valueChangeMode = ValueChangeMode.LAZY
      this.valueChangeTimeout = 1000
      
      this.addValueChangeListener {
        if (it.isFromClient) {
          selecionaLinha(index + 1)
        }
      }
    }
    
    edtLoc = textField("Loc") {
      if (index > 0) this.label = ""
      this.width = "4rem"
      this.isReadOnly = true
      this.tabIndex = -1
    }
    
    edtDescricao = textField("Descrição") {
      if (index > 0) this.label = ""
      this.setWidthFull()
      this.isReadOnly = true
      this.tabIndex = -1
    }
  }
  
  val prdno: String?
    get() = listaProdutos.firstOrNull()?.prdno
  val locApp: String?
    get() = edtLoc.value?.trim()
  val grade: String
    get() = edtGrade.value ?: ""
  val saldo: Int?
    get() = produtoGrade(grade)?.saldo
  val movimentacao: Int?
    get() = edtMovimentacao.value
  val codigoBarras: String
    get() = edtCodigoBarras.value ?: ""
  
  fun focus() {
    edtCodigo.focus()
  }
}