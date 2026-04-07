package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.config.AppConfig
import br.com.astrosoft.framework.model.printText.IPrinter
import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.rpad
import br.com.astrosoft.produto.model.beans.AjusteEst
import br.com.astrosoft.produto.model.beans.EEstoque
import br.com.astrosoft.produto.model.beans.FiltroAjusteEst
import java.time.LocalDate
import java.time.LocalTime

class PrintAjusteEst(private val filtro: FiltroAjusteEst) : PrintText<AjusteEst>() {
  private var valorPedido: Double = 0.0
  override fun printTitle(bean: AjusteEst) {
    writeln("Relatorio Estoque", negrito = true, center = true)
    writeln("")
    writeln(
      "Data: ${LocalDate.now().format()}     Hora: ${LocalTime.now().format()}",
      negrito = true
    )
    writeln(
      text = "Loja: ${filtro.lojaSigla()}   Fornecedor: ${
        filtro.fornecedor.toString().rpad(5, " ")
      }   Tipo: ${filtro.tipo.toString().rpad(5, " ")}  Cl:${filtro.cl.toString().rpad(5, " ")}",
      negrito = true
    )
    writeln(
      text = "Caracter: ${filtro.caracter.descricao}                Letra Dup: ${filtro.letraDup.descricao}",
      negrito = true
    )
    val saldo = if (filtro.estoque == EEstoque.TODOS) "" else filtro.saldo.toString()
    writeln(
      text = "Estoque: ${filtro.estoque.descricao}  $saldo",
      negrito = true
    )
    writeln(
      text = "Usuario: ${AppConfig.userLogin()?.name}",
      negrito = true
    )

    printLine()
  }

  override fun print(dados: List<AjusteEst>, printer: IPrinter) {
    valorPedido = dados.sumOf { ((it.qttyTotal ?: 0) * 1.00) }
    super.print(dados, printer)
  }

  init {
    column(AjusteEst::codigoStr, "Codigo", 6)
    column(AjusteEst::descricao, "Descricao", 36)
    column(AjusteEst::gradeProduto, "Grade", 8)
    column(AjusteEst::localizacao, "Loc", 4)
    column(AjusteEst::qttyTotal, "_Quant", 6)
  }

  override fun printSumary(bean: AjusteEst?) {
    writeln("")
    writeln("")
    writeln("")
    writeln("")
  }
}