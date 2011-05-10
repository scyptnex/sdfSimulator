# number of processors
param p, >0;

# Colocation factor
param C;

# number of actors
param n, >0;

# set of actors
set A := 1..n;

# set of processors
set P := 1..p;

# processor utilisation
param U{A}, >0;

# bandwidth between two actors
param BA{A,A}, >=0;

# bandwidth between two processors
param BP{P,P}, >=0;


# allocation of actor i to a processor j. If y[i,j] is
# one, then actor i is placed on processor j. 
var y{A,P}, binary;

# actor allocation 
subject to actor_allocation {i in A}:
    sum{j in P} y[i,j] =1;

# processor limit
subject to processor_limit {j in P}: 
    sum{i in A} U[i] * y[i,j] <= 1;
