package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.Templates
import br.com.astrosoft.framework.model.reports.Templates.fieldFont
import br.com.astrosoft.framework.model.reports.Templates.fieldFontTermo
import br.com.astrosoft.framework.model.reports.Templates.fieldFontTermoNegrido
import br.com.astrosoft.framework.model.reports.horizontalBlock
import br.com.astrosoft.framework.model.reports.horizontalList
import br.com.astrosoft.framework.model.reports.text
import br.com.astrosoft.framework.model.reports.verticalBlock
import br.com.astrosoft.framework.model.reports.verticalList
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.util.lpad
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import com.vaadin.copilot.javarewriter.JavaStyleRewriter.setStyle
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.StretchType
import net.sf.dynamicreports.report.constant.TextAdjust
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import okhttp3.internal.http2.StreamResetException
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import javax.swing.text.StyleConstants.setBold

class ReportTermoRecebimento2(val bean: TermoRecebimento) {
  protected fun VerticalListBuilder.writeln(
    text: String,
    negrito: Boolean = false,
    center: Boolean = false
  ) {
    this.text(
      text = text,
      horizontalTextAlignment = if (center) HorizontalTextAlignment.CENTER else HorizontalTextAlignment.JUSTIFIED
    ) {
      if (negrito) {
        setStyle(fieldFontTermoNegrido)
      } else {
        setStyle(fieldFontTermo)
      }
      this.setTextAdjust(TextAdjust.STRETCH_HEIGHT)
    }
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      writeln(" ${bean.dadosCliente.nome}", negrito = true)
      writeln(" CNPJ: ${bean.dadosCliente.cnpj}", negrito = true)
      writeln(" End: ${bean.dadosCliente.endereco} Bairro ${bean.dadosCliente.bairro}", negrito = true)
      writeln(" Cidade: ${bean.dadosCliente.cidade} Estado: ${bean.dadosCliente.uf}", negrito = true)
      writeln(" E-mail: ${bean.empEmailTermo}", negrito = true)
      writeln(" WhatsApp: (86) 99978-0752", negrito = true)
      writeln("")
      val volumeRec = bean.volumesRec?.toString() ?: "0"
      writeln("Termo de Recebimento de Volume(s) - NI ${bean.ni}", negrito = true, center = true)
      writeln("")
      if (bean.volumesDivergencia()) {
        writeln("       Declaramos para os devidos fins que recebemos $volumeRec volume(s) divergente do que constam na NF-e e CT-e informados abaixo, para posterior conferencia e sujeitos as notificações de irregularidade no recebimento.")
      } else {
        writeln("       Declaramos para os devidos fins que recebemos volumeRec volume(s) que constam  na NF-e e CT-e informados abaixo, para posterior conferencia e sujeitos as notificações de irregularidade no recebimento.")
      }
      writeln("")
      writeln(" Fornecedor: ${bean.dadosFornecedor.nome}")
      writeln(" CNPJ: ${bean.dadosFornecedor.cnpj}")

      val notaFiscal = "Nota Fiscal: ${bean.dadosFornecedor.notaFiscal}"
      val emissao = "Emissão: ${bean.dadosFornecedor.emissao?.format() ?: ""}"

      val espacoResto = 32 - notaFiscal.length

      writeln(" ${notaFiscal}${" ".repeat(espacoResto)}$emissao")

      val volume = " Volumes: ${bean.volumesInf?.format() ?: ""}"
      writeln(volume)
      writeln("")
      writeln(" Transportadora: ${bean.dadosTransportadora.nome}")
      writeln(" CNPJ: ${bean.dadosTransportadora.cnpj}")
      val cte = "CT-e: ${bean.dadosTransportadora.cte}"
      val emissaoTransp = "Emissão: ${bean.dadosTransportadora.emissao?.format() ?: ""}"
      val espacoResto3 = 32 - cte.length
      writeln(" $cte${" ".repeat(espacoResto3)}$emissaoTransp")
      writeln("")
      writeln("")
      writeln("Teresina-PI ${LocalDate.now().format("dd 'de' MMMM 'de' yyyy")}", center = true)
      writeln("")
      writeln("")
      val linha = "_____________________________________"
      val nome = bean.nomeassinatura
      val cpf = bean.cpf
      writeln(linha, center = true)
      writeln(nome, center = true)
      writeln("CPF: $cpf", center = true)
      writeln("Recebedor", center = true)
      writeln("")
    }
  }

  fun makeReport(): JasperReportBuilder? {
    val itens = listOf(bean)
    val pageOrientation = PORTRAIT
    return report()
      .title(titleBuiderPedido())
      .setTemplate(Templates.reportTemplate)
      .setColumnStyle(stl.style().setFontSize(7))
      .setDataSource(itens.toList())
      .setPageFormat(A4, pageOrientation)
      .setPageMargin(margin(28))
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(termo: TermoRecebimento): ByteArray? {
      val print = ReportTermoRecebimento2(termo).makeReport()?.toJasperPrint() ?: return null
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(listOf(print)))

      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

      exporter.exportReport()
      return out.toByteArray()
    }
  }
}