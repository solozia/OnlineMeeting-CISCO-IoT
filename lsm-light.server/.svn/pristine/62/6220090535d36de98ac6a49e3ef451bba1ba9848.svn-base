meeting_time(meeting1,0,60).

agenda_item(item1).
agenda_item(item2).
agenda_item(item3).

agenda_time(item1,0,20).
agenda_time(item2,20,50).
agenda_time(item3,50,60).

agenda_speaker(item1,a).
agenda_speaker(item2,b).
agenda_speaker(item3,c).

agenda_order(item1,1).
agenda_order(item2,2).
agenda_order(item3,3).

order(1..3).

%user(a).
%user(b).
%user(c).
%user(d).

could_not_attend(a,item1).

1{new_agenda_order(Item,Order):agenda_item(Item)}1 :-  order(Order).



:- agenda_speaker(Item,User), could_not_attend(User,Item), agenda_order(Item, Order), new_agenda_order(Item,Order).  


#show new_agenda_order/2.




