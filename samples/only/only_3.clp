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
  (printout t "save the day " crlf)
)
(assert (goal (emergency true) ) )
(assert (hero (name "superman")(status "occupied") ) )
(assert (hero (name "spiderman")(status "unoccupied") ) )
(assert (hero (name "batman")(status "unoccupied") ) )
(assert (hero (name "robin")(status "unoccupied") ) )
(assert (hero (name "wolverine")(status "unoccupied") ) )
(retract 4)
(retract 5)
(retract 6)
