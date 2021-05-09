(defquery queryByTransactionStart (declare (variables ?trxId ?startTime) )
    (ApplicationRequestLog
        (TransactionID ?trxId)
        (Timestamp ?startTime)
    )
)
(defquery queryByTransactionAfterStart (declare (variables ?trxId ?startTime) )
    (ApplicationRequestLog
        (TransactionID ?trxId)
        (Timestamp ?ts&:(> ?ts ?startTime) )
    )
)