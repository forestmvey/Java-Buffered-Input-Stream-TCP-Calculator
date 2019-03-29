# Java-Buffered-Input-Stream-TCP-Calculator
A java calculator using TCP transmission protocol to relay integer calculations using multiple library implementations.

Version A single BufferedOutputStream and a single BufferedInputStream to send and receive all data. 

Version B uses a PrintWriter and a BufferedReader to send and receive text, a BufferedOutputStream and a BufferedInputStream to send
and receive the request data byte array, and a DataOutputStream and a DataInputStream to send and receive the final result.

command line args 
arg 1 - ip address 
arg 2 - port 
arg 3 - Operator 
arg 4 >= Operands
