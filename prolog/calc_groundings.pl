% Script that outputs the results of a query given the source code
% Call using yap -l calc_groundings.yap -z "main('source_path',head,body,outfile)"
:- op(1000,xfx,['::']). % to support probabilistic facts
:- op(1149,xfx,['<-']). % to supprot annotated disjunctions

main(Source,Head,Body,Outfile) :-
	open(Outfile,'write',S),
	consult(Source),
	(
		call(Body),
		format(S,'~w\n',[Head]),
		fail
	;
		flush_output(S),
		close(S)
	).

% Calls the query and outputs all the results
call_body(Body) :-
	call(Body).
call_body(_) :-
	told,
	halt.

