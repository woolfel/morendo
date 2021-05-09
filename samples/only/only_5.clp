(deftemplate person
  (slot first)
  (slot last)
  (slot middle)
  (slot age)
)
(deftemplate team
  (slot players)
  (slot openings)
  (slot headcoach)
  (slot assistantcoach)
  (slot yearestablished)
  (slot record)
)
(defrule oldest_player
  (person
    (first ?younger)
    (age ?age)
  )
  (only
	  (person
	    (first ?fname)
	    (last ?lname)
	    (age ?age2&:(> ?age2 ?age) )
	  )
  )
=>
  (printout t ?fname " " ?lname " is the only person older than " ?younger crlf)
)
(assert (person (first "howard")(last "huges")(age 10) ) )
(assert (person (first "bill")(last "baldwin")(age 11) ) )
(assert (person (first "thomas")(last "jefferson")(age 9) ) )
