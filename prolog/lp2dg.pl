/**********************************************************************
                From Logic Program to Dependency Graph
Constructs a dependency graph representing a ground logic program.
Also, it compacts the graph. A preprocessing step after grounting and
before conversion to CNF. 

Input: a ground program
Output: a dependency graph / textual representation

Algorithm steps:
1) generate the graph
2) save to output file

Author: Dimitar Sht. Shterionov, Theofrastos Mantadelis.
***********************************************************************/

:- use_module(library(ordsets)).
:- use_module(problog_vc_from_dg).

:- op(200,xfx,['::']). % to support probabilistic facts

main :- 
    current_prolog_flag(argv, ARGS),
    (ARGS = [InFile, OutFile] ->
      generate_dg(InFile,OutFile)
    ;
      (ARGS = [InFile, OutFile, StatFile, AtomQueries] ->
        compact(InFile, OutFile, StatFile, AtomQueries)
      ;
        halt(5)
      )
    ).


/*Generation of a dependency graph - initialization predicate*/
generate_dg(InFile, OutFile) :-
        to_dependency_graph(InFile),
        save_to_dg_file(OutFile), 
        halt.

/*Generation and compating of a dependency graph - initialization predicate*/
compact(InFile, OutFile, StatFile, AtomQueries) :-
        to_dependency_graph(InFile),
        open(StatFile, 'write', OF),
        problog_vc_statistics(dg_size, InitSize),
        term_to_atom(Queries, AtomQueries),
        input_dg_problog_queries(Queries),
        problog_compact_dg,
        save_to_dg_file(OutFile),
        write(OF, dg_initial_size-InitSize), nl(OF),
        forall(problog_vc_statistics(Stat, Result),
                (write(OF, Stat-Result), nl(OF))),
        close(OF),
        remove_dg, 
        halt.

/*Opens and reads a file to get the ground program. Reads it line by line. 
Every line is processed and the terms are added to the dependency graph.*/
to_dependency_graph(File) :-
    seeing(Old),
    see(File),
    read_print_file,
    see(Old).
    
read_print_file :-
    read(Data), 
    Data \= end_of_file, !,
    add_to_graph(Data),
    read_print_file.
read_print_file:-
    seen.

/*Adding a term to the dependency graph has three cases: 
a) a probabilistic fact; b) Rule; c) non probabilistic fact.
For a rule its body is processed and an edge (and edge for compount body
or single edge for Body of one atom) is added*/

add_to_graph((Prob::Fact)) :- !,
  input_dg_problog_node(dg(Fact, Prob)).
add_to_graph((Head :- Body)) :- !,
  handle_and_body(Head, Body, BodyList, false),
  list_to_ord_set(BodyList, BodySet),
  (BodySet \= [] ->
    input_dg_edge(Head, BodySet)
  ;
    true
  ).
add_to_graph((Fact)):-
  input_dg_problog_node(dg(Fact, 1.0)).

/*Creates a new edge in the graph*/
input_dg_edge(Head, [Body]):- !,
   input_dg_problog_single_edge(dg(Head, Body)).
input_dg_edge(Head, Body):-
    input_dg_problog_and_edge(dg(Head, Body)).

/*Processing the body to a set of its atoms. Handles nested parts
and disjunctions: e.g. (a, b), c or (a;b), c*/
/*handle_body((R, '\\+'(F1, F2)), Set, NestedNodes, AccNestedNodes) :-!, 
  generate_new_head((F1, F2), Head), 
  handle_body((R, '\\+'Head), Set, NestedNodes, [(Head :- (F1, F2))|AccNestedNodes]).
handle_body(('\\+'(F1, F2), R), Set, NestedNodes, AccNestedNodes) :-!, 
  generate_new_head((F1, F2), Head), 
  handle_body(('\\+'Head, R), Set, NestedNodes, [(Head :- (F1, F2))|AccNestedNodes]).
handle_body((R, (F1; F2)), Set, NestedNodes, AccNestedNodes) :-!, 
  generate_new_head((F1, F2), Head), 
  handle_body((R, Head), Set, NestedNodes, [(Head :- F1), (Head :- F2)|AccNestedNodes]).
handle_body(((F1; F2), R), Set, NestedNodes, AccNestedNodes) :-!, 
  generate_new_head((F1, F2), Head), 
  handle_body((Head, R), Set, NestedNodes, [(Head :- F1), (Head :- F2)|AccNestedNodes]).
handle_body((R, '\\+' (F1; F2)), Set, NestedNodes, AccNestedNodes) :-!, 
  generate_new_head((F1, F2), Head), 
  handle_body((R, '\\+'Head), Set, [(Head :- F1), (Head :- F2)|AccNestedNodes]).
handle_body(('\\+' (F1; F2), R), Set, NestedNodes, AccNestedNodes):-!, 
  generate_new_head((F1, F2), Head), 
  handle_body(('\\+'Head, R), Set, [(Head :- F1), (Head :- F2)|AccNestedNodes]).*/
  
/*handle_body((F;R), Head):-!,
    generate_new_head((F;R), Head),
    add_to_graph((Head :- F)),
    add_to_graph((Head :- R)).
handle_body((F, R), Set):-!,
    handle_and_body((F, R), List),
    list_to_ord_set(List, Set).
handel_body('\\+'Body, not(Head)):-
    decomposable(Body),!,
    generate_new_head(Body, Head),
    add_to_graph((Head :- Body)).
handle_body('\\+'Body, not(Body)):-!.
handle_body(Body, Body).
*/
handle_and_body(ParentHead, ('\\+'F, R), [not(Head)|Rest], _Nested):-
    decomposable(F), !,
    generate_new_head(F, Head),
    add_to_graph((Head :- F)),
    handle_and_body(ParentHead, R, Rest, true).    
handle_and_body(ParentHead, ('\\+'F, R), [not(F)|Rest], Nested):-!,
    handle_and_body(ParentHead, R, Rest, Nested).

handle_and_body(_ParentHead, (F;R), [Head], true):-!,
    generate_new_head((F;R), Head),
    add_to_graph((Head :- F)),
    add_to_graph((Head :- R)). 
handle_and_body(ParentHead, (F;R), [], false):-!,
    add_to_graph((ParentHead :- F)),
    add_to_graph((ParentHead :- R)). 
handle_and_body(ParentHead, (F, R), [Head|Rest], _Nested):-
    decomposable(F), !,
    generate_new_head(F, Head),
    add_to_graph((Head :- F)),
    handle_and_body(ParentHead, R, Rest, true).    

handle_and_body(ParentHead, (F, R), [F|Rest], Nested) :- !, 
    handle_and_body(ParentHead, R, Rest, Nested).
handle_and_body(_, '\\+'F, [not(Head)], _):-
    decomposable(F), !,
    generate_new_head(F, Head),
    add_to_graph((Head :- F)).
handle_and_body(_, F, [Head], _):-
    decomposable(F), !,
    generate_new_head(F, Head),
    add_to_graph((Head :- F)).
handle_and_body(_, '\\+'F, [not(F)], _):-!.
handle_and_body(_, F, [F], _).


/*In case the body contains nested atoms for each nested pair a new rule is generated.*/
decomposable((_A,_B)).
decomposable((_A;_B)).

get_head_counter(Count):-
  bb_get(aux_head, Count), !,
  CountNew is Count+1,
  bb_put(aux_head, CountNew).
get_head_counter(0):-
  bb_put(aux_head, 1).

generate_new_head(Body, Head):-
  recorded(body_head, [Body, Head], _).
generate_new_head(Body, Head):-
  get_head_counter(HeadCount),
  Head = aux_head(HeadCount),
  recordz(body_head, [Body, Head], _).

  
/*Outputs facts to a prolog file. ProbLog formatted*/        
write_facts_to_prolog_file([], _).
write_facts_to_prolog_file([(Fact, Prob)|Rest], OF):-
        writeq(OF, Prob), write(OF, '::'), writeq(OF, Fact), write(OF, '.'), nl(OF),
        write_facts_to_prolog_file(Rest, OF).
write_rules_to_prolog_file([], _).
write_rules_to_prolog_file([(Head, Body)|Rest], OF):-
        writeq(OF, Head), write(OF, ' :- '), 
        write_body_to_prolog_file(Body, OF),
        write_rules_to_prolog_file(Rest, OF).

/*Saves the dependency graph to output file. It collects all
Node-Edge combinations (Probabilistic facts, And Edges, Simple edges)
and outputs them to OutFile*/
save_to_dg_file(OutFile) :-
        findall((Fact1, Prob), output_dg_problog_node(dg(Fact1, Prob)), ProbLogNodes),
        findall((Fact2, AndList), output_dg_problog_and_edge(dg(Fact2, AndList)), ProbLogAndEdges),
        findall((Fact3, Edge), output_dg_problog_single_edge(dg(Fact3, Edge)), ProbLogSingleEdges),
        open(OutFile, 'write', OF),
        export_python_dg(ProbLogNodes, 'problog_node', OF),
        export_python_dg(ProbLogAndEdges, 'problog_and_edge', OF), 
        export_python_dg(ProbLogSingleEdges, 'problog_single_edge', OF),
        close(OF).


/*Writes terms as a block to the OF stream. The block uses %.# as delimiter*/
export_python_dg([], _, _).
export_python_dg([(A, B)|Rest], Pred, OF) :-
        write(OF, '%.#'), nl(OF),
        writeq(OF, A), nl(OF),
        writeq(OF, Pred), nl(OF),
        write_body_to_dg_file(B, OF),
        write(OF, '#.%'), nl(OF),
        export_python_dg(Rest, Pred, OF).

write_body_to_dg_file([], _):-!.
write_body_to_dg_file([S], OF):-!,
        writeq(OF, S), nl(OF).
write_body_to_dg_file([F, S|Rest], OF):-!,
        writeq(OF, F), nl(OF),
        write_body_to_dg_file([S|Rest], OF).
write_body_to_dg_file(SingleFact, OF):-!,
        writeq(OF, SingleFact), nl(OF).

:- main.
