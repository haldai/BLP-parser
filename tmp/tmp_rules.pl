0.0::(po(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)), \+(spoc(X_2,s2_SPOC)).
0.0::(po(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s2_SPOC)),(class(X_1,c电视剧_CLS)).
1.0::(po(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)), \+(class(X_1,c电视剧_CLS)).
1.0::(po(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)),(class(X_1,c电视剧_CLS)), \+(class(X_2,c出演_CLS)).
0.0::(po(X_1,X_2)):- (vob(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)),(class(X_1,c电视剧_CLS)),(class(X_2,c出演_CLS)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)), \+(class(X_2,c籍贯_CLS)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(class(X_2,c籍贯_CLS)), \+(postag(X_1,ns_POS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(class(X_2,c籍贯_CLS)),(postag(X_1,ns_POS)).
0.09090909090909091::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(class(X_1,c界_CLS)), \+(postag(X_2,l_POS)).
0.75::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(class(X_1,c界_CLS)),(postag(X_2,l_POS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)),(class(X_1,c界_CLS)).
0.0::(co(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s8_SPOC)).
1.0::(co(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s7_SPOC)), \+(class(X_1,c演员_CLS)).
0.0::(co(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s7_SPOC)),(class(X_1,c演员_CLS)).
1.0::(co(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s7_SPOC)).
0.0::(po(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)), \+(spoc(X_2,s2_SPOC)).
1.0::(po(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s2_SPOC)), \+(class(X_2,c父亲母亲_CLS)).
0.0::(po(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s2_SPOC)),(class(X_2,c父亲母亲_CLS)).
0.0::(po(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s7_SPOC)), \+(spoc(X_1,s5_SPOC)).
1.0::(po(X_1,X_2)):- (vob(X_3,X_1)),(sbv(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s7_SPOC)),(spoc(X_1,s5_SPOC)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(class(X_1,c人物_CLS)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_1,c演员_CLS)),(class(X_1,c人物_CLS)), \+(postag(X_1,nr_POS)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(class(X_1,c演员_CLS)),(class(X_1,c人物_CLS)),(postag(X_1,nr_POS)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(class(X_1,c演员_CLS)), \+(spoc(X_2,s2_SPOC)), \+(class(X_2,c主演_CLS)).
0.3333333333333333::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(class(X_1,c演员_CLS)), \+(spoc(X_2,s2_SPOC)),(class(X_2,c主演_CLS)).
0.75::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)), \+(class(X_2,c演过_CLS)).
0.4::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)),(class(X_2,c演过_CLS)).
0.0::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_2,c电视剧_CLS)), \+(spoc(X_1,s2_SPOC)), \+(class(X_1,c主演_CLS)).
0.2::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_2,c电视剧_CLS)), \+(spoc(X_1,s2_SPOC)),(class(X_1,c主演_CLS)).
0.23076923076923078::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_2,c电视剧_CLS)),(spoc(X_1,s2_SPOC)), \+(class(X_1,c开头_CLS)).
0.42857142857142855::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_2,c电视剧_CLS)), \+(spoc(X_1,s7_SPOC)), \+(class(X_1,c演_CLS)).
0.0::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_2,c电视剧_CLS)), \+(spoc(X_1,s7_SPOC)),(class(X_1,c演_CLS)).
0.4375::(pc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_2,c电视剧_CLS)),(spoc(X_1,s7_SPOC)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s7_SPOC)), \+(class(X_2,c影视作品_CLS)).
0.02631578947368421::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s7_SPOC)), \+(postag(X_1,ns_POS)).
0.4::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s7_SPOC)),(postag(X_1,ns_POS)).
0.058823529411764705::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)), \+(postag(X_2,i_POS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_2,i_POS)).
0.375::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c电影_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(class(X_1,c电影_CLS)).
0.0::(op(X_1,X_2)):- (att(X_3,X_1)),(sbv(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(spoc(X_2,s7_SPOC)).
1.0::(op(X_1,X_2)):- (att(X_3,X_1)),(sbv(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s7_SPOC)), \+(class(X_2,c多大_CLS)).
0.0::(op(X_1,X_2)):- (att(X_3,X_1)),(sbv(X_2,X_3)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(spoc(X_2,s7_SPOC)),(class(X_2,c多大_CLS)).
0.01282051282051282::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(postag(X_1,ns_POS)), \+(spoc(X_1,s5_SPOC)), \+(spoc(X_1,s4_SPOC)).
0.3333333333333333::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(postag(X_1,ns_POS)), \+(spoc(X_1,s5_SPOC)),(spoc(X_1,s4_SPOC)).
0.03076923076923077::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)), \+(postag(X_1,ns_POS)),(spoc(X_1,s5_SPOC)), \+(spoc(X_2,s13_SPOC)).
0.125::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(postag(X_1,ns_POS)), \+(spoc(X_2,s8_SPOC)), \+(postag(X_2,n_POS)).
0.36363636363636365::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(postag(X_1,ns_POS)), \+(spoc(X_2,s8_SPOC)),(postag(X_2,n_POS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(==(X_2,X_1)),(postag(X_1,ns_POS)),(spoc(X_2,s8_SPOC)),(class(X_2,c好玩的地方_CLS)).
0.75::(po(X_1,X_2)):- (sbv(X_1,X_2)), \+(==(X_1,X_2)), \+(postag(X_1,v_POS)), \+(spoc(X_1,s5_SPOC)), \+(postag(X_1,a_POS)).
0.0::(po(X_1,X_2)):- (sbv(X_1,X_2)), \+(==(X_1,X_2)), \+(postag(X_1,v_POS)), \+(spoc(X_1,s5_SPOC)),(postag(X_1,a_POS)).
1.0::(po(X_1,X_2)):- (sbv(X_1,X_2)), \+(==(X_1,X_2)), \+(postag(X_1,v_POS)),(spoc(X_1,s5_SPOC)).
0.0::(po(X_1,X_2)):- (sbv(X_1,X_2)), \+(==(X_1,X_2)),(postag(X_1,v_POS)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)), \+(spoc(X_2,s2_SPOC)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s2_SPOC)), \+(class(X_1,c演员_CLS)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)), \+(spoc(X_2,s7_SPOC)),(spoc(X_2,s2_SPOC)),(class(X_1,c演员_CLS)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)), \+(class(X_2,c主演_CLS)), \+(class(X_2,c多大_CLS)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(==(X_2,X_1)),(spoc(X_2,s7_SPOC)),(class(X_2,c主演_CLS)), \+(class(X_1,c电视剧_CLS)).
0.0::(oc(X_1,X_2)):- (sbv(X_3,X_1)),(vob(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)), \+(class(X_2,c谁_CLS)).
1.0::(oc(X_1,X_2)):- (sbv(X_3,X_1)),(vob(X_3,X_2)), \+(==(X_3,X_1)), \+(==(X_3,X_2)), \+(==(X_1,X_2)),(class(X_2,c谁_CLS)), \+(spoc(X_1,s15_SPOC)).
