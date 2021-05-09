(deftemplate hero
  (slot id (type INTEGER))
  (slot name (type STRING))
  (slot status (type STRING))
  (slot long (type STRING))
  (slot lat (type STRING))
)
(deftemplate goal
  (slot emergency)
  (slot people)
  (slot location)
)
(deftemplate people
  (slot id (type INTEGER))
  (slot name (type STRING))
  (slot status (type STRING))
  (slot long (type STRING))
  (slot lat (type STRING))
)
(defrule save_the_day
  (hero (name ?name))
  (exists (people (name ~?name) ) )
=>
  (printout t "save the day " crlf)
)
(defrule save_the_day2
  (hero (id ?id1))
  (exists (people (id ?id2&:(< ?id2 ?id1)) ) )
=>
  (printout t "save the day2 " crlf)
)

(assert (hero (name "spiderman") ) )
(assert (people (name "iceman") ) )

(assert (hero (id 200) ) )
(assert (people (id 100) ) )

(fire)