rm inputs/* outputs/* &
java Controller 100 &
java Node 0 100 2 "TEST MSG" &
java Node 1 100 2 "TEST MSG" &
java Node 2 100 -1 &
java Node 3 100 2 "TEST MSG" &
java Node 4 100 2 "TEST MSG" &
java Node 5 100 2 "TEST MSG" &
