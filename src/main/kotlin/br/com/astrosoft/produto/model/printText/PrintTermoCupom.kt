package br.com.astrosoft.produto.model.printText

import br.com.astrosoft.framework.model.printText.PrintText
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import java.time.LocalDate

class PrintTermoCupom() : PrintText<TermoRecebimento>() {
  override fun printTitle(bean: TermoRecebimento) {
    writeln(bean.dadosCliente.nome, negrito = true)
    writeln("CNPJ: ${bean.dadosCliente.cnpj}", negrito = true)
    writeln("End: ${bean.dadosCliente.endereco} Bairro ${bean.dadosCliente.bairro}", negrito = true)
    writeln("Cidade: ${bean.dadosCliente.cidade} Estado: ${bean.dadosCliente.uf}", negrito = true)
    writeln("")
    writeln("Termo de Recebimento de Volume - NI ${bean.ni}", negrito = true, center = true)
    writeln("")
    writeln("    Declaramos para os devidos fins  que  recebemos  os  volumes")
    writeln("constante nas notas fiscais e CT-e informados nos dados  fiscais")
    writeln("abaixo para conferência posterior e sujeitos as notificações  de")
    writeln("irregularidade  no  recebimento  tais como:  Produto  avaria  no")
    writeln("transporte, falta de volume e ou  falta  do  produto  dentro  do")
    writeln("volume,  produto  próximo  ao  vencimento  ou  produto  vencido,")
    writeln("produto com defeito de fabricação, produto em  desacordo  com  o")
    writeln("pedido de compra etc.")
    writeln("")
    writeln("Fornecedor: ${bean.dadosFornecedor.nome}")
    writeln("CNPJ: ${bean.dadosFornecedor.cnpj}")

    val notaFiscal = "Nota Fiscal: ${bean.dadosFornecedor.notaFiscal}"
    val emissao = "Emissão: ${bean.dadosFornecedor.emissao?.format() ?: ""}"
    val valor = "Valor: ${bean.dadosFornecedor.valor.format()}"

    val espacoResto = 64 - (notaFiscal.length + valor.length + emissao.length)
    val espaco1 = espacoResto / 2
    val espaco2 = (espacoResto - espaco1)

    writeln("${notaFiscal}${" ".repeat(espaco1)}$emissao")

    val volume = "Volumes: ${bean.dadosFornecedor.volumes?.format() ?: ""}"
    val pesoBruto = "Peso Bruto: ${bean.dadosFornecedor.pesoBruto?.format() ?: ""}"
    val espacoResto2 = (64 - (volume.length + pesoBruto.length)) / 2
    writeln("$volume${" ".repeat(espacoResto2)}$pesoBruto")
    writeln("")
    writeln("Transportadora: ${bean.dadosTransportadora.nome}")
    writeln("CNPJ: ${bean.dadosTransportadora.cnpj}")
    val cte = "CT-e: ${bean.dadosTransportadora.cte}"
    val emissaoTransp = "Emissão: ${bean.dadosTransportadora.emissao?.format() ?: ""}"
    val espacoResto3 = (64 - (cte.length + emissaoTransp.length)) / 2
    writeln("$cte${" ".repeat(espacoResto3)}$emissaoTransp")
    printLine()
  }

  override fun printSumary(bean: TermoRecebimento?) {
    writeln("")
    writeln("")
    // 17 de julho de 2025
    writeln("Teresina - PI ${LocalDate.now().format("dd 'de' MMMM 'de' yyyy")}", center = true)
    writeln("")
    writeln("")
    writeln("")
    val linha = "_____________________________________"
    val espacoLinha = (64 - linha.length) / 2
    val margem = " ".repeat(espacoLinha)
    writeln("${margem}${linha}")
    writeln("${margem}Nome:")
    writeln("${margem}CPF:")
    writeln("")
    writeln("")
  }
}