(deftemplate node
  (slot name)
  (slot parent)
  (slot updated)
)
(deftemplate state
  (slot name)
  (slot code)
)
(defrule nodechange
  ?nd <- (node
    (name ?name)
    (updated false)
  )
  (exists (node (parent ?name) (updated true) ) )
  (state
    (code ?name)
  )
=>
  (printout t "child of " ?name " has been updated and state is " ?name crlf)
)
(assert (node (name "node1")(parent "")(updated false) ) )
(assert (node (name "node1-1")(parent "node1")(updated true) ) )
(assert (state (code "node1") ) )
(fire)
