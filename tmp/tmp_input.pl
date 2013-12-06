1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)), \+(spoc(X_2,s13_SPOC)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s13_SPOC)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s7_SPOC)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_2,c成语_CLS)).
1.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_2,c成语_CLS)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s13_SPOC)), \+(class(X_1,c城市_CLS)), \+(spoc(X_2,s15_SPOC)).
0.3333333333333333::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s13_SPOC)), \+(class(X_1,c城市_CLS)),(spoc(X_2,s15_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s13_SPOC)),(class(X_1,c城市_CLS)), \+(class(X_2,c地名_CLS)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s13_SPOC)),(class(X_1,c城市_CLS)),(class(X_2,c地名_CLS)).
0.09090909090909091::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s13_SPOC)), \+(postag(X_1,nr_POS)), \+(class(X_1,c四川_CLS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s13_SPOC)), \+(postag(X_1,nr_POS)),(class(X_1,c四川_CLS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s13_SPOC)),(postag(X_1,nr_POS)), \+(class(X_2,c电影_CLS)).
0.0::(pc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_1,c都演过什么_CLS)).
1.0::(pc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(class(X_1,c都演过什么_CLS)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s7_SPOC)), \+(class(X_1,c电视剧_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s7_SPOC)),(class(X_1,c电视剧_CLS)).
0.75::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s7_SPOC)), \+(spoc(X_1,s16_SPOC)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s7_SPOC)),(spoc(X_1,s16_SPOC)).
0.4::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(class(X_1,c电视剧_CLS)), \+(class(X_1,c地名_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(class(X_1,c电视剧_CLS)),(class(X_1,c地名_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)),(class(X_1,c电视剧_CLS)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_2,c结尾_CLS)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(class(X_2,c结尾_CLS)).
1.0::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_1,s5_SPOC)).
0.0::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_1,s5_SPOC)).
0.0::(sp(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)).
1.0::(sp(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)).
1.0::(sp(X_1,X_2)):- (adv(X_2,X_1)), \+(==(X_2,X_1)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_2,c主演过_CLS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(class(X_2,c主演过_CLS)).
0.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_3,c地名_CLS)).
1.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_3,c地名_CLS)).
1.0::(co(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)).

att(u主演过_2_n,u邓萃雯_1_n).
att(u电视剧_4_n,u主演过_2_n).
qun(u电视剧_4_n,u多少_3_r).
postag(u邓萃雯_1_n,n_0_POS).
class(u邓萃雯_1_n,c演员_0_CLS).
spoc(u邓萃雯_1_n,s5_0_SPOC).
postag(u主演过_2_n,n_0_POS).
class(u主演过_2_n,c主演过_0_CLS).
spoc(u主演过_2_n,s2_0_SPOC).
postag(u多少_3_r,r_0_POS).
class(u多少_3_r,c多少_0_CLS).
spoc(u多少_3_r,s16_0_SPOC).
postag(u电视剧_4_n,n_0_POS).
class(u电视剧_4_n,c电视剧_0_CLS).
spoc(u电视剧_4_n,s13_0_SPOC).

query(sp(_,_)).
query(op(_,_)).
query(oc(_,_)).
query(cp(_,_)).
query(ps(_,_)).
query(pc(_,_)).
