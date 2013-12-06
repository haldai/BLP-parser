%%% -*- Mode: Prolog; -*-

:- module(problog_vc_from_dg, [input_dg_problog_node/1,
                               input_dg_problog_single_edge/1,
                               input_dg_problog_and_edge/1,
                               input_dg_problog_queries/1,
                               output_dg_problog_node/1,
                               output_dg_problog_single_edge/1,
                               output_dg_problog_and_edge/1,
                               output_dg_problog_queries/2,
                               problog_compact_dg/0,
                               remove_dg/0,
                               problog_vc_statistics/2,
                               problog_activate_compacting/5]).

%:- use_module(problog_dg, [problog_export_dg/0]).

:- use_module(problog_identities, [problog_define_identity/1,
                                   problog_current_identity/2,
                                   problog_next_identity/2,
                                   problog_reset_identity/1]).
:- use_module(library(aggregate), [aggregate_all/3]).
:- use_module(library(lists), [member/2, memberchk/2, delete/3, append/3, flatten/2, reverse/2]).
:- use_module(library(ordsets), [ord_intersection/4,
                                 ord_add_element/3,
                                 list_to_ord_set/2,
                                 ord_union/3]).

:- use_module(library(charsio), [term_to_atom/2]).

:- initialization(local_init).

local_init :-
  bb_put(graphnumber, 1),
  problog_define_identity('AND-cluster_counter'),
  problog_define_identity('OR-cluster_counter'),
  problog_define_identity(compacting_counter),
  problog_define_identity(compacting_branch_counter),
  problog_define_identity(compacting_single_head_counter).

input_dg_problog_node(Edge) :-
  recordz('problog_vc_from_dg:problog_node', Edge, _).
input_dg_problog_single_edge(Edge) :-
  recordz('problog_vc_from_dg:problog_single_edge', Edge, _).
input_dg_problog_and_edge(Edge) :-
  recordz('problog_vc_from_dg:problog_and_edge', Edge, _).
input_dg_problog_queries(Queries) :-
  recordz('problog_vc_from_dg:problog_queries', queries(Queries), _).

output_dg_problog_node(Edge) :-
  recorded('problog_vc_from_dg:problog_node', Edge, _).
output_dg_problog_single_edge(Edge) :-
  recorded('problog_vc_from_dg:problog_single_edge', Edge, _).
output_dg_problog_and_edge(Edge) :-
  recorded('problog_vc_from_dg:problog_and_edge', Edge, _).
output_dg_problog_queries(Query, Result) :-
  recorded('problog_vc_from_dg:problog_queries', query(Query, Result), _).

remove_dg :-
  eraseall('problog_vc_from_dg:problog_node'),
  eraseall('problog_vc_from_dg:problog_single_edge'),
  eraseall('problog_vc_from_dg:problog_and_edge'),
  eraseall('problog_vc_from_dg:problog_queries'),
  problog_reset_identity('AND-cluster_counter'),
  problog_reset_identity('OR-cluster_counter'),
  problog_reset_identity(compacting_counter),
  problog_reset_identity(compacting_branch_counter),
  problog_reset_identity(compacting_single_head_counter).

problog_vc_statistics(simple_compactings, Count_simple) :-
  problog_current_identity(compacting_counter, Count_simple).
problog_vc_statistics(branch_compactings, Count_branch) :-
  problog_current_identity(compacting_branch_counter, Count_branch).
problog_vc_statistics(head_compactings, Count_single_head) :-
  problog_current_identity(compacting_single_head_counter, Count_single_head).
problog_vc_statistics(and_cluster_compactings, Count_and) :-
  problog_current_identity('AND-cluster_counter', Count_and).
problog_vc_statistics(or_cluster_compactings, Count_or) :-
  problog_current_identity('OR-cluster_counter', Count_or).
problog_vc_statistics(dg_size, Size) :-
  count_dg_size(Size).
problog_vc_statistics(dg_nodes, NodesCount) :-
  count_dg_nodes(NodesCount).

count_dg_size(Size) :-
  aggregate_all(count, recorded('problog_vc_from_dg:problog_node', _, _), NodesCNT),
  aggregate_all(count, recorded('problog_vc_from_dg:problog_single_edge', _, _), SingleEdgesCNT),
  aggregate_all(count, recorded('problog_vc_from_dg:problog_and_edge', _, _), AndEdgesCNT),
  findall(ANDNode, recorded('problog_vc_from_dg:problog_and_edge', dg(_, ANDNode), _), ANDNodes),
  flatten(ANDNodes, ANDNodesFlatten),
  length(ANDNodesFlatten, ANDNodesCNT),
  Size is SingleEdgesCNT + NodesCNT + AndEdgesCNT + ANDNodesCNT,
  !.
count_dg_size(0).

count_dg_nodes(NodesCount):-
  aggregate_all(count, recorded('problog_vc_from_dg:problog_node', _, _), NodesCount).
  
:- dynamic active/1.

active(simple_compacting).
active(branch_compacting).
active(single_head_compacting).
active(and_cluster_compacting).
active(or_cluster_compacting).

problog_activate_compacting(Simple, Branch, Single_Head, AND, OR) :-
  retractall(active(_)),
  (Simple -> assertz(active(simple_compacting)) ; true),
  (Branch -> assertz(active(branch_compacting)) ; true),
  (Single_Head -> assertz(active(single_head_compacting)) ; true),
  (AND -> assertz(active(and_cluster_compacting)) ; true),
  (OR -> assertz(active(or_cluster_compacting)) ; true).

%
% Handling Dependency Graph Edges
%

problog_dg_node(Fact, Prob) :-
  recorded('problog_vc_from_dg:problog_node', dg(Fact, Prob), _).
problog_dg_node(not(Fact), NotProb) :-
  recorded('problog_vc_from_dg:problog_node', dg(Fact, Prob), _),
  NotProb is 1.0 - Prob.

retract_problog_dg_node(Fact, Prob) :-
  recorded('problog_vc_from_dg:problog_node', dg(Fact, Prob), Ref),
  !,
  erase(Ref).
retract_problog_dg_node(not(Fact), NotProb) :-
  recorded('problog_vc_from_dg:problog_node', dg(Fact, Prob), Ref),
  erase(Ref),
  NotProb is 1.0 - Prob.

/*negate_problog_dg_node(F, _NewF) :- 
  is_list(F),
  throw(error(negating_list)).
negate_problog_dg_node(F, NewF) :-
  negate_problog_dg_node_inner(F, NewF).
*/
negate_problog_dg_node(not(F), F) :- !.
negate_problog_dg_node(F, not(F)).

example:-
  recordz('problog_vc_from_dg:problog_node', dg(0,0.3), _),
  recordz('problog_vc_from_dg:problog_node', dg(1,0.3), _),
  recordz('problog_vc_from_dg:problog_node', dg(2,0.2), _),
  recordz('problog_vc_from_dg:problog_and_edge', dg(t(0),[t(1),t(3)]), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(q),t(0)), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(1),0), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(1),t(2)), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(2),1), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(2),2), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(t(3),1), _),
  recordz('problog_vc_from_dg:problog_queries', queries([t(q),t(2)]), _).

show:-
  recorded('problog_vc_from_dg:problog_node',Node,_),
  writeln(Node),
  fail.
show:-
  recorded('problog_vc_from_dg:problog_single_edge',Edge,_),
  writeln(Edge),
  fail.
show:-
  recorded('problog_vc_from_dg:problog_and_edge',AndEdge,_),
  writeln(AndEdge),
  fail.
show.

/*

:- dynamic '$problog_node'/2, '$problog_single_edge'/2, '$problog_and_edge'/2.

'$problog_node'(0,0.3).
'$problog_node'(1,0.3).
'$problog_node'(2,0.2).
'$problog_node'(3,0.3).
'$problog_node'(4,0.2).
'$problog_single_edge'(t(1),0).
'$problog_single_edge'(t(2),1).
'$problog_single_edge'(t(3),3).
'$problog_and_edge'(t(1),[2,t(2)]).
'$problog_and_edge'(t(2),[4,not(t(3))]).


problog_edge(node, F, P) :- '$problog_node'(F, P).
problog_edge(single, N1, N2) :- '$problog_single_edge'(N1, N2).
problog_edge(and, N, AND_List) :- '$problog_and_edge'(N, AND_List).

*/

%

loop.
loop :- loop.

problog_compact_dg :-
  loop,
  bb_put(compactings, false),
  do_compactings,
  %export_dg_to_consecutive_graph,
  do_and_compactings,
  %export_dg_to_consecutive_graph,
  do_or_compactings,
  %export_dg_to_consecutive_graph,
  bb_get(compactings, false),
  !,
  problog_current_identity(compacting_counter, Count_simple),
  write('Detected: '),
  write(Count_simple),
  writeln(' compressions.'),

  problog_current_identity(compacting_branch_counter, Count_branch),
  write('Detected: '),
  write(Count_branch),
  writeln(' branch compressions.'),

  problog_current_identity(compacting_single_head_counter, Count_single_head),
  write('Detected: '),
  write(Count_single_head),
  writeln(' single head compressions.'),

  problog_current_identity('AND-cluster_counter', Count_and),
  write('Detected: '),
  write(Count_and),
  writeln(' AND-clusters.'),

  problog_current_identity('OR-cluster_counter', Count_or),
  write('Detected: '),
  write(Count_or),
  writeln(' OR-clusters.').

do_compactings :-
  active(simple_compacting),
  detect_compacting_node(Node, Fact),
  \+ is_query(Node),
  problog_next_identity(compacting_counter, _),
  perform_compacting(Node, Fact),
  % check_queries(Node, Fact),
  bb_put(compactings, true),
  fail.
do_compactings :-
  active(branch_compacting),
  detect_compacting_branch(Node, List),
  %\+ contains_query(List),
  problog_next_identity(compacting_branch_counter, _),
  perform_compacting_branch(Node, List),
  % check_queries(Node, List),
  bb_put(compactings, true),
  fail.
do_compactings :-
  active(single_head_compacting),
  detect_compacting_single_head(Father, Child),
  % check_queries(Child), % Problem if it is an asked q need to fix that
  problog_next_identity(compacting_single_head_counter, _),
  perform_compacting_single_head(Father, Child),
  bb_put(compactings, true),
  fail.
do_compactings.

do_and_compactings :-
  active(and_cluster_compacting),
  detect_compacting_AND_node(ANDClusters),
  perform_compacting_AND(ANDClusters),
  ANDClusters \== [],
  increase_counter('AND-cluster_counter', ANDClusters),
  bb_put(compactings, true),
  fail.
do_and_compactings.

do_or_compactings :-
  active(or_cluster_compacting),
  detect_compacting_OR_node(ORClusters),
  perform_compacting_OR(ORClusters),
  ORClusters \== [],
  increase_counter('OR-cluster_counter', ORClusters),
  bb_put(compactings, true),
  fail.
do_or_compactings :- 
  reset_nodes.

increase_counter(_Identifier, []).
increase_counter(Identifier, [_|T]) :-
  problog_next_identity(Identifier, _),
  increase_counter(Identifier, T).

is_query(Node) :-
  recorded('problog_vc_from_dg:problog_queries', queries(Queries), _),
  memberchk(Node, Queries), !.
is_query(not(Node)) :-
  recorded('problog_vc_from_dg:problog_queries', queries(Queries), _),
  memberchk(Node, Queries).

contains_query([]).
contains_query([Q|Rest]):-
  is_query(Q),
  contains_query(Rest).
  
check_queries(Node, Fact) :-
  recorded('problog_vc_from_dg:problog_queries', queries(Queries), _),
  memberchk(Node, Queries),
  recordz('problog_vc_from_dg:problog_queries', query(Node, Fact), _),
  fail.
check_queries(_, _).

check_queries(Node) :-
  recorded('problog_vc_from_dg:problog_queries', queries(Queries), _),
  memberchk(Node, Queries),
  copy_query(Node),
  recordz('problog_vc_from_dg:problog_queries', query(Node, copy(Node)), _),
  fail.
check_queries(_).

copy_query(Node) :-
  copy_query_and(Node),
  copy_query_single(Node).

copy_query_and(Node) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(Node, List), _),
  recordz('problog_vc_from_dg:problog_and_edge', dg(copy(Node), List), _),
  fail.
copy_query_and(_Node).

copy_query_single(Node) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Fact), _),
  recordz('problog_vc_from_dg:problog_single_edge', dg(copy(Node), Fact), _),
  fail.
copy_query_single(_Node).

%
% Detecting Compacting Node
%

detect_compacting_branch(Node, List) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(Node, List), Ref1),
  \+ check_negated(Node),
  \+ is_query(Node),
  \+ recorded('problog_vc_from_dg:problog_single_edge', dg(Node, _), _),
  findall(Ref, node_appears_in(Node, Ref), ChildRefs),
  ChildRefs \== [],
  findall(Ref2, recorded('problog_vc_from_dg:problog_and_edge', dg(Node, _), Ref2), Refs),
  Refs == [Ref1],
  erase(Ref1).

check_negated(not(_Node)) :- !.
check_negated(Node) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(_, not(Node)), _),
  !.
check_negated(Node) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(_, List), _),
  memberchk(not(Node), List).

perform_compacting_branch(Node, Child) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Father, Node), Ref1),
  erase(Ref1),
  recordz('problog_vc_from_dg:problog_and_edge', dg(Father, Child), _),
  fail.
perform_compacting_branch(Node, Child) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(Father, List), Ref1),
  replace_node_by_fact(Node, Child, List, FinalList),
  erase(Ref1),
  recordz('problog_vc_from_dg:problog_and_edge', dg(Father, FinalList), _),
  fail.
perform_compacting_branch(_, _).

detect_compacting_single_head(Father, Child) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Father, Child), Ref1),
  \+ check_negated(Child),
  \+ is_query(Father),
  node_is_head(Child),
  findall(Ref, node_appears_in(Child, Ref), Refs),
  Refs == [Ref1],
  erase(Ref1).

node_is_head(Node) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, _), _), !.
node_is_head(Node) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(Node, _), _), !.

node_appears_in(Node, Ref) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(_, Node), Ref).
node_appears_in(Node, Ref) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(_, not(Node)), Ref).
node_appears_in(Node, Ref) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(_, List), Ref),
  memberchk(Node, List).
node_appears_in(Node, Ref) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(_, List), Ref),
  memberchk(not(Node), List).

perform_compacting_single_head(Father, Child) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Child, Node), Ref1),
  erase(Ref1),
  recordz('problog_vc_from_dg:problog_single_edge', dg(Father, Node), _),
  fail.
perform_compacting_single_head(Father, Child) :-
  recorded('problog_vc_from_dg:problog_and_edge', dg(Child, List), Ref1),
  erase(Ref1),
  recordz('problog_vc_from_dg:problog_and_edge', dg(Father, List), _),
  fail.
perform_compacting_single_head(_, _).


detect_compacting_node(Node, Fact) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Fact), _),
  problog_dg_node(Fact, _Prob),
  \+ recorded('problog_vc_from_dg:problog_and_edge', dg(Node, _), _),
  \+ is_query(Node),
  findall(Node, recorded('problog_vc_from_dg:problog_single_edge', dg(Node, _), _), Nodes),
  Nodes == [Node].

% Something fishy here, check the code again.
perform_compacting(Node, Fact) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Fact), Ref1),
  erase(Ref1),
  recorded('problog_vc_from_dg:problog_single_edge', dg(Depended, Node), Ref2),
  erase(Ref2),
  recordz('problog_vc_from_dg:problog_single_edge', dg(Depended, Fact), _),
  fail ;
  recorded('problog_vc_from_dg:problog_and_edge', dg(Depended, List), _),
  replace_node_by_fact(Node, Fact, List, FinalList),
  recordz('problog_vc_from_dg:problog_and_edge', dg(Depended, FinalList), _),
  fail ;
  true.

replace_node_by_fact(Node, Fact, List, FinalList) :-
  replace_node_by_fact_inner(Node, Fact, List, FinalList).
replace_node_by_fact(Node, Fact, List, FinalList) :-
  negate_problog_dg_node(Node, NotNode),
  negate_problog_dg_node(Fact, NotFact),
  replace_node_by_fact_inner(NotNode, NotFact, List, FinalList).

replace_node_by_fact_inner(Node, Fact, List, FinalList) :-
  memberchk(Node, List),
  delete(List, Node, NewList),
  recorded('problog_vc_from_dg:problog_and_edge', dg(_Depended, List), Ref),
  erase(Ref),
  union(Fact, NewList, FinalList).

union(Fact, List, NewList) :-
  is_list(Fact), !,
  ord_union(List, Fact, NewList).
union(Fact, List, NewList) :-
  ord_add_element(List, Fact, NewList).

%
% OR Node 
%

get_node(Node) :-
  bb_get('problog_vc_from_dg:nodes', Nodes),
  !,
  member(Node, Nodes).
get_node(Node) :-
  all(Node, recorded('problog_vc_from_dg:problog_single_edge', dg(Node, _), _), Nodes),
  bb_put('problog_vc_from_dg:nodes', Nodes),
  member(Node, Nodes).

reset_nodes :-
  bb_get('problog_vc_from_dg:nodes', _Nodes),
  !,
  bb_delete('problog_vc_from_dg:nodes', _).
reset_nodes.

get_or_list(Node, OR) :-
  findall(Leaf, (recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Leaf), _), problog_dg_node(Leaf, _)), List),
  list_to_ord_set(List, OR).

%
% Detect OR-clusters
%

detect_compacting_OR_node(ORClusters) :-
  get_node(Node),
  get_or_list(Node, OR),
  length(OR, Len),
  Len > 1,
  refine_OR-cluster_from_ands(OR, OR1),
  refine_cluster_from_negation(OR1, OR2),
  refine_cluster_from_queries(OR2, OR3),
  refine_OR-cluster(OR3, ORClusters).


refine_OR-cluster_from_ands(OR, _FinalOR) :-
  bb_put('problog_vc_from_dg:OR_Cluster', OR),
  recorded('problog_vc_from_dg:problog_and_edge', dg(_, List), _),
  bb_get('problog_vc_from_dg:OR_Cluster', Cluster),
  refine_OR-cluster_from_ands(List, Cluster, NewCluster),
  bb_put('problog_vc_from_dg:OR_Cluster', NewCluster),
  fail.
/*refine_OR-cluster_from_ands(OR, _FinalOR) :-
  bb_put('problog_vc_from_dg:OR_Cluster', OR),
  recorded('problog_vc_from_dg:problog_queries', query(_Node, List), _),
  is_list(List),
  bb_get('problog_vc_from_dg:OR_Cluster', Cluster),
  refine_OR-cluster_from_ands(List, Cluster, NewCluster),
  bb_put('problog_vc_from_dg:OR_Cluster', NewCluster),
  fail.*/
refine_OR-cluster_from_ands(_OR, FinalOR) :-
  bb_get('problog_vc_from_dg:OR_Cluster', FinalOR).

refine_OR-cluster_from_ands(_, [], []).
refine_OR-cluster_from_ands(List, [F|OR], FinalOR) :-
  memberchk(F, List),
  !,
  refine_OR-cluster_from_ands(List, OR, FinalOR).
refine_OR-cluster_from_ands(List, [F|OR], [F|FinalOR]) :-
  refine_OR-cluster_from_ands(List, OR, FinalOR).

refine_OR-cluster(OR, _FinalOR) :-
  bb_put('problog_vc_from_dg:OR-Clusters', [OR]),
  get_node(Node),
  get_or_list(Node, List),
  bb_get('problog_vc_from_dg:OR-Clusters', Clusters),
  apply_to_all(Clusters, List, NewClusters),
  bb_put('problog_vc_from_dg:OR-Clusters', NewClusters),
  fail.
refine_OR-cluster(_OR, FinalOR) :-
  bb_get('problog_vc_from_dg:OR-Clusters', FinalOR).

%
% Detect AND-clusters
%

detect_compacting_AND_node(ANDClusters) :- 
  recorded('problog_vc_from_dg:problog_and_edge', dg(_Node, List), _),
  findall(Leaf, (member(Leaf, List), problog_dg_node(Leaf, _Prob)), AND),
  length(AND, Len),
  Len > 1,
  'refine_AND-cluster_from_singles'(AND, AND1),
  refine_cluster_from_negation(AND1, AND2),
  refine_cluster_from_queries(AND2, AND3),
  'refine_AND-cluster'(AND3, ANDClusters).

'refine_AND-cluster_from_singles'(AND, _FinalAND) :-
  bb_put('problog_vc_from_dg:AND_Cluster', AND),
  recorded('problog_vc_from_dg:problog_single_edge', dg(_Node, Term), _),
  bb_get('problog_vc_from_dg:AND_Cluster', Cluster),
  memberchk(Term, Cluster),
  delete(Cluster, Term, NewAND),
  bb_put('problog_vc_from_dg:AND_Cluster', NewAND),
  fail.
/*'refine_AND-cluster_from_singles'(AND, _FinalAND) :-
  bb_put('problog_vc_from_dg:AND_Cluster', AND),
  recorded('problog_vc_from_dg:problog_queries', query(_Node, Term), _),
  \+ is_list(Term),
  bb_get('problog_vc_from_dg:AND_Cluster', Cluster),
  memberchk(Term, Cluster),
  delete(Cluster, Term, NewAND),
  bb_put('problog_vc_from_dg:AND_Cluster', NewAND),
  fail.*/
'refine_AND-cluster_from_singles'(_AND, FinalAND) :-
  bb_get('problog_vc_from_dg:AND_Cluster', FinalAND).

'refine_AND-cluster'(AND, _FinalAND) :-
  bb_put('problog_vc_from_dg:AND-Clusters', [AND]),
  recorded('problog_vc_from_dg:problog_and_edge', dg(_Node, List), _),
  bb_get('problog_vc_from_dg:AND-Clusters', Clusters),
  apply_to_all(Clusters, List, NewClusters),
  bb_put('problog_vc_from_dg:AND-Clusters', NewClusters), 
  fail.
/*'refine_AND-cluster'(AND, _FinalAND) :-
  bb_put('problog_vc_from_dg:AND-Clusters', [AND]),
  recorded('problog_vc_from_dg:problog_queries', query(_Node, List), _),
  is_list(List),
  bb_get('problog_vc_from_dg:AND-Clusters', Clusters),
  apply_to_all(Clusters, List, NewClusters),
  bb_put('problog_vc_from_dg:AND-Clusters', NewClusters),
  fail.*/
'refine_AND-cluster'(_AND, FinalAND) :- 
  bb_get('problog_vc_from_dg:AND-Clusters', FinalAND).

refine_cluster_from_queries(Cluster, Final):-
  refine_cluster_from_queries(Cluster, [], FinalNotReversed),
  reverse(FinalNotReversed, Final).
refine_cluster_from_queries([], Final, Final).
refine_cluster_from_queries([Node| Rest], AccCluster, Final):-
  is_query(Node), !,
  refine_cluster_from_queries(Rest, AccCluster, Final).
refine_cluster_from_queries([Node| Rest], AccCluster, Final):-
  refine_cluster_from_queries(Rest, [Node | AccCluster], Final).

%
% Common Predicates
%

refine_cluster_from_negation([], []).
refine_cluster_from_negation([F|AND], FinalAND) :-
  negate_problog_dg_node(F, Not_F),
  once(recorded('problog_vc_from_dg:problog_single_edge', dg(Not_F, _), _)),
  !,
  refine_cluster_from_negation(AND, FinalAND).
refine_cluster_from_negation([F|AND], FinalAND) :-
  negate_problog_dg_node(F, Not_F),
  once(recorded('problog_vc_from_dg:problog_single_edge', dg(_, Not_F), _)),
  !,
  refine_cluster_from_negation(AND, FinalAND).
refine_cluster_from_negation([F|AND], FinalAND) :-
  negate_problog_dg_node(F, Not_F),
  once(recorded('problog_vc_from_dg:problog_and_edge', dg(Not_F, _), _)),
  !,
  refine_cluster_from_negation(AND, FinalAND).
refine_cluster_from_negation([F|AND], FinalAND) :-
  negate_problog_dg_node(F, Not_F),
  once(recorded('problog_vc_from_dg:problog_and_edge', dg(_, List), _)),
  memberchk(Not_F, List),
  !,
  refine_cluster_from_negation(AND, FinalAND).
refine_cluster_from_negation([F|AND], [F|FinalAND]) :-
  refine_cluster_from_negation(AND, FinalAND).

apply_to_all(Clusters, List, NewClusters) :-
  apply_to_all(Clusters, List, [], NewClusters).

apply_to_all([], _List, Acc, Acc).
apply_to_all([AND|Rest], List, Acc, FinalClusters) :-
  ord_intersection(List, AND, SUBAND1, SUBAND2),
  keep_possible_clusters([SUBAND1,SUBAND2], Clusters),
  /* improvable ? */
  append(Clusters, Acc, NewAcc),
  apply_to_all(Rest, List, NewAcc, FinalClusters).

keep_possible_clusters([], []).
keep_possible_clusters([H|T], [H|R]) :-
  length(H, Len),
  Len > 1, !,
  keep_possible_clusters(T, R).
keep_possible_clusters([_H|T], Result) :-
  keep_possible_clusters(T, Result).

%
% Compact OR-clusters
%

perform_compacting_OR([]).
perform_compacting_OR([OR|Clusters]) :-
  perform_compacting_OR_inner(OR),
  perform_compacting_OR(Clusters).

perform_compacting_OR_inner(OR) :-
  calculate_OR_probability(OR, Prob),
  term_to_atom(OR, AtomOR),
  recordz('problog_vc_from_dg:problog_node', dg(or(AtomOR), Prob), _),
  get_node(Node),
  check_contains_cluster(Node, OR),
  retract_OR_members(Node, OR),
  recordz('problog_vc_from_dg:problog_single_edge', dg(Node, or(AtomOR)), _),
  fail.
perform_compacting_OR_inner(_OR).

check_contains_cluster(Node, OR) :-
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Fact), _),
  memberchk(Fact, OR),
  !.

retract_OR_members(Node, ORCluster) :-
  member(Fact, ORCluster),
  recorded('problog_vc_from_dg:problog_single_edge', dg(Node, Fact), Ref),
  erase(Ref),
  fail.
retract_OR_members(_Node, _ORCluster).

calculate_OR_probability(OR, Prob) :-
  calculate_OR_probability(OR, 0.0, Prob).
calculate_OR_probability([], Prob, Prob).
calculate_OR_probability([F|OR], Acc, FinalProb) :-
  retract_problog_dg_node(F, P),
  NewAcc is P + (1 - P) * Acc,
  calculate_OR_probability(OR, NewAcc, FinalProb).

%
% Compact AND-clusters
%

perform_compacting_AND(ANDClusters) :-
  perform_compacting_AND_inner(ANDClusters),
  fail.
perform_compacting_AND(_ANDClusters).

perform_compacting_AND_inner([]) :-
  recorded('problog_vc_from_dg:scheduler', do(Call), Key),
  erase(Key),
  call(Call),
  fail.
perform_compacting_AND_inner([]).
perform_compacting_AND_inner([AND|Clusters]) :-
  calculate_AND_probability(AND, Prob),
  term_to_atom(AND, AtomAND),
  recordz('problog_vc_from_dg:problog_node', dg(and(AtomAND), Prob), _),
  recorded('problog_vc_from_dg:problog_and_edge', dg(Node, List), Ref),
  delete_AND_from_list(List, AND, NewList),
  List \== NewList,
  add_AND_to_list(NewList, AtomAND, FinalList),
  recordz('problog_vc_from_dg:scheduler', do(erase(Ref)), _),
  (is_list(FinalList) ->
    recordz('problog_vc_from_dg:scheduler', do(recordz('problog_vc_from_dg:problog_and_edge', dg(Node, FinalList), _)), _)
  ;
    recordz('problog_vc_from_dg:scheduler', do(recordz('problog_vc_from_dg:problog_single_edge', dg(Node, FinalList), _)), _)
  ),
  (fail ; perform_compacting_AND_inner(Clusters)).

calculate_AND_probability(AND, Prob) :-
  calculate_AND_probability(AND, 1.0, Prob).
calculate_AND_probability([], Acc, Acc).
calculate_AND_probability([F|R], Acc, FinalProb) :-
  retract_problog_dg_node(F, P),
  Prob is Acc * P,
  calculate_AND_probability(R, Prob, FinalProb).

delete_AND_from_list(List, [], List).
delete_AND_from_list(List, [E|T], FinalList) :-
  delete(List, E, NewList),
  delete_AND_from_list(NewList, T, FinalList).

add_AND_to_list([], AND, and(AND)).
add_AND_to_list([H|T], AND, Set) :-
  ord_add_element([H|T], and(AND), Set).

/*works only for metaproblog
export_dg_to_consecutive_graph:-
  bb_get(graphnumber, OutGraphNumber),
  atom_number(OutGraphNumberAtom, OutGraphNumber),
  atom_concat(['~/a', OutGraphNumberAtom, '.dot'], OutGraphName),
  open(OutGraphName, write, S1), set_output(S1),
  problog_export_dg,
  close(S1),
  OutGraphNumber1 is OutGraphNumber + 1,
  bb_put(graphnumber, OutGraphNumber1).*/
