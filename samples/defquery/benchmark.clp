(batch ./samples/defquery/templates.clp)
(batch ./samples/defquery/queries.clp)
(bind ?start (ms-time) )
(watch-query queryByTransactionStart queryByTransactionAfterStart)
(load-facts ./samples/defquery/log_data.dat)
(bind ?end (ms-time) )
(bind ?load (- ?end ?start) )
(printout t "done loading data. time:" ?load crlf)
(bind ?result2 (run-query queryByTransactionAfterStart "1234bbbb" 1269285000000))
(bind ?time (query-time queryByTransactionAfterStart) )
(printout t "elapsed time: " ?time crlf)
(bind ?result2 (run-query queryByTransactionAfterStart "4976" 1269285000000))
(bind ?time (query-time queryByTransactionAfterStart) )
(printout t "elapsed time: " ?time crlf)
(bind ?result2 (run-query queryByTransactionAfterStart "1064" 1269285000000))
(bind ?time (query-time queryByTransactionAfterStart) )
(printout t "elapsed time: " ?time crlf)
(bind ?result2 (run-query queryByTransactionAfterStart "1741" 1269285000000))
(bind ?time (query-time queryByTransactionAfterStart) )
(printout t "elapsed time: " ?time crlf)
(bind ?result2 (run-query queryByTransactionAfterStart "5248" 1269285000000))
(bind ?time (query-time queryByTransactionAfterStart) )
(printout t "elapsed time: " ?time crlf)
