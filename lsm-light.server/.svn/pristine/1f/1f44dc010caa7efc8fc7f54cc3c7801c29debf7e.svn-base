% author: Thu-Le Pham
%-----------------------------------------------------
%-----------------------------------------------------
% define capability
capability(talk;listen;read;type;share).

% define factor; (type == chat)
factor(noise;light;proximity;user_activity; user_place; item_sensitive).

%define affect between a factor for each capability: affect(F,C) :- factor(F), capability(C) 
affect(noise, talk).
affect(noise, listen).

affect(light, type).

affect(proximity, type).
affect(proximity, read).

affect(user_activity, type).
affect(user_activity, read).

%define limitValue of each factor: limit_value(factor, limitvalue)
limit_value(noise, off).
limit_value(light, off).
limit_value(proximity,off).
limit_value(user_activity, off).
limit_value(user_place, off).
limit_value(item_sensitive, true).

% rules......
on(U,C) :- user(U), capability(C), not limit(U,C).
off(U,C) :- user(U), capability(C), not on(U,C).

%define when a capability is limited
limit(U, C):- affect(F,C), value(U,F,V), limit_value(F,L), V==L. 
limit(U, C):- value(U, user_place,off), value(U, item_sensitive,true), capability(C), C!= listen. 

 

#show on/2.
#show off/2.

  

