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
(defrule save_the_day
  (goal
    (emergency true)
  )
  (exists (hero (name ?name)(status "unoccupied") ) )
=>
  (printout t "save the day " crlf)
)
(assert (goal (emergency true)(location "a") ) )
(assert (goal (emergency true)(location "b") ) )
(assert (goal (emergency true)(location "c") ) )
(assert (hero (name "spiderman") (status "unoccupied") ) )
(assert (hero (name "iceman") (status "unoccupied") ) )
(fire)
