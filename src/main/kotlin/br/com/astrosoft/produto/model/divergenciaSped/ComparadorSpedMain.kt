package br.com.astrosoft.produto.model.divergenciaSped

import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode

object ComparadorSpedMain {
  @JvmStatic
  fun main(args: Array<String>) {
    if (args.size < 2) {
      println("Uso: <planilha.xlsx> <arquivo_sped.txt> [saida.xlsx]")
      return
    }

    val caminhoPlanilha = args[0]
    val caminhoSped = args[1]
    val caminhoSaida = if (args.size >= 3) args[2] else "divergencia_sped.xlsx"

    val planilha = lerPlanilhaInventario(caminhoPlanilha)
    val avisos = mutableListOf<SpedParseWarning>()
    val sped = lerRegistrosH010(caminhoSped, avisos::add)

    val divergencias = compararInventarios(planilha, sped)

    gerarPlanilhaSaida(caminhoSaida, divergencias)

    println("Divergencias geradas: ${divergencias.size}")
    if (avisos.isNotEmpty()) {
      println("Avisos de parsing SPED: ${avisos.size}")
    }
  }
}

private data class InventarioResumo(
  val codigo: String,
  val descricao: String,
  val quantidade: BigDecimal,
  val valorUnitario: BigDecimal
)

private data class DivergenciaInventarioDetalhe(
  val codigo: String,
  val descricao: String,
  val quantidadeExcel: BigDecimal?,
  val quantidadeSped: BigDecimal?,
  val valorUnitExcel: BigDecimal?,
  val valorUnitSped: BigDecimal?,
  val tipo: TipoDivergencia
)

private fun compararInventarios(
  planilha: List<PlanilhaInventario>,
  sped: List<ArquivoSped>
): List<DivergenciaInventarioDetalhe> {
  val inventarioExcel = resumirPlanilha(planilha)
  val inventarioSped = resumirSped(sped)

  val codigos = (inventarioExcel.keys + inventarioSped.keys).toSortedSet()
  val divergencias = mutableListOf<DivergenciaInventarioDetalhe>()

  val toleranciaQuantidade = BigDecimal("0.0001")
  val toleranciaValor = BigDecimal("0.01")

  for (codigo in codigos) {
    val excel = inventarioExcel[codigo]
    val spedItem = inventarioSped[codigo]

    when {
      excel != null && spedItem != null -> {
        val mesmaQuantidade = valoresIguaisComTolerancia(
          excel.quantidade,
          spedItem.quantidade,
          toleranciaQuantidade
        )
        val mesmoValorUnitario = valoresIguaisComTolerancia(
          excel.valorUnitario,
          spedItem.valorUnitario,
          toleranciaValor
        )

        if (!mesmaQuantidade || !mesmoValorUnitario) {
          divergencias.add(
            DivergenciaInventarioDetalhe(
              codigo = codigo,
              descricao = excel.descricao,
              quantidadeExcel = excel.quantidade,
              quantidadeSped = spedItem.quantidade,
              valorUnitExcel = excel.valorUnitario,
              valorUnitSped = spedItem.valorUnitario,
              tipo = TipoDivergencia.DIFERENTE
            )
          )
        }
      }

      excel != null -> {
        divergencias.add(
          DivergenciaInventarioDetalhe(
            codigo = codigo,
            descricao = excel.descricao,
            quantidadeExcel = excel.quantidade,
            quantidadeSped = null,
            valorUnitExcel = excel.valorUnitario,
            valorUnitSped = null,
            tipo = TipoDivergencia.SOMENTE_EXCEL
          )
        )
      }

      spedItem != null -> {
        divergencias.add(
          DivergenciaInventarioDetalhe(
            codigo = codigo,
            descricao = "",
            quantidadeExcel = null,
            quantidadeSped = spedItem.quantidade,
            valorUnitExcel = null,
            valorUnitSped = spedItem.valorUnitario,
            tipo = TipoDivergencia.SOMENTE_SPED
          )
        )
      }
    }
  }

  return divergencias
}

private fun resumirPlanilha(planilha: List<PlanilhaInventario>): Map<String, InventarioResumo> {
  return planilha
    .groupBy { it.numeroProduto.trim() }
    .mapValues { (codigo, itens) ->
      val descricao = itens.firstOrNull { it.descricaoProduto.isNotBlank() }?.descricaoProduto ?: ""
      val quantidade = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.quantidade }
      val valorTotal = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.valorTotal }
      val valorUnitario = calcularValorUnitario(quantidade, valorTotal, itens.firstOrNull()?.custoMedio)

      InventarioResumo(
        codigo = codigo,
        descricao = descricao,
        quantidade = quantidade,
        valorUnitario = valorUnitario
      )
    }
}

private fun resumirSped(sped: List<ArquivoSped>): Map<String, InventarioResumo> {
  return sped
    .groupBy { it.codItem.trim() }
    .mapValues { (codigo, itens) ->
      val quantidade = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.quantidade }
      val valorItem = itens.fold(BigDecimal.ZERO) { acc, item -> acc + item.valorItem }
      val valorUnitario = calcularValorUnitario(quantidade, valorItem, itens.firstOrNull()?.valorUnitario)

      InventarioResumo(
        codigo = codigo,
        descricao = "",
        quantidade = quantidade,
        valorUnitario = valorUnitario
      )
    }
}

private fun calcularValorUnitario(
  quantidade: BigDecimal,
  valorTotal: BigDecimal,
  fallback: BigDecimal?
): BigDecimal {
  return if (quantidade.compareTo(BigDecimal.ZERO) == 0) {
    fallback ?: BigDecimal.ZERO
  } else {
    valorTotal.divide(quantidade, 10, RoundingMode.HALF_UP)
  }
}

private fun gerarPlanilhaSaida(
  caminhoSaida: String,
  divergencias: List<DivergenciaInventarioDetalhe>
) {
  val workbook = XSSFWorkbook()
  val sheet = workbook.createSheet("Divergencias")

  val headerStyle = workbook.createCellStyle().apply {
    alignment = HorizontalAlignment.CENTER
    verticalAlignment = VerticalAlignment.CENTER
    setFont(workbook.createFont().apply { bold = true })
  }

  val headers = listOf(
    "Codigo",
    "Descricao",
    "Quantidade Excel",
    "Quantidade SPED",
    "Valor Unit Excel",
    "Valor Unit SPED",
    "Tipo"
  )

  val headerRow = sheet.createRow(0)
  headers.forEachIndexed { index, titulo ->
    headerRow.createCell(index).apply {
      setCellValue(titulo)
      cellStyle = headerStyle
    }
  }

  divergencias.forEachIndexed { index, item ->
    val row = sheet.createRow(index + 1)

    row.createCell(0).setCellValue(item.codigo)
    row.createCell(1).setCellValue(item.descricao)

    item.quantidadeExcel?.let { row.createCell(2).setCellValue(it.toDouble()) }
    item.quantidadeSped?.let { row.createCell(3).setCellValue(it.toDouble()) }
    item.valorUnitExcel?.let { row.createCell(4).setCellValue(it.toDouble()) }
    item.valorUnitSped?.let { row.createCell(5).setCellValue(it.toDouble()) }

    row.createCell(6).setCellValue(item.tipo.name)
  }

  for (i in headers.indices) {
    sheet.autoSizeColumn(i)
  }

  FileOutputStream(File(caminhoSaida)).use { output ->
    workbook.write(output)
  }
  workbook.close()
}
