# Confluent_Controller

maven project

the objective of the project is to gather kafka connectors and make them easy to deploy via java program.


ProTip: 
to skip manual creation of the entity-object script
file-> new-> module
  chk JavaEE Persistance
  chk import database schema
  in Choose Data Source [...] 
  create your db_source
  
intellij- View -> Tool Windows -> Database -> select the source just created and look for current database, w8 for tables to be loaded
                select the table you want (right click) -> scripted Extensions -> Generate POJOs.groovy
