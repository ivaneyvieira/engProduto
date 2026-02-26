USE sqldados;

DO @ESTOQUE := ( SELECT MAX(ROUND((S.qtty_varejo + S.qtty_atacado) / 1000))
                 FROM
                   sqldados.stk AS S
                 WHERE storeno = :numloja
                   AND prdno = :prdno
                   AND grade = :grade );

REPLACE INTO produtoMovimentacao (numero, numloja, data, hora, prdno, grade, noGravado, movimentacao, estoque, noLogin,
                                  noEntregue, noRecebido, noRota, dataEntrege, horaEntrege, dataRecebido, horaRecebido)
  VALUE (:numero, :numloja, :data, :hora, :prdno, :grade, :noGravado, :movimentacao, @ESTOQUE, :noLogin, :noEntregue,
         :noRecebido, :noRota, :dataEntrege, :horaEntrege, :dataRecebido, :horaRecebido)
