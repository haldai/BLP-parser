0.0::(sp(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s2_SPOC)).
0.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)), \+(spoc(X_1,s1_SPOC)).
0.9318181818181818::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)), \+(spoc(X_2,s8_SPOC)), \+(postag(X_2,n_POS)).
0.9935064935064936::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)), \+(spoc(X_2,s8_SPOC)),(postag(X_2,n_POS)).
0.25::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)),(spoc(X_2,s8_SPOC)), \+(class(X_1,c影视作品_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,n_POS)),(spoc(X_2,s8_SPOC)),(class(X_1,c影视作品_CLS)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u演员_WRD)).
0.6666666666666666::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u演员_WRD)).
0.38461538461538464::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)), \+(spoc(X_2,s4_SPOC)).
1.0::(sp(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s2_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)),(spoc(X_2,s4_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)), \+(postag(X_1,n_POS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)), \+(spoc(X_1,s1_SPOC)), \+(spoc(X_2,s16_SPOC)), \+(wrd(X_1,u主演是_WRD)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)), \+(spoc(X_1,s1_SPOC)), \+(spoc(X_2,s16_SPOC)),(wrd(X_1,u主演是_WRD)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)), \+(spoc(X_1,s1_SPOC)),(spoc(X_2,s16_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_2,s8_SPOC)),(postag(X_1,n_POS)),(spoc(X_1,s1_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s1_SPOC)), \+(spoc(X_1,s4_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s1_SPOC)),(spoc(X_1,s4_SPOC)).
0.6060606060606061::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u成语_WRD)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u成语_WRD)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)), \+(postag(X_1,ns_POS)),(class(X_1,c演员_CLS)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)), \+(spoc(X_2,s2_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)),(spoc(X_2,s2_SPOC)), \+(wrd(X_2,u演员_WRD)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s1_SPOC)),(postag(X_1,ns_POS)),(spoc(X_2,s2_SPOC)),(wrd(X_2,u演员_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_2,s2_SPOC)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)), \+(postag(X_1,v_POS)).
0.6666666666666666::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,v_POS)), \+(class(X_1,c词汇_CLS)), \+(wrd(X_2,u影视作品_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,v_POS)), \+(class(X_1,c词汇_CLS)),(wrd(X_2,u影视作品_WRD)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)),(postag(X_1,v_POS)),(class(X_1,c词汇_CLS)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u偶像_WRD)), \+(wrd(X_2,u字_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u偶像_WRD)),(wrd(X_2,u字_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u偶像_WRD)).
1.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)), \+(wrd(X_2,u偶像_WRD)).
0.0::(sp(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(class(X_1,c演员_CLS)),(wrd(X_2,u偶像_WRD)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_2,s8_SPOC)), \+(wrd(X_2,u字_WRD)).
1.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_2,s8_SPOC)),(wrd(X_2,u字_WRD)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)), \+(postag(X_1,v_POS)), \+(postag(X_1,a_POS)), \+(spoc(X_1,s1_SPOC)).
1.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)), \+(postag(X_1,v_POS)), \+(postag(X_1,a_POS)),(spoc(X_1,s1_SPOC)), \+(wrd(X_2,u电影_WRD)).
0.8571428571428571::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)), \+(postag(X_1,v_POS)), \+(postag(X_1,a_POS)),(spoc(X_1,s1_SPOC)),(wrd(X_2,u电影_WRD)).
1.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)), \+(postag(X_1,v_POS)),(postag(X_1,a_POS)), \+(spoc(X_1,s16_SPOC)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)), \+(postag(X_1,v_POS)),(postag(X_1,a_POS)),(spoc(X_1,s16_SPOC)).
1.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)),(postag(X_1,v_POS)), \+(spoc(X_1,s2_SPOC)), \+(wrd(X_2,u词_WRD)), \+(wrd(X_1,u张檬_WRD)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)),(postag(X_1,v_POS)), \+(spoc(X_1,s2_SPOC)), \+(wrd(X_2,u词_WRD)),(wrd(X_1,u张檬_WRD)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)),(postag(X_1,v_POS)), \+(spoc(X_1,s2_SPOC)),(wrd(X_2,u词_WRD)).
0.0::(oc(X_1,X_2)):- (de(X_3,X_1)),(att(X_2,X_3)),(spoc(X_2,s8_SPOC)),(postag(X_1,v_POS)),(spoc(X_1,s2_SPOC)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)), \+(spoc(X_2,s2_SPOC)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,n_POS)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)), \+(wrd(X_1,u杂种狗_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)),(spoc(X_2,s2_SPOC)),(postag(X_1,n_POS)),(wrd(X_1,u杂种狗_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)), \+(postag(X_1,n_POS)), \+(postag(X_1,nr_POS)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)), \+(postag(X_1,n_POS)),(postag(X_1,nr_POS)), \+(wrd(X_1,u吴子龙走吧_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)), \+(postag(X_1,n_POS)),(postag(X_1,nr_POS)),(wrd(X_1,u吴子龙走吧_WRD)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)),(postag(X_1,n_POS)), \+(wrd(X_1,u大片_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)),(postag(X_1,n_POS)),(wrd(X_1,u大片_WRD)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)), \+(wrd(X_1,u周杰伦_WRD)), \+(spoc(X_2,s4_SPOC)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)), \+(wrd(X_1,u周杰伦_WRD)),(spoc(X_2,s4_SPOC)), \+(wrd(X_1,u杨颖_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)), \+(wrd(X_1,u周杰伦_WRD)),(spoc(X_2,s4_SPOC)),(wrd(X_1,u杨颖_WRD)).
0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)), \+(wrd(X_2,u主演_WRD)),(wrd(X_1,u周杰伦_WRD)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)),(wrd(X_2,u主演_WRD)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s4_SPOC)), \+(postag(X_2,n_POS)).
1.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s4_SPOC)),(postag(X_2,n_POS)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s4_SPOC)), \+(postag(X_2,n_POS)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s4_SPOC)),(postag(X_2,n_POS)), \+(class(X_2,c演员_CLS)), \+(spoc(X_1,s2_SPOC)).
0.125::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s4_SPOC)),(postag(X_2,n_POS)), \+(class(X_2,c演员_CLS)),(spoc(X_1,s2_SPOC)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s4_SPOC)),(postag(X_2,n_POS)),(class(X_2,c演员_CLS)).
1.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)),(spoc(X_2,s2_SPOC)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(spoc(X_2,s2_SPOC)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,ns_POS)), \+(class(X_1,c电影_CLS)), \+(spoc(X_2,s4_SPOC)).
0.375::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,ns_POS)), \+(class(X_1,c电影_CLS)),(spoc(X_2,s4_SPOC)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)), \+(postag(X_1,ns_POS)),(class(X_1,c电影_CLS)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(spoc(X_2,s2_SPOC)),(postag(X_1,ns_POS)).
1.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)), \+(postag(X_2,v_POS)).
0.0::(sp(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)),(postag(X_2,v_POS)).
0.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_3,s16_SPOC)), \+(spoc(X_2,s8_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_3,s16_SPOC)),(spoc(X_2,s8_SPOC)), \+(spoc(X_3,s2_SPOC)), \+(postag(X_1,n_POS)).
1.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_3,s16_SPOC)),(spoc(X_2,s8_SPOC)), \+(spoc(X_3,s2_SPOC)),(postag(X_1,n_POS)), \+(spoc(X_2,s2_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_3,s16_SPOC)),(spoc(X_2,s8_SPOC)), \+(spoc(X_3,s2_SPOC)),(postag(X_1,n_POS)),(spoc(X_2,s2_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)), \+(spoc(X_3,s16_SPOC)),(spoc(X_2,s8_SPOC)),(spoc(X_3,s2_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)),(spoc(X_3,s16_SPOC)), \+(postag(X_3,m_POS)).
1.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)),(spoc(X_3,s16_SPOC)),(postag(X_3,m_POS)), \+(wrd(X_2,u别名_WRD)).
0.0::(oc(X_1,X_2)):- (att(X_3,X_1)),(att(X_2,X_3)),(spoc(X_3,s16_SPOC)),(postag(X_3,m_POS)),(wrd(X_2,u别名_WRD)).
0.0012033694344163659::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)), \+(postag(X_2,i_POS)), \+(postag(X_2,v_POS)), \+(class(X_2,c演员_CLS)).
0.09090909090909091::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)), \+(postag(X_2,i_POS)), \+(postag(X_2,v_POS)),(class(X_2,c演员_CLS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)), \+(postag(X_2,i_POS)),(postag(X_2,v_POS)), \+(wrd(X_2,u别名_WRD)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)), \+(postag(X_2,i_POS)),(postag(X_2,v_POS)),(wrd(X_2,u别名_WRD)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)),(postag(X_2,i_POS)), \+(postag(X_1,nr_POS)).
0.14285714285714285::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)),(postag(X_2,i_POS)),(postag(X_1,nr_POS)), \+(class(X_1,c人物_CLS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)), \+(wrd(X_1,u文章_WRD)),(postag(X_2,i_POS)),(postag(X_1,nr_POS)),(class(X_1,c人物_CLS)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)),(wrd(X_1,u文章_WRD)), \+(postag(X_2,l_POS)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)),(wrd(X_1,u文章_WRD)),(postag(X_2,l_POS)), \+(wrd(X_2,u是什么星座_WRD)), \+(wrd(X_2,u是什么座_WRD)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)),(wrd(X_1,u文章_WRD)),(postag(X_2,l_POS)), \+(wrd(X_2,u是什么星座_WRD)),(wrd(X_2,u是什么座_WRD)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)), \+(wrd(X_1,u邓萃雯_WRD)),(wrd(X_1,u文章_WRD)),(postag(X_2,l_POS)),(wrd(X_2,u是什么星座_WRD)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)),(wrd(X_1,u邓萃雯_WRD)), \+(postag(X_2,l_POS)), \+(wrd(X_2,u电视剧_WRD)).
0.0::(op(X_1,X_2)):- (att(X_2,X_1)),(wrd(X_1,u邓萃雯_WRD)), \+(postag(X_2,l_POS)),(wrd(X_2,u电视剧_WRD)).
1.0::(op(X_1,X_2)):- (att(X_2,X_1)),(wrd(X_1,u邓萃雯_WRD)),(postag(X_2,l_POS)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_2)), \+(spoc(X_2,s2_SPOC)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_2)),(spoc(X_2,s2_SPOC)), \+(wrd(X_1,u电视剧_WRD)), \+(spoc(X_1,s8_SPOC)).
1.0::(cp(X_1,X_2)):- (att(X_1,X_2)),(spoc(X_2,s2_SPOC)), \+(wrd(X_1,u电视剧_WRD)),(spoc(X_1,s8_SPOC)).
1.0::(cp(X_1,X_2)):- (att(X_1,X_2)),(spoc(X_2,s2_SPOC)),(wrd(X_1,u电视剧_WRD)).
