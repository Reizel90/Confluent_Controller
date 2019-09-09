# Confluent_Controller

maven project <br />

the objective of the project is to gather kafka connectors and make them easy to deploy via java program. <br />
<br />

ProTip: 
to skip manual creation of the entity-object script <br /> 
file-> new-> module <br />
  chk JavaEE Persistance <br />
  chk import database schema <br />
  in Choose Data Source [...] <br />
  create your db_source <br />
  <br /> 
intellij- View -> Tool Windows -> Database -> select the source just created and look for current database, w8 for tables to be loaded <br /> 
                select the table you want (right click) -> scripted Extensions -> Generate POJOs.groovy


(intellij)
if you want to add javafx for a gui, you need to add into .idea -> compile.xml <br />
\<wildcardResourcePatterns> <br />
          \<entry name="!?*.java" /> <br />
          \<entry name="!?*.form" /> <br />
          \<entry name="!?*.class" /> <br />
          \<entry name="!?*.groovy" /> <br />
          \<entry name="!?*.scala" /> <br />
          \<entry name="!?*.flex" /> <br />
          \<entry name="!?*.kt" /> <br />
          \<entry name="!?*.clj" /> <br />
          \<entry name="!?*.fxml" /> <br />
      \</wildcardResourcePatterns> <br />
