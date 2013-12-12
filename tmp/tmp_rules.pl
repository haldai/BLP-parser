0.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)), \+(class(X_2,c18000_CLS)).
1.0::(op(X_1,X_2)):- (sbv(X_2,X_1)), \+(class(X_1,c演员_CLS)),(class(X_2,c18000_CLS)).
0.8::(op(X_1,X_2)):- (sbv(X_2,X_1)),(class(X_1,c演员_CLS)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_1,s16_SPOC)), \+(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s8_SPOC)).
0.75::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_1,s16_SPOC)), \+(spoc(X_2,s8_SPOC)),(spoc(X_1,s8_SPOC)).
1.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_1,s16_SPOC)),(spoc(X_2,s8_SPOC)), \+(spoc(X_1,s2_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)), \+(spoc(X_1,s16_SPOC)),(spoc(X_2,s8_SPOC)),(spoc(X_1,s2_SPOC)).
0.0::(oc(X_1,X_2)):- (att(X_2,X_1)),(spoc(X_1,s16_SPOC)).
0.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)), \+(spoc(X_2,s8_SPOC)).
0.875::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)), \+(spoc(X_2,s2_SPOC)),(spoc(X_2,s8_SPOC)).
1.0::(cp(X_1,X_2)):- (att(X_1,X_3)),(de(X_3,X_2)),(spoc(X_2,s2_SPOC)).
