(defrule ContentStats
  ?report <- (Report
    (Application ?app)
  )
  ?cresult <- (cubequery
    (TransactionCube
      (Application ?app)
      (AvergeResponseTime ?aveRespTime)
      (MaxResponseTime ?maxRespTime)
      (AverageDbQueryTime ?aveDbTime)
      (MaxDbQueryTime ?maxDbTime)
      (NinetyPercentResponseTime ?ninetyPercent)
    )
  )
=>
  (printout t "results: " ?app " - ave resp: " ?aveRespTime " - max resp: " ?maxRespTime " - 90% resp: " ?ninetyPercent crlf)
  (printout t "- db ave: " ?aveDbTime " - db max: " ?maxDbTime crlf)
)