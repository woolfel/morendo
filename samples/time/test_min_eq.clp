(bind ?t1 (now) )
(bind ?t2 (now) )
(eq-minute ?t1 ?t2)
(printout t ?t1 " = " ?t2 crlf)
