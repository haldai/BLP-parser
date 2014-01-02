0.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s2_SPOC)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(spoc(X_1,s1_SPOC)).
0.9919354838709677::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)), \+(spoc(X_2,s8_SPOC)), \+(postag(X_1,ns_POS)).
0.971830985915493::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,ns_POS)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)),(spoc(X_2,s8_SPOC)), \+(wrd(X_2,u演员_WRD)).
0.75::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)),(spoc(X_2,s8_SPOC)),(wrd(X_2,u演员_WRD)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)), \+(class(X_2,c演员_CLS)).
0.75::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)),(class(X_2,c演员_CLS)).
0.42857142857142855::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)), \+(spoc(X_2,s4_SPOC)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)),(spoc(X_2,s4_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)), \+(postag(X_1,n_POS)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)), \+(spoc(X_2,s4_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)),(spoc(X_2,s4_SPOC)), \+(spoc(X_2,s1_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)),(spoc(X_2,s4_SPOC)),(spoc(X_2,s1_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s1_SPOC)), \+(spoc(X_1,s4_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s1_SPOC)),(spoc(X_1,s4_SPOC)).
0.7692307692307693::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)), \+(postag(X_1,nr_POS)), \+(class(X_1,c词汇_CLS)).
0.23076923076923078::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)), \+(postag(X_1,nr_POS)),(class(X_1,c词汇_CLS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)),(postag(X_1,nr_POS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)), \+(spoc(X_2,s2_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)),(spoc(X_2,s2_SPOC)), \+(wrd(X_2,u演员_WRD)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)),(spoc(X_2,s2_SPOC)),(wrd(X_2,u演员_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_2,s2_SPOC)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)), \+(postag(X_1,b_POS)), \+(postag(X_1,v_POS)).
0.8::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)), \+(postag(X_1,b_POS)),(postag(X_1,v_POS)), \+(class(X_1,c词汇_CLS)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)), \+(postag(X_1,b_POS)),(postag(X_1,v_POS)),(class(X_1,c词汇_CLS)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,b_POS)), \+(wrd(X_2,u近义词_WRD)), \+(wrd(X_2,u女朋友_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,b_POS)), \+(wrd(X_2,u近义词_WRD)),(wrd(X_2,u女朋友_WRD)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,b_POS)),(wrd(X_2,u近义词_WRD)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u偶像_WRD)),(spoc(X_2,s1_SPOC)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u偶像_WRD)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)), \+(wrd(X_1,u演员_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)),(wrd(X_1,u演员_WRD)).
