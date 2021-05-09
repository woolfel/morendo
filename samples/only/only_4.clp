(deftemplate person
  (slot name)
  (slot license)
  (slot age)
)
(deftemplate car
  (slot make)
  (slot model)
  (slot year)
  (slot owner)
  (slot licenseplate)
  (slot impound)
)
(deftemplate violation
  (slot citationNumber)
  (slot amount)
  (slot licenseplate)
  (slot driverlicense)
  (slot paid)
  (slot impound)
)
(defrule car_is_not_impounded
  (car
    (owner ?name)
    (licenseplate ?lplate)
    (impound ?impound)
  )
  (only
    (violation
      (licenseplate ?lplate)
      (impound ~?impound)
      (citationNumber ?citation)
    )
  )
=>
  (printout t ?lplate " should be impounded but currently isn't. citation number: " ?citation crlf)
)

(assert (car (make "chevy")(model "corvette")(year "2004")(licenseplate "winning2")(impound false) ) )
(assert (violation (citationNumber "456")(amount 150.00)(licenseplate "winning2")(impound true) ) )
(assert (violation (citationNumber "789")(amount 350.00)(licenseplate "winning2")(impound false) ) )
(assert (violation (citationNumber "1022")(amount 200.00)(licenseplate "winning2")(impound false) ) )

