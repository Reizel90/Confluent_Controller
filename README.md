# Confluent_Controller

maven project

the objective of the project is to gather kafka connectors and make them easy to deploy via java program.


ProTip: 
to skip manual creation of the entity-object script /n 
file-> new-> module /n
  chk JavaEE Persistance /n 
  chk import database schema /n 
  in Choose Data Source [...] /n 
  create your db_source /n 
  /n 
intellij- View -> Tool Windows -> Database -> select the source just created and look for current database, w8 for tables to be loaded /n 
                select the table you want (right click) -> scripted Extensions -> Generate POJOs.groovy
