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
  (exists
  	(hero (name ?name)(status "unoccupied") ) 
  	(hero (status "occupied") ) 
  )
=>
  (printout t "save the day " crlf)
)
(assert (goal (emergency true) ) )
(assert (hero (name "spiderman") (status "unoccupied") ) )
(assert (hero (name "iceman") (status "unoccupied") ) )
(assert (hero (name "batman") (status "occupied") ) )
(assert (hero (name "green lanter") (status "occupied") ) )
(fire)
