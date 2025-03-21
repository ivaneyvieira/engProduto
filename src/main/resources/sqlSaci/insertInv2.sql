INSERT INTO sqldados.inv2(invno, vendno, ordno, xfrno, issue_date, date, comp_date, ipi, icm, freight, netamt, grossamt,
                          subst_trib, discount, prdamt, despesas, base_ipi, aliq, cfo, nfNfno, auxLong1, auxLong2,
                          auxMoney1, auxMoney2, dataSaida, amtServicos, amtIRRF, amtINSS, amtISS, auxMoney3, auxMoney4,
                          auxMoney5, auxLong3, auxLong4, auxLong5, auxLong6, auxLong7, auxLong8, auxLong9, auxLong10,
                          auxLong11, auxLong12, auxMoney6, auxMoney7, auxMoney8, auxMoney9, auxMoney10, auxMoney11,
                          auxMoney12, auxMoney13, icmsUfRemet, fcp, fcpSt, fcpStRet, servico, baseIss, deducoes,
                          retencoes, impImport, issRetido, l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12, l13, l14,
                          l15, l16, l17, l18, l19, l20, l21, l22, l23, l24, l25, l26, l27, l28, m1, m2, m3, m4, m5, m6,
                          m7, m8, m9, m10, m11, m12, m13, m14, m15, m16, m17, m18, m19, m20, m21, m22, m23, m24, m25,
                          m26, m27, m28, weight, carrno, packages, storeno, indxno, book_bits, type, usernoFirst,
                          usernoLast, nfStoreno, bits, padbyte, auxShort1, auxShort2, auxShort3, auxShort4, auxShort5,
                          auxShort6, auxShort7, auxShort8, auxShort9, auxShort10, auxShort11, auxShort12, auxShort13,
                          auxShort14, bits2, bits3, bits4, bits5, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10, s11, s12,
                          s13, s14, s15, s16, s17, s18, s19, s20, s21, s22, s23, s24, s25, s26, s27, s28, nfname, invse,
                          account, remarks, contaCredito, contaDebito, nfNfse, auxStr1, auxStr2, auxStr3, auxStr4,
                          auxStr5, auxStr6, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10)
SELECT :invno,
       :vendno,
       :ordno,
       0                  AS xfrno,
       :issueDate,
       CURRENT_DATE * 1   AS date,
       CURRENT_DATE * 1   AS compDate,
       :ipi,
       :icm,
       :freight,
       :baseCalculo       AS netamt,
       :grossamt,
       :substTrib,
       :discount,
       :prdamt,
       :despesas,
       :baseIpi,
       :aliq,
       :cfo,
       0                  AS nfNfno,
       0                  AS auxLong1,
       :conhecimentoFrete AS auxLong2,
       0                  AS auxMoney1,
       0                  AS auxMoney2,
       :dataSaida,
       0                  AS amtServicos,
       0                  AS amtIRRF,
       0                  AS amtINSS,
       0                  AS amtISS,
       0                  AS auxMoney3,
       0                  AS auxMoney4,
       0                  AS auxMoney5,
       0                  AS auxLong3,
       0                  AS auxLong4,
       0                  AS auxLong5,
       0                  AS auxLong6,
       0                  AS auxLong7,
       0                  AS auxLong8,
       0                  AS auxLong9,
       0                  AS auxLong10,
       0                  AS auxLong11,
       0                  AS auxLong12,
       0                  AS auxMoney6,
       0                  AS auxMoney7,
       0                  AS auxMoney8,
       0                  AS auxMoney9,
       0                  AS auxMoney10,
       0                  AS auxMoney11,
       0                  AS auxMoney12,
       0                  AS auxMoney13,
       :icmsUfRemet,
       0                  AS fcp,
       0                  AS fcpSt,
       0                  AS fcpStRet,
       0                  AS servico,
       0                  AS baseIss,
       0                  AS deducoes,
       0                  AS retencoes,
       0                  AS impImport,
       0                  AS issRetido,
       0                  AS l1,
       0                  AS l2,
       :l3,
       0                  AS l4,
       0                  AS l5,
       0                  AS l6,
       0                  AS l7,
       0                  AS l8,
       0                  AS l9,
       0                  AS l10,
       0                  AS l11,
       0                  AS l12,
       0                  AS l13,
       0                  AS l14,
       0                  AS l15,
       0                  AS l16,
       0                  AS l17,
       0                  AS l18,
       0                  AS l19,
       0                  AS l20,
       0                  AS l21,
       0                  AS l22,
       0                  AS l23,
       0                  AS l24,
       0                  AS l25,
       0                  AS l26,
       0                  AS l27,
       0                  AS l28,
       0                  AS m1,
       0                  AS m2,
       0                  AS m3,
       0                  AS m4,
       0                  AS m5,
       :icmsDese          AS m6,
       0                  AS m7,
       0                  AS m8,
       0                  AS m9,
       0                  AS m10,
       0                  AS m11,
       0                  AS m12,
       0                  AS m13,
       0                  AS m14,
       0                  AS m15,
       0                  AS m16,
       0                  AS m17,
       0                  AS m18,
       0                  AS m19,
       0                  AS m20,
       0                  AS m21,
       0                  AS m22,
       0                  AS m23,
       0                  AS m24,
       0                  AS m25,
       0                  AS m26,
       0                  AS m27,
       0                  AS m28,
       :weight,
       :carrno,
       :packages,
       :storeno,
       0                  AS indxno,
       :bookBits,/*Flags Livro Entrada campos do livro de entrada*/
       0                  AS type,
       1                  AS usernoFirst,
       1                  AS usernoLast,
       0                  AS nfStoreno,
       5024               AS bits,
       0                  AS padbyte,
       0                  AS auxShort1,
       0                  AS auxShort2,
       0                  AS auxShort3,
       0                  AS auxShort4,
       0                  AS auxShort5,
       0                  AS auxShort6,
       0                  AS auxShort7,
       0                  AS auxShort8,
       0                  AS auxShort9,
       0                  AS auxShort10,
       0                  AS auxShort11,
       0                  AS auxShort12,
       0                  AS auxShort13,
       0                  AS auxShort14,
       0                  AS bits2,
       0                  AS bits3,
       0                  AS bits4,
       0                  AS bits5,
       0                  AS s1,
       0                  AS s2,
       0                  AS s3,
       0                  AS s4,
       0                  AS s5,
       0                  AS s6,
       0                  AS s7,
       0                  AS s8,
       0                  AS s9,
       0                  AS s10,
       0                  AS s11,
       0                  AS s12,
       0                  AS s13,
       0                  AS s14,
       0                  AS s15,
       0                  AS s16,
       0                  AS s17,
       0                  AS s18,
       0                  AS s19,
       0                  AS s20,
       0                  AS s21,
       0                  AS s22,
       0                  AS s23,
       0                  AS s24,
       0                  AS s25,
       0                  AS s26,
       0                  AS s27,
       0                  AS s28,
       :nfname,
       :invse,
       '2.01.20'          AS account,
       ''                 AS remarks,
       ''                 AS contaCredito,
       '271'              AS contaDebito,
       ''                 AS nfNfse,
       ''                 AS auxStr1,
       ''                 AS auxStr2,
       ''                 AS auxStr3,
       ''                 AS auxStr4,
       ''                 AS auxStr5,
       ''                 AS auxStr6,
       ''                 AS c1,
       ''                 AS c2,
       ''                 AS c3,
       ''                 AS c4,
       ''                 AS c5,
       ''                 AS c6,
       ''                 AS c7,
       ''                 AS c8,
       ''                 AS c9,
       ''                 AS c10
FROM
  DUAL