/**
Farhad is a legend

Nic's magical IP/LP solver
**/

#Number of actors
param n, >0;

#number of processors
param p, >0;

param e, >0;

#actors
set A := 1..n;

#processors
set P := 1..p;

#edges
set E := 1..e;

#edges
set EDJ, dimen 2;

#Actor groupings, any actor in the same group cant be assigned to same processor
param AG{A,A}, integer;
#set AGRP, dimen 2;

# runtime of an actor on a given processor
param PI{A,P} >= 0;

#degree of an actor, number of edges incident to it
param PD{A}, integer;

# communication cost between two actors (units in bytes)
param CA{A,A} >= 0;

# communication bandwitdh between two processors (units in bytes per second)
param CP{P,P} >= 0;

#why is everything being counted twice?
#its still same optimal
param CAP{i in A, j in A, k in P, l in P} := CA[i,j] * CP[k,l] + PI[i,k]/PD[i] + PI[j,l]/PD[j];
 
#assignment matrix
var x{A,P} >= 0;

#linearisation of term x[a,p] x[b,q]
var y{A,A,P,P} >= 0;

#costs
#param mks;



#target solution
minimize totalcost:
	sum {(i,j) in EDJ, k in P, l in P} CAP[i,j,k,l] * y[i,j,k,l];
#	sum {(i,j) in EDJ, k in P, l in P} CAP[i,j,k,l] * (min(x[i,k], x[j,l]));



subject to

# actor allocation
actor_allocation {i in A}:
    sum{j in P} x[i,j] = 1;

#maximal process completion
#make_span_constraint{j in P}:
# sum {i in A} PI[i,j] * x[i,j] <= mks;

#quadapp{i in A, j in A, k in P, l in P}:
#	y[i,j,k,l] = (x[i,k]+x[j,l])/2;

quadapp2{i in A, j in A, k in P, l in P}:
	y[i,j,k,l] = +(x[i,k]-x[j,l])

#quad_a{i in A, j in A, k in P, l in P}:
#	x[i,k] <= y[i,j,k,l];

#quad_b{i in A, j in A, k in P, l in P}:
#	x[j,l] <= y[i,j,k,l];

#primary_pairing{i in A, j in A, k in P, l in P}:
#    y[i,j,k,l] <= x[i,k];

#secondary_pairing{i in A, j in A, k in P, l in P}:
#    y[i,j,k,l] <= x[j,l];

#sum_pairing{i in A, j in A, k in P, l in P}:
#    x[i,k] + x[j,l] -1 <= y[i,j,k,l];

non_overlap2{i in A, j in A, k in P}:
	AG[i,j]*(x[i,k]+x[j,k]) <= 1;

#non_overlap{i in A, j in A, k in P}:
#	AG[i,j]*y[i,j,k,k] = 0;

