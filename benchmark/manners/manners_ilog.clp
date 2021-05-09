(deftemplate guest
   (slot name) 
   (slot sex)
   (slot hobby) )
   
(deftemplate last_seat
   (slot seat) )
   
(deftemplate seating
   (slot seat1)
   (slot name1)
   (slot name2)
   (slot seat2)
   (slot id)
   (slot pid)
   (slot path_done) )
   
(deftemplate context
   (slot name) )
   
(deftemplate path
   (slot id)
   (slot name)
   (slot seat) )
   
(deftemplate chosen
   (slot id)
   (slot name)
   (slot hobby) )

(deftemplate count
   (slot c) )
   
(defrule assign_first_seat ""
  ?context <- (context (name startup))
  (guest (name ?n))
  ?count <- (count (c ?cnt) )
=>
  (assert (seating (seat1 1) (name1 ?n) (name2 ?n) (seat2 1) (id ?cnt) (pid 0) (path_done TRUE) ) )
  (assert (path (id ?cnt) (name ?n) (seat 1) ) )
  (bind ?cntPlus1 (+ ?cnt 1) )
  (modify ?count (c ?cntPlus1) )
  (modify ?context (name assign_seats) )      
  (printout t "seat1 :1: name1 :" ?n ": seat2 :1: name2 :" ?n ": count :" ?cntPlus1 ": pid :0: path_done TRUE" crlf)
)


(defrule find_seating ""
  ?context <- (context (name assign_seats) )
  ?seating <- (seating (seat1 ?s1) (name2 ?n2) (seat2 ?s2) (id ?seatID) (path_done TRUE) )
  (guest (name ?n2) (sex ?sex1) (hobby ?h1) )
  (guest (name ?g2) (sex ~?sex1) (hobby ?h1) )
  ?count <- (count (c ?cnt) )
  (not (path (id ?seatID) (name ?g2) ) )
  (not (chosen (id ?seatID) (name ?g2) (hobby ?h1) ) )
=>
  (bind ?s2plus1 (+ ?s2 1) )
  (assert (seating (seat1 ?s2) (name1 ?n2) (name2 ?g2) (seat2 ?s2plus1) (id ?cnt) (pid ?seatID) (path_done FALSE) ) )
  (assert (path (id ?cnt) (name ?g2) (seat ?s2plus1) ) )
  (assert (chosen (id ?seatID) (name ?g2) (hobby ?h1) ) ) 
  (bind ?cntPlus1 (+ ?cnt 1) )
  (modify ?count (c ?cntPlus1) )
  (printout t "seat1 :" ?s2 ": name1 :" ?n2 ": seat2 :" ?s2plus1 ": name2 :" ?g2 ": id :" ?cnt ": pid :" ?seatID ": path_done FALSE" crlf)
)

(defrule make_path ""
  ?context <- (context (name assign_seats) )
  (seating (id ?sID) (pid ?sPID) (path_done FALSE) )
  (path (id ?sPID) (name ?n1) (seat ?s1) )
  (not (path (id ?sID) (name ?n1) ) )
=>
  (assert (path (id ?sID) (name ?n1) (seat ?s1) ) )
  (printout t "path - id=" ?sID " name=" ?n1 " seat=" ?s1 crlf)
)

(defrule path_done ""
  (declare (salience 10))
  ?context <- (context (name assign_seats) )
  ?seating <- (seating (path_done FALSE)(seat1 ?s1)(name1 ?n1) )
=>
  (modify ?seating (path_done TRUE) )
  (printout t ?s1 " path_done TRUE" crlf)
)

(defrule are_we_done ""
  (declare (salience 1))
  ?context <- (context (name assign_seats) )
  (last_seat (seat ?l_seat) )
  (seating (seat2 ?l_seat) )
=>
  (printout t "rule are_we_done: Yes, we are done:  Print Results!!" crlf)
  (modify ?context (name print_results) )
)

(defrule print_results ""
  (context (name print_results) )
  (seating (id ?seatID) (seat2 ?s2) )
  (last_seat (seat ?s2) )
  ?path <- (path (id ?seatID) (name ?n) (seat ?s) )
=>
  (printout t crlf "name :" ?n ": seat :" ?s ":" )
  (retract ?path)
)   
