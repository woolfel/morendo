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
(deftemplate crime
  (slot inprogress)
  (slot location)
)
(defrule save_the_day
  (goal
    (emergency true)
  )
  (exists (hero (status "unoccupied") ) )
  (crime
    (inprogress true)
    (location ?place)
  )
=>
  (printout t "send hero to " ?place crlf)
)
(assert (goal (emergency true)(location "a") ) )
(assert (hero (name "spiderman") (status "unoccupied") ) )
(assert (hero (name "iceman") (status "unoccupied") ) )
(assert (crime (inprogress true)(location "city hall") ) )
(fire)
