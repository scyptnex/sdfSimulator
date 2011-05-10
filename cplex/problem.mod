/**
	Farhad is a legend

	Nic's magical IP/LP solver
**/

#objective constants
#param cm, >= 0;
#param cc, >= 0;


#Number of actors
param n, >0;

#number of processors
param p, >0;

#actors
set A := 1..n;

#processors
set P := 1..p;

#repetitions
#param R{A}, integer;


# runtime of an actor on a given processor 
param PI{A,P} >= 0;

# communication cost between two actors (units in bytes)
param CA{A,A} >= 0;

# communication bandwitdh between two processors (units in bytes per second)
param CP{P,P} >= 0;

param CAP{i in A, j in A, k in P, l in P} := CA[i,j] * CP[k,l];
 
#assignment matrix
var x{A,P}, binary;

#linearisation of term x[a,p] x[b,q]
var y{A,A,P,P}, binary;

#costs
param mks;

#target solution
minimize totalcost: 
     sum {i in A, j in A, k in P, l in P} CAP[i,j,k,l] * y[i,j,k,l]; 

# actor allocation 
subject to 

actor_allocation {i in A}:
    sum{j in P} x[i,j] =1;

#maximal process completion
make_span_constraint{j in P}: 
    sum {i in A} PI[i,j] * x[i,j] <= mks;

linear1{i in A, j in A, k in P, l in P}: 
    y[i,j,k,l] <= x[i,k];

linear2{i in A, j in A, k in P, l in P}: 
    y[i,j,k,l] <= x[j,l];

linear3{i in A, j in A, k in P, l in P}: 
    x[i,k] + x[j,l] -1 <= y[i,j,k,l];

data;

#param cm := 1.0;

#param cc := 1.0;

param n := 3;

param p := 2;

param mks := 10;

#param R :=
#  1 2
#  2 1
#  3 1
#;

param PI :
		1	2	:=
	1	0.2	0.3
	2	0.4	0.5
	3	0.5	0.4
;

param CP :
		1	2	:=
	1	0	0.3
	2	0.4	0
;

param CA :
		1	2	3	:=
	1	0	0.3	0.1
	2	0.4	0	1.4
	3	0.5	0.2	0
;

