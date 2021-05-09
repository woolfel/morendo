(deftemplate hero
  (slot name)
  (slot status)
  (slot long)
  (slot lat)
)
(deftemplate goal
  (slot emergency)
  (slot people)
  (slot location)
)
(defrule only_one_hero
  (goal
    (emergency true)
  )
  (only (hero (name ?name)(status "unoccupied") ) )
=>
  (printout t ?name " save the day " crlf)
)
(assert (goal (emergency true) ) )
(assert (hero (name "superman")(status "occupied") ) )
(assert (hero (name "spiderman")(status "unoccupied") ) )