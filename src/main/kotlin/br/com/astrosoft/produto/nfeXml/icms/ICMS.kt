package br.com.astrosoft.produto.nfeXml.icms

import com.fincatto.documentofiscal.nfe400.classes.nota.*

interface ICMS {
  val orig: String
  val cst: String
  val modBC: String
  val vBC: Double
  val pICMS: Double
  val vICMS: Double
  val vBCST: Double
  val pICMSST: Double
  val vICMSST: Double
  val pRedBCST: Double
  val vCredICMSSN: Double
  val vICMSDeson: Double
  val vICMSDif: Double
  val pDif: Double
  val pICMSEfet: Double
  val vICMSEfet: Double
  val pFCP: Double
  val vFCP: Double
  val pFCPST: Double
  val vFCPST: Double
  val vBCFCPSTRet: Double
  val vBCFCP: Double
  val vBCFCPST: Double
  val vFCPSTRet: Double
  val pFCPSTRet: Double
  val zero: Double
  val pCredSN: Double
  val pRedBCEfet: Double

  fun origCST(): String {
    return "$orig$cst"
  }
}

class ICMS00(val icms: NFNotaInfoItemImpostoICMS00) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBaseCalculo?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS02(val icms: NFNotaInfoItemImpostoICMS02) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0

  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS10(val icms: NFNotaInfoItemImpostoICMS10) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBaseCalculo?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = icms.valorBaseCalculoFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS15(val icms: NFNotaInfoItemImpostoICMS15) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS20(val icms: NFNotaInfoItemImpostoICMS20) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBCICMS?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = icms.valorICMSDesoneracao?.toDoubleOrNull() ?: 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = icms.valorBCFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS30(val icms: NFNotaInfoItemImpostoICMS30) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMSST?.codigo ?: ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = icms.valorICMSDesoneracao?.toDoubleOrNull() ?: 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS40(val icms: NFNotaInfoItemImpostoICMS40) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = icms.valorICMSDesoneracao?.toDoubleOrNull() ?: 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS51(val icms: NFNotaInfoItemImpostoICMS51) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBCICMS?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualICMS?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorICMS?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = icms.valorICMSDiferimento?.toDoubleOrNull() ?: 0.0
  override val pDif: Double = icms.percentualDiferimento?.toDoubleOrNull() ?: 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = icms.valorBCFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS53(val icms: NFNotaInfoItemImpostoICMS53) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = icms.percentualDiferimento?.toDoubleOrNull() ?: 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS60(val icms: NFNotaInfoItemImpostoICMS60) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = icms.valorFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0
  override val pFCPSTRet: Double = icms.percentualFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS61(val icms: NFNotaInfoItemImpostoICMS61) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS70(val icms: NFNotaInfoItemImpostoICMS70) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBC?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = icms.valorICMSDesoneracao?.toDoubleOrNull() ?: 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = icms.valorBCFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMS90(val icms: NFNotaInfoItemImpostoICMS90) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBC?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquota?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorTributo?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = icms.percentualFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vFCP: Double = icms.valorFundoCombatePobreza?.toDoubleOrNull() ?: 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = icms.valorICMSDesoneracao?.toDoubleOrNull() ?: 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = icms.valorBCFundoCombatePobreza?.toDoubleOrNull() ?: 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMSPartilhado(val icms: NFNotaInfoItemImpostoICMSPartilhado) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBCICMS?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquotaImposto?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorICMS?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMSst(val icms: NFNotaInfoItemImpostoICMSST) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoTributaria?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = icms.percentualAliquotaICMSEfetiva?.toDoubleOrNull() ?: 0.0
  override val vICMSEfet: Double = icms.valorICMSEfetivo?.toDoubleOrNull() ?: 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = icms.valorBCFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = icms.valorFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0
  override val pFCPSTRet: Double = icms.percentualFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = icms.percentualReducaoBCEfetiva?.toDoubleOrNull() ?: 0.0
}

class ICMSsn101(val icms: NFNotaInfoItemImpostoICMSSN101) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = icms.valorCreditoICMSSN?.toDoubleOrNull() ?: 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = icms.percentualAliquotaAplicavelCalculoCreditoSN?.toDoubleOrNull() ?: 0.0
  override val pRedBCEfet: Double = 0.00
}

class ICMSsn102(val icms: NFNotaInfoItemImpostoICMSSN102) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMSsn201(val icms: NFNotaInfoItemImpostoICMSSN201) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = icms.valorCreditoICMSSN?.toDoubleOrNull() ?: 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double  = icms.percentualAliquotaAplicavelCalculoCreditoSN?.toDoubleOrNull() ?: 0.0
  override val pRedBCEfet: Double = 0.00
}

class ICMSsn202(val icms: NFNotaInfoItemImpostoICMSSN202) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = 0.00
}

class ICMSsn500(val icms: NFNotaInfoItemImpostoICMSSN500) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = ""
  override val vBC: Double = 0.0
  override val pICMS: Double = 0.0
  override val vICMS: Double = 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = 0.0
  override val pICMSST: Double = 0.0
  override val vICMSST: Double = 0.0
  override val pFCPST: Double = 0.0
  override val vFCPST: Double = 0.0
  override val pRedBCST: Double = 0.0
  override val vCredICMSSN: Double = 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = icms.percentualAliquotaICMSEfetiva?.toDoubleOrNull() ?: 0.0
  override val vICMSEfet: Double = icms.valorICMSEfetivo?.toDoubleOrNull() ?: 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = icms.valorBCFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = 0.0
  override val vFCPSTRet: Double = icms.valorFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0
  override val pFCPSTRet: Double = icms.percentualFundoCombatePobrezaRetidoST?.toDoubleOrNull() ?: 0.0

  //Parte 4
  override val pCredSN: Double = 0.00
  override val pRedBCEfet: Double = icms.percentualReducaoBCEfetiva?.toDoubleOrNull() ?: 0.0
}

class ICMSsn900(val icms: NFNotaInfoItemImpostoICMSSN900) : ICMS {
  override val orig: String = icms.origem?.codigo ?: ""
  override val cst: String = icms.situacaoOperacaoSN?.codigo ?: ""
  override val modBC: String = icms.modalidadeBCICMS?.codigo ?: ""
  override val vBC: Double = icms.valorBCICMS?.toDoubleOrNull() ?: 0.0
  override val pICMS: Double = icms.percentualAliquotaImposto?.toDoubleOrNull() ?: 0.0
  override val vICMS: Double = icms.valorICMS?.toDoubleOrNull() ?: 0.0
  override val pFCP: Double = 0.0
  override val vFCP: Double = 0.0

  //parte 2
  override val vBCST: Double = icms.valorBCICMSST?.toDoubleOrNull() ?: 0.0
  override val pICMSST: Double = icms.percentualAliquotaImpostoICMSST?.toDoubleOrNull() ?: 0.0
  override val vICMSST: Double = icms.valorICMSST?.toDoubleOrNull() ?: 0.0
  override val pFCPST: Double = icms.percentualFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPST: Double = icms.valorFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val pRedBCST: Double = icms.percentualReducaoBCICMSST?.toDoubleOrNull() ?: 0.0
  override val vCredICMSSN: Double = icms.valorCreditoICMSSN?.toDoubleOrNull() ?: 0.0
  override val vICMSDeson: Double = 0.0
  override val vICMSDif: Double = 0.0
  override val pDif: Double = 0.0
  override val pICMSEfet: Double = 0.0
  override val vICMSEfet: Double = 0.0
  override val zero: Double = 0.0

  //Parte 3
  override val vBCFCPSTRet: Double = 0.0
  override val vBCFCP: Double = 0.0
  override val vBCFCPST: Double = icms.valorBCFundoCombatePobrezaST?.toDoubleOrNull() ?: 0.0
  override val vFCPSTRet: Double = 0.0
  override val pFCPSTRet: Double = 0.0

  //Parte 4
  override val pCredSN: Double = icms.percentualAliquotaAplicavelCalculoCreditoSN?.toDoubleOrNull() ?: 0.0
  override val pRedBCEfet: Double = 0.00
}

fun NFNotaInfoItemImpostoICMS.listTags(): List<ICMS> {
  return listOfNotNull(
    this.icms00?.let { ICMS00(it) },
    this.icms02?.let { ICMS02(it) },
    this.icms10?.let { ICMS10(it) },
    this.icms15?.let { ICMS15(it) },
    this.icms20?.let { ICMS20(it) },
    this.icms30?.let { ICMS30(it) },
    this.icms40?.let { ICMS40(it) },
    this.icms51?.let { ICMS51(it) },
    this.icms53?.let { ICMS53(it) },
    this.icms60?.let { ICMS60(it) },
    this.icms61?.let { ICMS61(it) },
    this.icms70?.let { ICMS70(it) },
    this.icms90?.let { ICMS90(it) },
    this.icmsPartilhado?.let { ICMSPartilhado(it) },
    this.icmsst?.let { ICMSst(it) },
    this.icmssn101?.let { ICMSsn101(it) },
    this.icmssn102?.let { ICMSsn102(it) },
    this.icmssn201?.let { ICMSsn201(it) },
    this.icmssn202?.let { ICMSsn202(it) },
    this.icmssn500?.let { ICMSsn500(it) },
    this.icmssn900?.let { ICMSsn900(it) }
  )
}

fun NFNotaInfoItemImpostoICMS.tags() = listTags().firstOrNull()