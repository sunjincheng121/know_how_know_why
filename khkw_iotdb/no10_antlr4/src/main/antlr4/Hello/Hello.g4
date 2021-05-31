// Define a grammar called Hello
grammar Hello;

welcome : 'IoTDB' DESC ;

DESC :  POWER|OPEN|APACHE;

POWER : 'is Powerful' ;
OPEN : 'is Open Source' ;
APACHE : 'is Apache Project' ;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines